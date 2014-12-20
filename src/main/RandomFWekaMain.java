package main;

import randomForest.ModifiedRandomForest;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.util.*;

public class RandomFWekaMain {

	public static void main(String[] args) throws Exception {
		System.out.println("###### TRAININGSSET FERTIGMACHEN ######");
		List<FeatureVector> trainingsSetVectors  = Util.getStemmedPostsAndCreateFiles("./testset.csv");


		
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
		fvClassVal.addElement("contra");
		fvClassVal.addElement("pro");
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
			if(vector.getValue() == 0){
				valueOfVector = "contra";
			} else if(vector.getValue() == 1){
				valueOfVector = "pro";
			}
			temp.setValue((Attribute) fvWekaAttributes.get(keyToIndex.get("ClassVal")), valueOfVector);
			
			trainingsSet.add(temp);
		}
		
		//Klassifizierer bauen
		ModifiedRandomForest randomForest = new ModifiedRandomForest();
		randomForest.setNumFeatures(trainingsSet.size()/8*3);
		randomForest.setNumTrees(Constants.AMOUNT_RANDOM_TREES);
		randomForest.setNumExecutionSlots(3);
		randomForest.buildClassifier(trainingsSet);
		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
		
		//Klassifizierer gegen TrainingsSet probieren
		System.out.println("###### TrainingsSet checken ######");
		Evaluation eval = new Evaluation(trainingsSet);
		eval.evaluateModel(randomForest, trainingsSet);
		System.out.println(eval.toSummaryString());
		// F-Score gegen TrainingsSet auswerten
		Fscore score = new Fscore();
		for(Instance temp:trainingsSet){
			int result = (int) randomForest.classifyInstance(temp);
			if(result == 1){
				if(((int)temp.classValue()) == 1){
					score.incrementTruePositive();
				} else {
					score.incrementFalsePositive();
				}
			} else if(result == 0){
				if(((int)temp.classValue()) == 0){
					score.incrementTruePositive();
				} else {
					score.incrementFalseNegativ();
				}
			}
		}
		System.out.println("FScore Precision: " + score.computePrecision());
		System.out.println("FScore Accuracy: " + score.computeAccuracy());
		System.out.println();
		
		
		//VergleichsSet zusammenbauen
		System.out.println("###### VergleichsSet gegentesten ######");
		long startClassifyRF = System.currentTimeMillis();
		List<FeatureVector> compareSetVectors = Util.getStemmedPostsAndCreateFiles("./reddit_testset.txt");
		for(FeatureVector vector:compareSetVectors){
			Instance temp = new SparseInstance(trainingsSet.firstInstance());
			temp.setDataset(trainingsSet);
			for(String key:vector.getMap().keySet()){
				if(keyToIndex.get(key) != null){
					temp.setValue((Attribute) fvWekaAttributes.get(keyToIndex.get(key)), vector.getMap().get(key));
				}
			}
			double[] distribution = randomForest.distributionForInstance(temp);
			double result = randomForest.classifyInstance(temp);
			System.out.println(vector + " ist zu " + distribution[1] + "% pro, " + distribution[0] + "% contra, also die Klasse " + result);
		}
		
		
		System.out.println("RANDOM-FOREST hat bewertet, in " + (System.currentTimeMillis()-startClassifyRF) + "ms.");
	}

}
