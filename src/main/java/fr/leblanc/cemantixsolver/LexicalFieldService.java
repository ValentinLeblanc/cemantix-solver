package fr.leblanc.cemantixsolver;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class LexicalFieldService {

	private static final String LEXICAL_FIELD_URL = "https://www.rimessolides.com/motscles.aspx?m=";

	private boolean isTest;
	
	private RestTemplate restTemplate = new RestTemplate();
	
	private JSONObject lexicalFields = parseLexicalFields();
	
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	public List<String> getLexicalField(String word) {
		
		if (isTest || lexicalFields.has(word)) {
			return new ArrayList<>(lexicalFields.getJSONArray(word).toList().stream().map(Object::toString).toList());
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", "PostmanRuntime/7.36.0");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(LEXICAL_FIELD_URL + encodeWord(word), HttpMethod.GET,
				entity, String.class);

		List<String> lexicalField = extractWords(response.getBody());
		
		lexicalFields.put(word, lexicalField);
		
		return lexicalField;
	}

	private JSONObject parseLexicalFields() {
		JSONObject json = new JSONObject();
        try (InputStream inputStream = new FileInputStream("lexical_fields.json")) {
            json = new JSONObject(new JSONTokener(inputStream));
        } catch (IOException ignored) {
        	// ignore
        }
		return json;
	}

	private String encodeWord(String word) {
		try {
			word = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return word;
	}

	private List<String> extractWords(String htmlContent) {
		List<String> words = new ArrayList<>();

		Document doc = Jsoup.parse(htmlContent);
		
		Elements motcleElements = doc.select(".motcle");
		for (Element element : motcleElements) {
			String text = element.text();
			words.add(text.replace(",", ""));
		}
		
		motcleElements = doc.select(".refm");
		for (Element element : motcleElements) {
			String text = element.text();
			words.add(text.replace(",", ""));
		}
		return words;
	}

	public void storeLexicalFields() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("lexical_fields.json"), StandardCharsets.UTF_8)) {
        	writer.write(lexicalFields.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
