package fr.leblanc.cemantixsolver;

import java.math.BigDecimal;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PedantixScoreService {

	private static final String CEMANTIX_URL = "https://cemantix.certitudes.org/pedantix/score";

	public double getScore(String word, String targetKey, Map<String, Double> scoreCache) { 
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		String url = CEMANTIX_URL;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setOrigin("https://cemantix.certitudes.org");
		headers.set("Referer", "https://cemantix.certitudes.org/pedantix");

		String requestBody = "{\"word\":\"" + word + "\",\"answer\":[\"" + word + "\",\"" + word + "\",\"" + word
				+ "\"]}";

		HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			String responseBody = responseEntity.getBody();
			JSONObject res = new JSONObject(responseBody);
			JSONObject score = res.getJSONObject("score");
			
			Double scoreResult = null;
			
			if (score.has(targetKey) && score.get(targetKey) instanceof String) {
				scoreResult = PedantixSolver.SCORE_TARGET;
			} else {
				if (score.has(targetKey)) {
					scoreResult = ((BigDecimal) score.get(targetKey)).doubleValue();
				}
			}
			if (scoreResult != null) {
				if (!scoreCache.containsKey(word) || scoreResult > scoreCache.get(word)) {
					scoreCache.put(word, scoreResult);
				}
			} else {
				scoreCache.put(word, 0d);
			}
		}
		return scoreCache.get(word);

	}
}
