package main;

import static main.Constants.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import neuronalNetwork.EncogMLP;
import neuronalNetwork.NeurophMLP;
import normalisation.NormaliseAndFilterString;
import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;
import contentSource.RedditPosts;

public class Util {
	static Map<String, Integer> stemmedPostTovalue = null;
	
	public static List<FeatureVector> getStemmedPosts(String fileToLearn) throws IOException, InterruptedException {
		//###### INITIALISIERUNGEN ######
		stemmedPostTovalue = new HashMap<String, Integer>();
		String svm_input = "svm_input";
        BufferedWriter svm_bw = new BufferedWriter(new FileWriter("svm_input"));
        int counter = 0;
		//###### Text zu Bewertung ######
		Map<String, Integer> postToValue = new HashMap<String, Integer>();
		Map<String, Integer> correctedPostToValue = new HashMap<String, Integer>();
		
		//###### Wortlisten ######
		List<String> wortliste = new ArrayList<String>();
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		
		//###### Bag Of Words ######
		Map<Map<String, Integer>, Integer> bagsOfWords = new HashMap<Map<String, Integer>, Integer>();
		List<FeatureVector> listOfFeatureVectors = new ArrayList<FeatureVector>();
		
		//###### Initialisiere Dictionary ######
		String dictionary = "dictionary.txt";
		DictionaryCreator dc;
		SpellingCorrector sc;
		
		new File(dictionary).delete();
		dc = new DictionaryCreator(dictionary);
		sc = new SpellingCorrector(dictionary);
		
		dc.addTextFile(new File("texts/sherlockHolmes_theValleyOfFear.txt"));
		dc.addTextFile(new File("texts/edgarWallace_theAngelOfTerror.txt"));
		dc.addTextFile(new File("texts/history_theProudRebel.txt"));
		dc.addTextFile(new File("texts/listOfAllWords.txt"));
		dc.addTextFile(new File("texts/henryReeve_democracyInAmerica.txt"));
		dc.addTextFile(new File("texts/big.txt"));
		dc.addSmileyFile(new File("texts/smileys.txt"));
		sc.refresh();
		
		//###### Initialisiere neurales Netzwerk ######
		NeurophMLP nmlp = null;
		EncogMLP emlp = null;
		
		//###### Nehme Startzeit ######
		long startZeitStarten = System.currentTimeMillis();
		
		//###### OBJEKTE AUS DER DATENBANK HOLEN (Birger) ######
		//input: ()
		//output: Map<String, Value> -> Map an Text zu Bewertung
		System.out.println("###### OBJEKTE AUS DER DATENBANK HOLEN ######");
		
		postToValue = RedditPosts.getTrainingsSetFromFile(fileToLearn);
		System.out.println("Anzahl der Posts fuer Trainingsfile: " + postToValue.size());
		
		//###### TEXTE VON FEHLERN BEREINIGEN (Steffen) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Korrigierter Text zu Bewertung
		System.out.println("###### TEXTE VON FEHLERN BEREINIGEN ######");
		
		postToValue.forEach((key, value) -> 
			correctedPostToValue.put(sc.correctTweet(key), value)
		);
		
		//###### PART OF SPEECH TAGGING + STEMMING (Fabian) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Getagter Text zu Bewertung
		//output: List<String> -> Liste aller zu benutzenden Woerter
		System.out.println("###### PART OF SPEECH TAGGING + STEMMING ######");
		//Erstellen des Thread-Pools
	    ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
		correctedPostToValue.forEach((key, value) -> {
			executor.submit(new NormaliseAndFilterString(key, value, true, false, UTIL_FLAG));
//						System.out.println(counter);
//						String normalizedString = normalisation.PartOfSpeechAnalysis.normaliseAndFilterString(key, true, false);
//					if(!(normalizedString.equals("")))
//						stemmedPostTovalue.put(normalizedString, value);
			});
		executor.shutdown();
		while(!(executor.isTerminated())){
			Thread.sleep(2000);
		}
		
		BufferedReader stammed_dictionary = new BufferedReader(new FileReader("stammed_dictionary.txt"));
		
		while(stammed_dictionary.ready()){
			String wordToAdd = stammed_dictionary.readLine().trim();
			if((!(wortliste.contains(wordToAdd)) && FeatureVector.seperateWordsOfString(stemmedPostTovalue).contains(wordToAdd))){
				wortliste.add(wordToAdd);
			}
		}
		stammed_dictionary.close();
		
		//###### BAG-OF-WORDS ERSTELLEN (Kai) ######
		//input: Map<String, Value>
		//output: Map<Map<String, Value>, Value> -> Liste an Bags of Words (Map<Map<Wort, Haeufigkeit>, Bewertung>)
		System.out.println("###### PART OF SPEECH TAGGING + STEMMING ######");
		
		stemmedPostTovalue.forEach((key, value) -> 
			bagsOfWords.put(getBagOfWords(key), value)
		);
		
		//###### VOLLSTAENDIGE LISTE ALLER WOERTER ERZEUGEN (Steffen) ######
		//input: List<String>
		//output: ListOfAllWords
		System.out.println("###### VOLLSTAENDIGE LISTE ALLER WOERTER ERZEUGEN ######");
		
		listOfAllWords.addWords(wortliste);
		
		//###### VECTOR OBJEKTE ERSTELLEN (Steffen) ######
		//input: Map<Map<String, Value>, Value> -> Liste an Bags of Words (Map<Map<Wort, Haeufigkeit>, Bewertung>)
		//output: List<FeatureVector> -> Hier sind nun auch nicht vorkommende Woerter in der Map enthalten
		System.out.println("###### VECTOR OBJEKTE ERSTELLEN ######");

		bagsOfWords.forEach((key, value) -> 
			listOfFeatureVectors.add(new FeatureVector(listOfAllWords.createCompleteHash(key), value))
		);
		System.out.println("Anzahl der verwertbaren Featurevektoren: " + listOfFeatureVectors.size());
		
		//###### Nehme Endzeit ######
		long endZeitStarten = System.currentTimeMillis();
		System.out.println("###### System Gestartet - Startdauer " + Math.round((endZeitStarten - startZeitStarten) / 1000) + " Sekunden ######");
		
		return listOfFeatureVectors;
	}
	
	//####### Hilfsmethoden #######
	
		public static void addStemmedPost(String post, int value){
			stemmedPostTovalue.put(post, value);
		}
		private static HashMap<String, Integer> getBagOfWords(String stemmed_strings){
			HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
			String[] splitted_string = stemmed_strings.split(" "); 
			for(String ele : splitted_string ){
				if(bagOfWords.containsKey(ele)){
					Integer count = bagOfWords.get(ele);
					bagOfWords.put(ele, count+1);
				} else bagOfWords.put(ele, 1);
			}
			return bagOfWords;
		}
}