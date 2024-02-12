package fr.leblanc.cemantixsolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PedantixSolver {

	private PedantixScoreService scoreService = new PedantixScoreService();
	private LexicalFieldService lexicalFieldService = new LexicalFieldService();

	public static final double SCORE_TARGET = 1000d;

    private static final List<String> ROOT_WORDS = Arrays.asList(
            "le", "temps", "vie", "espace", "personne", "endroit", "chose", "jour", "nuit", "année", "mois",
            "travail", "étude", "famille", "maison", "ville", "pays", "route", "nature", "air", "eau",
            "terre", "soleil", "lune", "étoile", "mer", "montagne", "forêt", "animal", "plante", "fleur",
            "fruit", "légume", "nourriture", "boisson", "corps", "tête", "main", "pied", "œil", "oreille",
            "bouche", "nez", "cœur", "esprit", "âme", "émotion", "pensée", "santé", "maladie", "médecine",
            "science", "technologie", "art", "musique", "danse", "théâtre", "cinéma", "peinture", "sculpture",
            "architecture", "livre", "poésie", "histoire", "géographie", "mathématiques", "physique", "chimie", "biologie",
            "informatique", "langue", "mot", "phrase", "lettre", "nombre", "couleur", "forme", "idée", "question",
            "problème", "solution", "projet", "trajet", "moyen", "transport", "voiture", "train", "avion", "bateau",
            "vélo", "marche", "course", "jeu", "sport", "loisir", "amour", "amitié", "relation", "mariage",
            "enfant", "parent", "grand-parent", "frère", "sœur", "oncle", "tante", "cousin", "cousine", "ami",
            "voisin", "collègue", "chef", "employé", "client", "argent", "trésor", "achat", "vente", "commerce",
            "entreprise", "travailleur", "école", "étudiant", "professeur", "cours", "examen", "note", "diplôme", "vacances",
            "voyage", "tourisme", "découverte", "aventure", "culture", "religion", "spiritualité", "croyance", "pratique", "rituel",
            "fête", "célébration", "événement", "souvenir", "histoire", "actualité", "information", "communication", "média", "journal",
            "radio", "télévision", "internet", "réseaux sociaux", "technologie", "invention", "découverte", "progrès", "changement", "tradition",
            "mode", "tendance", "style", "beauté", "santé", "bien-être", "alimentation", "exercice", "repos", "sommeil",
            "rêve", "cauchemar", "humour", "rire", "pleur", "émotion", "sentiment", "joie", "peine", "colère",
            "peur", "espoir", "confiance", "doute", "courage", "aventure", "risque", "sécurité", "paix", "guerre",
            "conflit", "solution", "problème", "décision", "choix", "avenir", "passé", "présent", "futur", "sous",
            "et", "ou", "mais", "car", "donc", "ni", "de", "à", "avec", "sans",
            "le", "la", "les", "un", "une", "des", "ce", "cette", "ces", "mon",
            "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses", "notre", "votre",
            "leur", "son", "mais", "et", "ou", "donc", "or", "ni", "car", "que",
            "qui", "quoi", "où", "quand", "comment", "pourquoi", "lequel", "laquelle", "lesquels", "lesquelles",
            "ceux", "celui", "celle", "celles", "ceci", "cela", "ça", "me", "te", "se",
            "nous", "vous", "ils", "elles", "tout", "tous", "toute", "toutes", "quel", "quelle",
            "quels", "quelles", "ce", "cet", "cette", "ces", "aucun", "aucune", "chaque", "plusieurs", "certains",
            "certaines", "autre", "autres", "même", "pareil", "tel", "telle", "tels", "telles", "autant",
            "toutefois", "néanmoins", "cependant", "malgré", "aussi", "donc", "parce que", "puisque", "car", "en effet"
        );
    
	public void solve(String targetKey) {

		Map<String, Double> scoreCache = new HashMap<>();

		double bestScore = Double.NEGATIVE_INFINITY;

		List<String> sample = new ArrayList<>(ROOT_WORDS);

		String bestWord = null;
		
		Iterator<String> it = ROOT_WORDS.iterator();

		while (bestScore != SCORE_TARGET) {
			bestWord = getBestWord(sample, targetKey, scoreCache);
			if (bestWord == null) {
				bestWord = it.next();
			}
			Double score = scoreCache.get(bestWord);
			if (score > 0d && targetKey.equals("0")) {
				System.out.println(targetKey + " => " + bestWord +" => " + score);
			}
			if (score > bestScore) {
				bestScore = score;
			}
			List<String> newSsample = lexicalFieldService.getLexicalField(bestWord);
			if (newSsample.isEmpty()) {
				sample.remove(bestWord);
			} else {
				sample = newSsample;
			}
			if (bestScore == SCORE_TARGET) {
				break;
			}
		}

		System.err.println(targetKey + " => " + bestWord);

	}

	public String getBestWord(List<String> sample, String targetKey, Map<String, Double> scoreCache) {

		Double maxScore = 0d;
		String bestWord = null;

		for (String word : sample) {
			if (!scoreCache.containsKey(word)) {
				double score = scoreService.getScore(word, targetKey, scoreCache);
				if (score > maxScore) {
					maxScore = score;
					bestWord = word;
				}
				scoreCache.put(word, score);
			}
		}
		

		return bestWord;
	}

}
