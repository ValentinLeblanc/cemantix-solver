package fr.leblanc.solver.pedantix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.leblanc.solver.LexicalFieldService;

public class PedantixSolver {

	private PedantixScoreService scoreService;
	private LexicalFieldService lexicalFieldService;

	public static final double TARGET_SCORE = 1000d;

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
            "toutefois", "n�anmoins", "cependant", "malgr�", "aussi", "donc", "parce que", "puisque", "car", "en effet", "par", "depuis", "�tre", "avoir"
        );
    
    public PedantixSolver(String date) {
    	this.scoreService = new PedantixScoreService(date);
    	this.lexicalFieldService = new LexicalFieldService();
    }
    
	public String solve(String rank) {

		double bestScore = Double.NEGATIVE_INFINITY;

		List<PedantixWord> sample = new ArrayList<>(ROOT_WORDS.stream().map(PedantixWord::new).toList());

		String closestWord = null;
		
		Iterator<String> it = ROOT_WORDS.iterator();
		
		while (bestScore != TARGET_SCORE && !sample.isEmpty()) {
			
			if (scoreService.getRankWord(rank) != null) {
				return scoreService.getRankWord(rank);
			}
			
			closestWord = getClosestWord(sample, rank);
			if (closestWord == null) {
				closestWord = it.next();
			}
			PedantixWord pedantixWord = new PedantixWord(closestWord);
			Double score = scoreService.getScore(pedantixWord, rank);
			if (score == TARGET_SCORE) {
				return pedantixWord.getWord();
			}
			if (score > bestScore) {
				bestScore = score;
			}
			List<String> newSample = lexicalFieldService.getLexicalField(closestWord);
			if (newSample.isEmpty()) {
				sample.remove(new PedantixWord(closestWord));
			} else {
				sample = new ArrayList<>(newSample.stream().map(PedantixWord::new).toList());
			}
		}

		return "NOT_FOUND";
		
	}	

	public String getClosestWord(List<PedantixWord> sample, String targetKey) {

		Double maxScore = 0d;
		String bestWord = null;

		for (PedantixWord pedantixWord : sample) {
			double score = scoreService.getScore(pedantixWord, targetKey);
			if (score > maxScore) {
				maxScore = score;
				bestWord = pedantixWord.getWord();
			}
			if (score == TARGET_SCORE) {
				break;
			}
		}

		return bestWord;
	}

}
