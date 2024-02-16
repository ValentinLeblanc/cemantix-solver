package fr.leblanc.cemantixsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CemantixSolver {

	private static final double TARGET_SCORE = 1.0;
	private CemantixScoreService scoreService;
	private LexicalFieldService lexicalFieldService;

	private static final List<String> ROOT_WORDS = Arrays.asList("objet", "ciel", "terre", "sentiment", "pensée", "travail",
			"espace", "santé", "pays", "triste", "histoire", "humain", "vision", "politique");

	public CemantixSolver(String date) {
		this.scoreService = new CemantixScoreService(date);
		this.lexicalFieldService = new LexicalFieldService();
	}

	public String solve() {
		
		double currentBestScore = Double.NEGATIVE_INFINITY;
		Set<String> visitedWords = new HashSet<>();
		List<String> nextWords = new ArrayList<>(ROOT_WORDS);

		String bestWord = null;

		while (currentBestScore != TARGET_SCORE) {
			nextWords.removeIf(visitedWords::contains);
			bestWord = getBestWord(nextWords);
			if (bestWord != null) {
				visitedWords.add(bestWord);
				Double score = scoreService.getScore(bestWord);
				if (score != null) {
					if (score == TARGET_SCORE) {
						return bestWord;
					}
					if (score > currentBestScore) {
						currentBestScore = score;
					}
				}
				nextWords = lexicalFieldService.getLexicalField(bestWord);
			}
		}
		
		throw new IllegalStateException("word not found");
	}
	
	private String getBestWord(List<String> sample) {
		Optional<String> max = sample.stream().parallel().max((w1, w2) -> Double.compare(scoreService.getScore(w1), scoreService.getScore(w2)));
		return max.orElse(null);
	}

	public void storeResults() {
		lexicalFieldService.storeLexicalFields();
		scoreService.storeScores();
	}

}
