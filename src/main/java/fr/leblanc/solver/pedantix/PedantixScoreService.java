package fr.leblanc.solver.pedantix;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import fr.leblanc.solver.ResourceHelper;

public class PedantixScoreService {

    public static final String PEDANTIX_SCORES_JSON = "pedantix-scores.json";

	private static final String CEMANTIX_URL = "https://cemantix.certitudes.org/pedantix/score";

    private JSONObject scores = ResourceHelper.parseJSON(PEDANTIX_SCORES_JSON);

    private String date;
    
	public PedantixScoreService(String date) {
		this.date = date;
	}

	public double getScore(PedantixWord pedantixWord, String rank) { 
		
		synchronized (scores) {
			if (!scores.has(date)) {
				scores.put(date, new JSONObject());
			}
			if (!scores.getJSONObject(date).has(rank)) {
				scores.getJSONObject(date).put(rank, new JSONObject());
			}
			if (scores.getJSONObject(date).getJSONObject(rank).has(pedantixWord.getWord())) {
				try {
					return scores.getJSONObject(date).getJSONObject(rank).getDouble(pedantixWord.getWord());
				} catch (JSONException e) {
					return 0d;
				}
			}
		}
		
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		String url = CEMANTIX_URL;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setOrigin("https://cemantix.certitudes.org");
		headers.set("Referer", "https://cemantix.certitudes.org/pedantix");

		String requestBody = "{\"word\":\"" + pedantixWord + "\",\"answer\":[\"" + pedantixWord + "\",\"" + pedantixWord + "\",\"" + pedantixWord
				+ "\"]}";

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		RestTemplate restTemplate = new RestTemplate();
		
		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
			
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				String responseBody = responseEntity.getBody();
				JSONObject res = new JSONObject(responseBody);
				JSONObject score = res.getJSONObject("score");
				
				Double scoreResult = null;
				
				synchronized (scores) {
					for (String key : score.keySet()) {
						if (score.get(key) instanceof String correctWord) {
							scoreResult = PedantixSolver.TARGET_SCORE;
							if (key.equals(rank)) {
								pedantixWord.setWord(correctWord);
								synchronized (scores) {
									ResourceHelper.storeJSON(scores, PEDANTIX_SCORES_JSON);
								}
							}
						} else {
							scoreResult = ((BigDecimal) score.get(key)).doubleValue();
						}
						if (!scores.getJSONObject(date).has(key)) {
							scores.getJSONObject(date).put(key, new JSONObject());
						}
						scores.getJSONObject(date).getJSONObject(key).put(pedantixWord.getWord(), scoreResult);
					}
					if (!score.has(rank)) {
						scores.getJSONObject(date).getJSONObject(rank).put(pedantixWord.getWord(), 0d);
					}
				}
				
			}
		} catch (RestClientException e) {
			System.err.println("ERROR");
			// nothing
		}

		synchronized (scores) {
			return scores.getJSONObject(date).getJSONObject(rank).getDouble(pedantixWord.getWord());
		}
	}

	public String getRankWord(String rank) {
		
		synchronized (scores) {
			if (!scores.has(date)) {
				scores.put(date, new JSONObject());
			}
			if (!scores.getJSONObject(date).has(rank)) {
				scores.getJSONObject(date).put(rank, new JSONObject());
			}
			
			for (String key : scores.getJSONObject(date).getJSONObject(rank).keySet()) {
				if (scores.getJSONObject(date).getJSONObject(rank).getDouble(key) == 1000d) {
					return key;
				}
			}
		}
		
		return null;
	}

}
