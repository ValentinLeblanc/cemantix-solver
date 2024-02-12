package fr.leblanc.cemantixsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CemantixSolver {

	private CemantixScoreService scoreService = new CemantixScoreService();
	private LexicalFieldService lexicalFieldService = new LexicalFieldService();

	private String date;
	
	private boolean isTest = false;
	
	private static final List<String> ROOT_WORDS = Arrays.asList("objet", "ciel", "terre", "sentiment", "pensée", "travail",
			"espace", "santé", "pays", "triste", "histoire", "humain", "vision", "politique");

	public void setDate(String date) {
		this.date = date;
	}
	
	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}
	
	public String solve() {
		
		scoreService.setTest(isTest);
		lexicalFieldService.setTest(isTest);
		
		Map<String, List<String>> similarWordsMap = new HashMap<>();
		
		Set<String> visitedBestWords = new HashSet<>();
				
		double bestScore = Double.NEGATIVE_INFINITY;

		List<String> lexicalField = new ArrayList<>(ROOT_WORDS);

		String bestWord = null;

		while (bestScore != 1.0) {
			lexicalField.removeIf(visitedBestWords::contains);
			bestWord = getBestWord(lexicalField);
			if (bestWord != null) {
				visitedBestWords.add(bestWord);
				Double score = scoreService.getScore(bestWord, date);
				if (score != null && score > bestScore) {
					bestScore = score;
					if (!isTest) {
						System.err.println(bestWord + " => " + score);
					}
				} else {
					if (!isTest) {
						System.out.println(bestWord + " => " + score);
					}
				}
				lexicalField = lexicalFieldService.getLexicalField(bestWord);
				similarWordsMap.put(bestWord, lexicalField);
				if (bestScore == 1.0) {
					return bestWord;
				}
			}
		}
		
		if (!isTest) {
			lexicalFieldService.storeLexicalFields();
			scoreService.storeScores();
		}

		throw new IllegalStateException("word not found");
	}
	
	private String getBestWord(List<String> sample) {
		Optional<String> max = sample.stream().parallel().max((w1, w2) -> Double.compare(scoreService.getScore(w1, date), scoreService.getScore(w2, date)));
		return max.orElse(null);
	}

}
