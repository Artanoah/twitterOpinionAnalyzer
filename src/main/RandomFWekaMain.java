package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import randomForest.ModifiedRandomForest;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class RandomFWekaMain {

	public static void main(String[] args) throws Exception {
		System.out.println("###### TRAININGSSET FERTIGMACHEN ######");
		List<FeatureVector> trainingsSetVectors  = Util.getStemmedPosts("./alleBewertet.txt");


		
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
		Vector<String> fvClassVal = new Vector<String>(3);
		fvClassVal.addElement("pro");
		fvClassVal.addElement("neutral");
		fvClassVal.addElement("contra");
		Attribute klassifizierung = new Attribute("ClassVal", fvClassVal);
		possibleAttributes.add(klassifizierung);
		keyToIndex.put("ClassVal", possibleAttributes.indexOf(klassifizierung));
		//WEKA FeatureVector erstellen
		ArrayList<Attribute> fvWekaAttributes = new ArrayList<Attribute>();
		for(Attribute attribut:possibleAttributes){
			fvWekaAttributes.add(attribut);
		}
		
		//TrainingsSet erstellen
		Instances trainingsSet = new Instances("TrainingsSet",fvWekaAttributes,trainingsSetVectors.size());
		trainingsSet.setClassIndex(keyToIndex.get("ClassVal"));
		for(FeatureVector vector:trainingsSetVectors){
			Instance temp = new SparseInstance(vector.getMap().size()+1);
			for(String key:vector.getMap().keySet()){
				temp.setValue((Attribute) fvWekaAttributes.get(keyToIndex.get(key)), vector.getMap().get(key));
			}
			String valueOfVector = "";
			switch(vector.getValue()){
			case 0:valueOfVector = "neutral";
			case -1:valueOfVector = "contra";
			case 1:valueOfVector = "pro";
			}
			temp.setValue((Attribute) fvWekaAttributes.get(keyToIndex.get("ClassVal")), valueOfVector);
			
			trainingsSet.add(temp);
		}
		
		//Klassifizierer bauen
		ModifiedRandomForest randomForest = new ModifiedRandomForest();
		randomForest.setNumFeatures(trainingsSet.size()/3);
		randomForest.setNumTrees(Constants.AMOUNT_RANDOM_TREES);
		randomForest.buildClassifier(trainingsSet);
		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
		
		//Klassifizierer gegen TrainingsSet probieren
		System.out.println("###### TrainingsSet checken ######");
		Evaluation eval = new Evaluation(trainingsSet);
		eval.evaluateModel(randomForest, trainingsSet);
		System.out.println(eval.toSummaryString());
		
		
		//VergleichsSet zusammenbauen
		System.out.println("###### VergleichsSet gegentesten ######");
		long startClassifyRF = System.currentTimeMillis();
		List<FeatureVector> compareSetVectors = Util.getStemmedPosts("./reddit_testset.txt");
		for(FeatureVector vector:compareSetVectors){
			Instance temp = new SparseInstance(trainingsSet.firstInstance());
			temp.setDataset(trainingsSet);
			for(String key:vector.getMap().keySet()){
				if(keyToIndex.get(key) != null){
					temp.setValue((Attribute) fvWekaAttributes.get(keyToIndex.get(key)), vector.getMap().get(key));
				}
			}
			double[] distribution = randomForest.distributionForInstance(temp);
			System.out.println(vector + " ist zu " + distribution[0] + "% pro, " + distribution[2] + "% contra und " + distribution[1] + "% neutral");
		}
		
		
		System.out.println("RANDOM-FOREST hat bewertet, in " + (System.currentTimeMillis()-startClassifyRF) + "ms.");
	}

}
