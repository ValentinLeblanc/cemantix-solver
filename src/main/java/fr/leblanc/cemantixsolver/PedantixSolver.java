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
            "le", "temps", "vie", "espace", "personne", "endroit", "chose", "jour", "nuit", "ann�e", "mois",
            "travail", "�tude", "famille", "maison", "ville", "pays", "route", "nature", "air", "eau",
            "terre", "soleil", "lune", "�toile", "mer", "montagne", "for�t", "animal", "plante", "fleur",
            "fruit", "l�gume", "nourriture", "boisson", "corps", "t�te", "main", "pied", "�il", "oreille",
            "bouche", "nez", "c�ur", "esprit", "�me", "�motion", "pens�e", "sant�", "maladie", "m�decine",
            "science", "technologie", "art", "musique", "danse", "th��tre", "cin�ma", "peinture", "sculpture",
            "architecture", "livre", "po�sie", "histoire", "g�ographie", "math�matiques", "physique", "chimie", "biologie",
            "informatique", "langue", "mot", "phrase", "lettre", "nombre", "couleur", "forme", "id�e", "question",
            "probl�me", "solution", "projet", "trajet", "moyen", "transport", "voiture", "train", "avion", "bateau",
            "v�lo", "marche", "course", "jeu", "sport", "loisir", "amour", "amiti�", "relation", "mariage",
            "enfant", "parent", "grand-parent", "fr�re", "s�ur", "oncle", "tante", "cousin", "cousine", "ami",
            "voisin", "coll�gue", "chef", "employ�", "client", "argent", "tr�sor", "achat", "vente", "commerce",
            "entreprise", "travailleur", "�cole", "�tudiant", "professeur", "cours", "examen", "note", "dipl�me", "vacances",
            "voyage", "tourisme", "d�couverte", "aventure", "culture", "religion", "spiritualit�", "croyance", "pratique", "rituel",
            "f�te", "c�l�bration", "�v�nement", "souvenir", "histoire", "actualit�", "information", "communication", "m�dia", "journal",
            "radio", "t�l�vision", "internet", "r�seaux sociaux", "technologie", "invention", "d�couverte", "progr�s", "changement", "tradition",
            "mode", "tendance", "style", "beaut�", "sant�", "bien-�tre", "alimentation", "exercice", "repos", "sommeil",
            "r�ve", "cauchemar", "humour", "rire", "pleur", "�motion", "sentiment", "joie", "peine", "col�re",
            "peur", "espoir", "confiance", "doute", "courage", "aventure", "risque", "s�curit�", "paix", "guerre",
            "conflit", "solution", "probl�me", "d�cision", "choix", "avenir", "pass�", "pr�sent", "futur", "sous",
            "et", "ou", "mais", "car", "donc", "ni", "de", "�", "avec", "sans",
            "le", "la", "les", "un", "une", "des", "ce", "cette", "ces", "mon",
            "ma", "mes", "ton", "ta", "tes", "son", "sa", "ses", "notre", "votre",
            "leur", "son", "mais", "et", "ou", "donc", "or", "ni", "car", "que",
            "qui", "quoi", "o�", "quand", "comment", "pourquoi", "lequel", "laquelle", "lesquels", "lesquelles",
            "ceux", "celui", "celle", "celles", "ceci", "cela", "�a", "me", "te", "se",
            "nous", "vous", "ils", "elles", "tout", "tous", "toute", "toutes", "quel", "quelle",
            "quels", "quelles", "ce", "cet", "cette", "ces", "aucun", "aucune", "chaque", "plusieurs", "certains",
            "certaines", "autre", "autres", "m�me", "pareil", "tel", "telle", "tels", "telles", "autant",
            "toutefois", "n�anmoins", "cependant", "malgr�", "aussi", "donc", "parce que", "puisque", "car", "en effet"
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
