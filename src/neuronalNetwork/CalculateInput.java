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
		List<FeatureVector> featureVectors = Util.getStemmedPosts("./reddit_testset2.txt");
		
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		listOfAllWords.loadFromFile("listOfAllWords.dump");
		EncogMLP resilient_mlp = new EncogMLP(listOfAllWords);
		//resilient_mlp.loadNetwork("./learned/neuronalesNetzInput_75Percent_reseilientPropagation.eg");
		
		EncogMLP back_mlp = new EncogMLP(listOfAllWords);
		back_mlp.loadNetwork("./learned/neuronalesNetzInput_75Percent_resilientpropagation_moreInput.eg");
		
		featureVectors.stream().forEach(fv -> {
			System.out.println("Map: " + fv);
			//System.out.println("Resilient Ergebnis " + Math.round(resilient_mlp.calculate(fv)));
			System.out.println("Backpropagation Ergebnis " + Math.round(back_mlp.calculate(fv)));
		});
	}
}
