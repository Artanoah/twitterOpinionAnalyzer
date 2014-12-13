package neuronalNetwork;

import java.io.File;
import java.io.IOException;
import java.util.List;

import main.FeatureVector;
import main.ListOfAllWords;
import main.Util;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

public class CalculateInput {

	public static void main(String[] args) throws IOException, InterruptedException {
		
//		//############# PREPARATION ###############
//		System.out.println("###### FeatureVectoren einlesen oder erzeugen ######");
//		List<FeatureVector> featureVectors = Util.getStemmedPostsAndCreateFiles("./testset.csv");
//		ListOfAllWords listOfAllWords = new ListOfAllWords();
//		
//		System.out.println("###### MLP LERNEN ######");
//		listOfAllWords.loadFromFile("listOfAllWords.dump");
//		
//		EncogMLP emlp = new EncogMLP(listOfAllWords);
//		
//		System.out.println("###### MLP LERNEN ######");
//		
//		long startZeitMLPLernen = System.currentTimeMillis();
//		
//		listOfAllWords.dumpToFile("listOfAllWords.dump");
//		
//		emlp.addVector(featureVectors);
//		int iterationen = emlp.learnWithErrorBackPropagation();
//		emlp.saveNetwork("neuronalesNetzInput_75Percent_resilientpropagation_twitterInput.eg");
//		
//		long endZeitMLPLernen = System.currentTimeMillis();
//		
//		System.out.println("###### MLP Angelernt ######");
//		System.out.println("Lerndauer: " + Math.round((endZeitMLPLernen - startZeitMLPLernen) / 1000) + " Sekunden");
//		System.out.println("Anzahl der Iterationen: " + iterationen);
		
		
		//############# CALCULATION ################
		
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		listOfAllWords.loadFromFile("listOfAllWords.dump");
		
		//EncogMLP resilient_mlp = new EncogMLP(listOfAllWords);
		//resilient_mlp.loadNetwork("./learned/neuronalesNetzInput_75Percent_reseilientPropagation.eg");
		
		List<FeatureVector> featureVectors = Util.getStemmedPostsDontCreateFiles("./twitterTestData.csv");
		
		EncogMLP back_mlp = new EncogMLP(listOfAllWords);
		back_mlp.loadNetwork("./neuronalesNetzInput_75Percent_resilientpropagation_twitterInput.eg");
		
		featureVectors.stream().forEach(fv -> {
			System.out.println("Map: " + fv);
			//System.out.println("Resilient Ergebnis " + Math.round(resilient_mlp.calculate(fv)));
			System.out.println("Backpropagation Ergebnis " + Math.round(back_mlp.calculate(fv)));
			System.out.println();
		});
	}
}
