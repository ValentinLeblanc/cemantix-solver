package fr.leblanc.cemantixsolver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONObject;

public class ScoreService {
	
    private static final String CEMANTIX_URL = "https://cemantix.certitudes.org/score";

	public double getScore(String word, Map<String, Double> scoreCache) {
    	
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
			scoreCache.put(word, score);
			
			return score;
			
		} catch (Exception e) {
			return Double.NEGATIVE_INFINITY;
		}
        
    }
}
