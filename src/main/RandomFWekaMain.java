package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import static main.Constants.*;

public class RandomFWekaMain {

	public static void main(String[] args) throws Exception {
		System.out.println("###### TRAININGSSET FERTIGMACHEN ######");
		File stemmedPostFile = new File("./stemmedPosts.dump");
		List<FeatureVector> trainingsSetVectors  = null;
		if(!stemmedPostFile.exists()){
			trainingsSetVectors = Util.getStemmedPosts("./alleBewertet.txt");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stemmedPostFile));
			oos.writeObject(trainingsSetVectors);
		} else {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stemmedPostFile));
			trainingsSetVectors = (List<FeatureVector>) ois.readObject();
		}

		
		System.out.println("###### RANDOM-FOREST LERNEN ######");
		long startLearningRF = System.currentTimeMillis();
		Map<String,Integer> keyToIndex = new HashMap<String,Integer>();
		
		//TrainingsSet Vorbereitung
		//WEKA FeatureVector Vorbereitung
		List<Attribute> possibleAttributes = new ArrayList<Attribute>();
		for(String key:trainingsSetVectors.get(0).getMap().keySet()){
			Attribute temp = new Attribute(key);
			possibleAttributes.add(temp);
			keyToIndex.put(key, possibleAttributes.indexOf(temp));
		}
		FastVector fvClassVal = new FastVector(3);
		fvClassVal.addElement("pro");
		fvClassVal.addElement("contra");
		fvClassVal.addElement("neutral");
		Attribute klassifizierung = new Attribute("ClassVal", fvClassVal);
		possibleAttributes.add(klassifizierung);
		keyToIndex.put("ClassVal", possibleAttributes.indexOf(klassifizierung));
		//WEKA FeatureVector erstellen
		FastVector fvWekaAttributes = new FastVector(possibleAttributes.size());
		for(Attribute attribut:possibleAttributes){
			fvWekaAttributes.addElement(attribut);
		}
		
		//TrainingsSet erstellen
		Instances trainingsSet = new Instances("TrainingsSet",fvWekaAttributes,trainingsSetVectors.size());
		trainingsSet.setClassIndex(keyToIndex.get("ClassVal"));
		for(FeatureVector vector:trainingsSetVectors){
			Instance temp = new Instance(vector.getMap().size()+1);
			for(String key:vector.getMap().keySet()){
				temp.setValue((Attribute) fvWekaAttributes.elementAt(keyToIndex.get(key)), vector.getMap().get(key));
			}
			String valueOfVector = "";
			switch(vector.getValue()){
			case 0:valueOfVector = "neutral";
			case -1:valueOfVector = "contra";
			case 1:valueOfVector = "pro";
			}
			temp.setValue((Attribute) fvWekaAttributes.elementAt(keyToIndex.get("ClassVal")), valueOfVector);
			
			trainingsSet.add(temp);
		}
		
		//Klassifizierer bauen
		Classifier randomForest = new RandomForest();
		randomForest.buildClassifier(trainingsSet);
		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
		
		//Klassifizierer gegen TrainingsSet probieren
		System.out.println("###### TrainingsSet checken ######");
		Evaluation eval = new Evaluation(trainingsSet);
		eval.evaluateModel(randomForest, trainingsSet);
		System.out.println(eval.toSummaryString());
		
		
		//VergleichsSet zusammenbauen
		System.out.println("###### VergleichsSet gegentesten ######");
		List<FeatureVector> compareSetVectors = Util.getStemmedPosts("./reddit_testset.txt");
		for(FeatureVector vector:compareSetVectors){
			Instance temp = new Instance(vector.getMap().size());
			for(String key:vector.getMap().keySet()){
				temp.setValue((Attribute) fvWekaAttributes.elementAt(keyToIndex.get(key)), vector.getMap().get(key));
			}
			double[] distribution = randomForest.distributionForInstance(temp);
			
			System.out.println(vector + " ist zu " + distribution[0] + "% pro, " + distribution[1] + "% contra und " + distribution[2] + "% neutral");
		}
		
		long startClassifyRF = System.currentTimeMillis();
		System.out.println("RANDOM-FOREST hat bewertet, in " + (System.currentTimeMillis()-startClassifyRF) + "ms.");
		
	}

}
