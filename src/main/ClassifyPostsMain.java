package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import neuronalNetwork.EncogMLP;
import neuronalNetwork.NeurophMLP;
import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;
import contentSource.RedditPosts;
import static main.Constants.*;

public class ClassifyPostsMain {

	/**
	 * <Text, Bewertung> Objekte aus der Datenbank holen. Die Texte werden dann 
	 * zuerst von Fehlern bereinigt (Spelling Correction), mit Part-of-Speech-Tagging 
	 * bearbeitet und dann mit Stemming heruntergebrochen. </br>
	 * Aus diesen "korrigierten" Texten werden "Bag-Of-Words" (Hash-Maps) erstellt
	 * die zusammen mit der dazugehoerigen Bewertung in ein FeatureVector-Objekt geschoben werden.</br>
	 * Mit diesen FeatureVector-Objekten werden dann die jeweiligen Lernverfahren angestossen. 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//###### INITIALISIERUNGEN ######
		String svm_input = "svm_input";
        BufferedWriter svm_bw = new BufferedWriter(new FileWriter("svm_input"));
		//###### Text zu Bewertung ######
		Map<String, Integer> postToValue = new HashMap<String, Integer>();
		Map<String, Integer> correctedPostToValue = new HashMap<String, Integer>();
		Map<String, Integer> stemmedPostTovalue = new HashMap<String, Integer>();
		
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
		
		//###### OBJEKTE AUS DER DATENBANK HOLEN (Birger) ######
		//input: ()
		//output: Map<String, Value> -> Map an Text zu Bewertung
		System.out.println("###### OBJEKTE AUS DER DATENBANK HOLEN ######");
		
		postToValue = RedditPosts.getTrainingsSetFromFile("./reddit.txt");
		
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
		
		correctedPostToValue.forEach((key, value) -> {
				stemmedPostTovalue.put(normalisation.PartOfSpeechAnalysis.normaliseAndFilterString(key, true, false), value);
			});
		
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
			bagsOfWords.put(ProcessDataThread.getBagOfWords(key), value)
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
		
		//###### MLP STEFFEN ######
		System.out.println("###### MLP LERNEN ######");
		/*mlp = new NeurophMLP(listOfAllWords);
		mlp.addVector(listOfFeatureVectors);
		
		mlp.learn();
		mlp.save("mlp.nnet");*/
		
		//emlp = new EncogMLP(listOfAllWords);
		//emlp.addVector(listOfFeatureVectors);
		//emlp.learn();
		
		//###### SVM FABIAN ######
        //Set<String> result = new HashSet<String>();
		
        for(FeatureVector fv : listOfFeatureVectors){
        	if (fv.getValue() > 0){
        		svm_bw.write("+" + Integer.toString(fv.getValue()));
        		
        	}
        	else if (fv.getValue() < 0){
        		svm_bw.write(Integer.toString(fv.getValue()));
        	}
        	else {
        		continue;
        	};
        	int i = 1;
        	Map<String, Integer>fvMap = fv.getMap();
        	Integer sLength = FeatureVector.countAppearingWordsOfVector(fvMap);
        	for(String s : fvMap.keySet()){
        		svm_bw.write(" ");
        		System.out.println("Value: " + Integer.toString(fvMap.get(s)));
        		System.out.println("SizeOfMap " + Integer.toString(sLength));
        		System.out.println("Ergebnis Division" + Float.toString(fvMap.get(s)/sLength));
        		svm_bw.write(i + ":" + fvMap.get(s)/sLength);
        		i = i+1;
        	}
        	svm_bw.write("\n");
        }
    	svm_bw.close();
		
		//###### RANDOM FORRESTER BIRGER ######
		System.out.println("###### RANDOM-FOREST LERNEN ######");
		long startLearningRF = System.currentTimeMillis();
		Dataset trainingsSet = new DefaultDataset();
		Map<String,Integer> keyToIndex = new HashMap<String,Integer>();
		Map<FeatureVector,Instance> vectorToInstance = new HashMap<FeatureVector,Instance>();
		int index = 1;
		for(String key:listOfFeatureVectors.get(0).getMap().keySet()){
			keyToIndex.put(key, index++);
		}
		
		//TrainingsSet zusammenbauen
		for(FeatureVector vector:listOfFeatureVectors){
			Instance temp = new SparseInstance(vector.getMap().size());
			for(String key:vector.getMap().keySet()){
				temp.put(keyToIndex.get(key), (double)vector.getMap().get(key));
			}
			temp.setClassValue(vector.getValue());
			vectorToInstance.put(vector, temp);
			trainingsSet.add(temp);
		}
		
		//Klassifizierer bauen
		RandomForest forest = new RandomForest(AMOUNT_RANDOM_TREES);
		forest.buildClassifier(trainingsSet);
		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
		
		//Klassifizierer testen mit den vorhandenen Trainingsdaten
		long startClassifyRF = System.currentTimeMillis();
		int correctClassified = 0;
		for(FeatureVector key : vectorToInstance.keySet()){
			if(forest.classify(vectorToInstance.get(key)).equals(key.getValue())){
				correctClassified++;
			}
		}
		System.out.println("RANDOM-FOREST hat " + correctClassified + " von " + listOfFeatureVectors.size() + " korrekt bewertet, in " +(System.currentTimeMillis()-startClassifyRF) + "ms.");
		
		//###### SIMPLE BASE KAI ######
	}
}
