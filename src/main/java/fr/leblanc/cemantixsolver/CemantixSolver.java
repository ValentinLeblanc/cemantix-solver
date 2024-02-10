package fr.leblanc.cemantixsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CemantixSolver {

	private Map<String, Double> scoreCache = new HashMap<>();

	private CemantixScoreService scoreService = new CemantixScoreService();
	private LexicalFieldService lexicalFieldService = new LexicalFieldService();

	private static final List<String> ROOT_WORDS = Arrays.asList("objet", "ciel", "terre", "sentiment", "pensée", "travail",
			"espace", "santé", "pays", "triste", "histoire", "humain", "vision", "politique");

	public void solve() {

		double bestScore = Double.NEGATIVE_INFINITY;

		List<String> sample = new ArrayList<>(ROOT_WORDS);

		String bestWord = null;

		while (bestScore != 1.0) {
			bestWord = getBestWord(sample);
			if (bestWord != null) {
				Double score = scoreCache.get(bestWord);
				if (score != null && score > bestScore) {
					bestScore = score;
					System.err.println(bestWord + " => " + score);
				} else {
					System.out.println(bestWord + " => " + score);
				}
				List<String> newSsample = lexicalFieldService.getSimilarWords(bestWord);
				if (newSsample.isEmpty()) {
					sample.remove(bestWord);
				} else {
					sample = newSsample;
				}
				if (bestScore == 1.0) {
					break;
				}
			}
		}

	}

	public String getBestWord(List<String> sample) {

		Optional<String> max = sample.stream().parallel().filter(w -> !scoreCache.containsKey(w)).max((w1, w2) -> Double
				.compare(scoreService.getScore(w1, scoreCache), scoreService.getScore(w2, scoreCache)));

		if (max.isPresent()) {
			return max.get();
		}

		return null;
	}

}
