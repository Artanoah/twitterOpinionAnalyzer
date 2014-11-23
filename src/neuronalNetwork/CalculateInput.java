package neuronalNetwork;

import java.io.File;
import java.io.IOException;

import main.ListOfAllWords;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

public class CalculateInput {

	public static void main(String[] args) throws IOException {
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		listOfAllWords.loadFromFile("listOfAllWords.dump");
		EncogMLP mlp = new EncogMLP(listOfAllWords);
		mlp.loadNetwork("./learned/neuronalesNetzInput_75Percent_reseilientPropagation.eg");
	}

}
