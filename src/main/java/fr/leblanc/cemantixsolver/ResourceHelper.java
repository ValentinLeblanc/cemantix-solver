package fr.leblanc.cemantixsolver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ResourceHelper {
	
	private ResourceHelper() {
		
	}

	public static JSONObject parseJSON(String fileName) {
		JSONObject json = new JSONObject();
		try (InputStream inputStream = new FileInputStream(fileName)) {
			json = new JSONObject(new JSONTokener(inputStream));
		} catch (IOException ignored) {
			// ignore
		}
		return json;
	}
	
	public static void storeJSON(JSONObject json, String fileName) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
        	writer.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
