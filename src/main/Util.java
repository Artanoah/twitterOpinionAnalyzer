package main;

import contentSource.CSVFile;
import contentSource.RedditPosts;
import neuronalNetwork.EncogMLP;
import neuronalNetwork.NeurophMLP;
import normalisation.NormaliseAndFilterString;
import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.Constants.MAX_THREADS;
import static main.Constants.UTIL_FLAG;

public class Util {
	static Map<String, Integer> stemmedPostTovalue = null;

	/**
	 * Get Stemmed Posts from a .csv-file or a .txt-file filled with json-Strings. When done it will create a dump-file which will be loaded next time using this function with the same fileToLearn.
	 * @param fileToLearn absolute or relative path to the file
	 * @return List with all FeatureVectors from the given file. Each FeatureVector contains the same words.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<FeatureVector> getStemmedPostsAndCreateFiles(String fileToLearn) throws IOException, InterruptedException {
		return getStemmedPosts(fileToLearn,true);
	}


	/**
	 * Get Stemmed Posts from a .csv-file or a .txt-file filled with json-Strings.
	 * @param fileToLearn absolute or relative path to the file
	 * @return List with all FeatureVectors from the given file. Each FeatureVector contains the same words.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<FeatureVector> getStemmedPostsDontCreateFiles(String fileToLearn) throws IOException, InterruptedException {
		return getStemmedPosts(fileToLearn,false);
	}

	/**
	 * Get Stemmed Posts from a .csv-file or a .txt-file filled with json-Strings. When createFiles is set, it will create a dump-file which will be loaded next time using this function with the same fileToLearn.
	 * @param fileToLearn absolute or relative path to the file
	 * @param createFiles shall a dump-file be created and used?
	 * @return List with all FeatureVectors from the given file. Each FeatureVector contains the same words.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static List<FeatureVector> getStemmedPosts(String fileToLearn,boolean createFiles) throws IOException, InterruptedException {
		String[] fileToLearnParts = fileToLearn.split("/");
		File stemmedPostFile = new File("./" + fileToLearnParts[fileToLearnParts.length-1] + ".dump");
		List<FeatureVector> listOfFeatureVectors = new ArrayList<FeatureVector>();
		if(!stemmedPostFile.exists() || !createFiles){
			//###### INITIALISIERUNGEN ######
			stemmedPostTovalue = new HashMap<String, Integer>();
			//###### Text zu Bewertung ######
			Map<String, Integer> postToValue = new HashMap<String, Integer>();
			Map<String, Integer> correctedPostToValue = new HashMap<String, Integer>();

			//###### Wortlisten ######
			List<String> wortliste = new ArrayList<String>();
			ListOfAllWords listOfAllWords = new ListOfAllWords();

			//###### Bag Of Words ######
			Map<Map<String, Integer>, Integer> bagsOfWords = new HashMap<Map<String, Integer>, Integer>();

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

			if(fileToLearn.contains("csv")){
				CSVFile traingsFile = new CSVFile(fileToLearn);
				postToValue = traingsFile.getPostsFromFile();
			} else {
				postToValue = RedditPosts.getTrainingsSetFromFile(fileToLearn);
			}

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

			for(Map<String,Integer> key:bagsOfWords.keySet()){
				listOfFeatureVectors.add(new FeatureVector(listOfAllWords.createCompleteHash(key), bagsOfWords.get(key)));
			}

			System.out.println("Anzahl der verwertbaren Featurevektoren: " + listOfFeatureVectors.size());

			//###### Nehme Endzeit ######
			long endZeitStarten = System.currentTimeMillis();
			System.out.println("###### System Gestartet - Startdauer " + Math.round((endZeitStarten - startZeitStarten) / 1000) + " Sekunden ######");

			if(createFiles){
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stemmedPostFile));
				oos.writeObject(listOfFeatureVectors);
				oos.close();
				listOfAllWords.dumpToFile("listOfAllWords.dump");
			}
		} else {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stemmedPostFile));
			try {
				listOfFeatureVectors = (List<FeatureVector>) ois.readObject();
			} catch (ClassNotFoundException e) {
				System.err.println("Geht nicht, weil gibt's nicht.");
			} finally {
				ois.close();
			}
		}
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
