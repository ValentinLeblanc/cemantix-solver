package fr.leblanc.cemantixsolver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

public class CemantixScoreService {
	
    public static final String SCORES_JSON = "scores.json";

	private static final String CEMANTIX_URL = "https://cemantix.certitudes.org/score";
    
    private JSONObject scores = ResourceHelper.parseJSON(SCORES_JSON);
    
    private String date;
    
	public CemantixScoreService(String date) {
		this.date = date;
	}

	public double getScore(String word) {
    	
		if (!scores.has(date)) {
			scores.put(date, new JSONObject());
		}		
		
		if (scores.getJSONObject(date).has(word)) {
			try {
				return scores.getJSONObject(date).getDouble(word);
			} catch (JSONException e) {
				return 0d;
			}
		}
		
		try {
			System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
			
			String url = CEMANTIX_URL;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Origin", "https://cemantix.certitudes.org");
			con.setDoOutput(true);
			con.setDoInput(true);
			
			String requestBody = "word=" + word;
			byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
			
			con.setRequestProperty("Content-Length", Integer.toString(postData.length));
			
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
				wr.write(postData);
			}
			
			StringBuilder response;
			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String inputLine;
				response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			}
			
			JSONObject res = new JSONObject(response.toString());
			
			double score = res.getDouble("score");
			
			scores.getJSONObject(date).put(word, score);
			
			return score;
			
		} catch (Exception e) {
			return Double.NEGATIVE_INFINITY;
		}
        
    }

	public void storeScores() {
		ResourceHelper.storeJSON(scores, SCORES_JSON);
	}

}
