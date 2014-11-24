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
		List<FeatureVector> featureVectors = Util.getStemmedPosts("./reddit_testset.txt");
		
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		listOfAllWords.loadFromFile("listOfAllWords.dump");
		EncogMLP mlp = new EncogMLP(listOfAllWords);
		mlp.loadNetwork("./learned/neuronalesNetzInput_75Percent_reseilientPropagation.eg");
		
		featureVectors.stream().forEach(fv -> {
			System.out.println("Map: " + fv.getMap());
			System.out.println("Ergebnis " + mlp.calculate(fv)[0]);
		});
	}

}
