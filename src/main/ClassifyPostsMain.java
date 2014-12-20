package main;

import contentSource.CSVFile;
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

import static main.Constants.CLASSIFY_POST_MAIN_FLAG;
import static main.Constants.MAX_THREADS;


public class ClassifyPostsMain {

	/**
	 * <Text, Bewertung> Objekte aus der Datenbank holen. Die Texte werden dann 
	 * zuerst von Fehlern bereinigt (Spelling Correction), mit Part-of-Speech-Tagging 
	 * bearbeitet und dann mit Stemming heruntergebrochen. </br>
	 * Aus diesen "korrigierten" Texten werden "Bag-Of-Words" (Hash-Maps) erstellt
	 * die zusammen mit der dazugehoerigen Bewertung in ein FeatureVector-Objekt geschoben werden.</br>
	 * Mit diesen FeatureVector-Objekten werden dann die jeweiligen Lernverfahren angestossen. 
	 * @param args

	 * @throws IOException 
	 * @throws ClassifierException 
	 */
	
	static Map<String, Integer> stemmedPostTovalue = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws Exception {

		//###### INITIALISIERUNGEN ######
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
		
		CSVFile traingsFile = new CSVFile("./testset.csv");
		postToValue = traingsFile.getPostsFromFile();
		//postToValue = RedditPosts.getTrainingsSetFromFile("./alleBewertet.txt");
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
			executor.submit(new NormaliseAndFilterString(key, value, true, false, CLASSIFY_POST_MAIN_FLAG));
//				System.out.println(counter);
//				String normalizedString = normalisation.PartOfSpeechAnalysis.normaliseAndFilterString(key, true, false);
//			if(!(normalizedString.equals("")))
//				stemmedPostTovalue.put(normalizedString, value);
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
		
		listOfAllWords.dumpToFile("listOfAllWords.dump");
		
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
		
		//###### MLP STEFFEN ######
		System.out.println("###### MLP LERNEN ######");
		
		long startZeitMLPLernen = System.currentTimeMillis();

//		mlp = new NeurophMLP(listOfAllWords);
//		mlp.addVector(listOfFeatureVectors);
//		
//		mlp.learn();
//		mlp.save("mlp.nnet");
		
		listOfAllWords.dumpToFile("listOfAllWords.dump");
		
		emlp = new EncogMLP(listOfAllWords);
		emlp.addVector(listOfFeatureVectors);
		int iterationen = emlp.learnWithErrorBackPropagation();
		emlp.saveNetwork("neuronalesNetzInput_75Percent_resilientpropagation_moreInput.eg");
		
		long endZeitMLPLernen = System.currentTimeMillis();
		
		System.out.println("###### MLP Angelernt ######");
		System.out.println("Lerndauer: " + Math.round((endZeitMLPLernen - startZeitMLPLernen) / 1000) + " Sekunden");
		System.out.println("Anzahl der Iterationen: " + iterationen);
//		
		//###### SVM FABIAN ######
        //Set<String> result = new HashSet<String>();
		
		//System.out.println(listOfFeatureVectors);
//        for(FeatureVector fv : listOfFeatureVectors){
//        	//System.out.println(fv);
//        	if (fv.getValue() > 0){
//        		svm_bw.write("+" + Integer.toString(fv.getValue()));
//        		
//        	}
//        	else if (fv.getValue() < 0){
//        		svm_bw.write(Integer.toString(fv.getValue()));
//        	}
//        	else {
//        		continue;
//        	};
//        	int i = 1;
//        	Map<String, Integer>fvMap = fv.getMap();
//        	Integer sLength = FeatureVector.countAppearingWordsOfVector(fvMap);
//        	for(String s : fvMap.keySet()){
//        		svm_bw.write(" ");
//        		//System.out.println("Value: " + Integer.toString(fvMap.get(s)));
//        		//System.out.println("SizeOfMap " + Integer.toString(sLength));
//        		//System.out.println("Ergebnis Division" + Float.toString((float)fvMap.get(s)/(float)sLength));
//        		svm_bw.write(i + ":" + (float)fvMap.get(s)/(float)sLength);
//        		i = i+1;
//        	}
//        	svm_bw.write("\n");
//        }
//    	svm_bw.close();
		
		//###### RANDOM FORRESTER BIRGER ######

		
		//###### NAIVE BAYES KAI ######
	
//		NaiveBayes nb = new NaiveBayes();
//		
//		System.out.println(stemmedPostTovalue.toString());
//		Map<String, String[]> bayesTrainer = new HashMap<String, String[]>();
//		 
//		Iterator it = stemmedPostTovalue.entrySet().iterator();
//		    while (it.hasNext()) {
//		       
//		    	Map.Entry pairs = (Map.Entry)it.next();
//		        
//		        if((String) pairs.getKey()!=""){
//		        //System.out.println(pairs.getValue().toString()+"value");
//		        //geht!!!
//		        String key = (String) pairs.getValue().toString();
//		        //System.out.println(pairs.getKey().toString()+"key");
//		        String values = (String) pairs.getValue().toString();
//		        
//		        //System.out.println(key);
//		        System.out.println(values);
//		        //bayesTrainer.put(key, values);
//		        }
//		        it.remove(); // avoids a ConcurrentModificationException
//		    }
//		 
		

		    //nb.setChisquareCriticalValue(6.63); //0.01 pvalue
		    //nb.train(bayesTrainer);
//	
//		

//		System.out.println("###### RANDOM-FOREST LERNEN ######");
//		long startLearningRF = System.currentTimeMillis();
//		Dataset trainingsSet = new DefaultDataset();
//		Map<String,Integer> keyToIndex = new HashMap<String,Integer>();
//		Map<FeatureVector,Instance> vectorToInstance = new HashMap<FeatureVector,Instance>();
//		int index = 1;
//		for(String key:listOfFeatureVectors.get(0).getMap().keySet()){
//			keyToIndex.put(key, index++);
//		}
//		
//		//TrainingsSet zusammenbauen
//		for(FeatureVector vector:listOfFeatureVectors){
//			Instance temp = new SparseInstance(vector.getMap().size());
//			for(String key:vector.getMap().keySet()){
//				temp.put(keyToIndex.get(key), (double)vector.getMap().get(key));
//			}
//			temp.setClassValue(vector.getValue());
//			vectorToInstance.put(vector, temp);
//			trainingsSet.add(temp);
//		}
//		
//		//Klassifizierer bauen
//		RandomForest forest = new RandomForest(AMOUNT_RANDOM_TREES);
//		forest.buildClassifier(trainingsSet);
//		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
//		
//		//Klassifizierer testen mit den vorhandenen Trainingsdaten
//		long startClassifyRF = System.currentTimeMillis();
//		int correctClassified = 0;
//		for(FeatureVector key : vectorToInstance.keySet()){
//			if(forest.classify(vectorToInstance.get(key)).equals(key.getValue())){
//				correctClassified++;
//			}
//		}
//		System.out.println("RANDOM-FOREST hat " + correctClassified + " von " + listOfFeatureVectors.size() + " korrekt bewertet, in " +(System.currentTimeMillis()-startClassifyRF) + "ms.");
//		
		//###### SIMPLE BASE KAI ######

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
