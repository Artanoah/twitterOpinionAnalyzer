package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.javaml.classification.tree.RandomForest;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import static main.Constants.*;

public class RandomFMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("###### TRAININGSSET FERTIGMACHEN ######");
		List<FeatureVector> trainingsSetVectors = Util.getStemmedPosts("./alleBewertet.txt");

		
		System.out.println("###### RANDOM-FOREST LERNEN ######");
		long startLearningRF = System.currentTimeMillis();
		Dataset trainingsSet = new DefaultDataset();
		Map<String,Integer> keyToIndex = new HashMap<String,Integer>();
		int index = 1;
		for(String key:trainingsSetVectors.get(0).getMap().keySet()){
			keyToIndex.put(key, index++);
		}
		
		//TrainingsSet zusammenbauen
		for(FeatureVector vector:trainingsSetVectors){
			Instance temp = new SparseInstance(vector.getMap().size());
			for(String key:vector.getMap().keySet()){
				temp.put(keyToIndex.get(key), (double)vector.getMap().get(key));
			}
			temp.setClassValue(vector.getValue());
			trainingsSet.add(temp);
		}
		
		//Klassifizierer bauen
		RandomForest forest = new RandomForest(AMOUNT_RANDOM_TREES);
		forest.buildClassifier(trainingsSet);
		System.out.println("Benoetigte Zeit zum RandomForest lernen: " + (System.currentTimeMillis()-startLearningRF) + "ms.");
		
		
		
		//VergleichsSet zusammenbauen
		List<FeatureVector> compareSetVectors = Util.getStemmedPosts("./reddit_testset.txt");
		Map<FeatureVector,Instance> vectorToInstance = new HashMap<FeatureVector,Instance>();
		Dataset compareSetInstances = new DefaultDataset();
		for(FeatureVector vector:compareSetVectors){
			Instance temp = new SparseInstance(vector.getMap().size());
			for(String key:vector.getMap().keySet()){
				temp.put(keyToIndex.get(key), (double)vector.getMap().get(key));
			}
			temp.setClassValue(vector.getValue());
			vectorToInstance.put(vector, temp);
			compareSetInstances.add(temp);
		}
		
		long startClassifyRF = System.currentTimeMillis();
		for(FeatureVector key : vectorToInstance.keySet()){
			System.out.println("## POST ##");
			System.out.println(key.getMap());
			System.out.println("Bewertung: " + forest.classify(vectorToInstance.get(key)));
			System.out.println();
		}
		System.out.println("RANDOM-FOREST hat bewertet, in " + (System.currentTimeMillis()-startClassifyRF) + "ms.");
		
	}

}
