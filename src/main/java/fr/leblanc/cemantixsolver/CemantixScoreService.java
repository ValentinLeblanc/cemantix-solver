package fr.leblanc.cemantixsolver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CemantixScoreService {
	
    private static final String CEMANTIX_URL = "https://cemantix.certitudes.org/score";
    
    private boolean isTest;
    
    private JSONObject scores = parseScores();
    
	public double getScore(String word, String date) {
    	
		if (!scores.has(date)) {
			scores.put(date, new JSONObject());
		}		
		
		if (isTest || scores.getJSONObject(date).has(word)) {
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

	private JSONObject parseScores() {
		JSONObject json = new JSONObject();
		try (InputStream inputStream = new FileInputStream("scores.json")) {
			json = new JSONObject(new JSONTokener(inputStream));
		} catch (IOException ignored) {
			// ignore
		}
		return json;
	}

	public void storeScores() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("scores.json"), StandardCharsets.UTF_8)) {
        	writer.write(scores.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
}
