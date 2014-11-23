package neuronalNetwork;

import java.io.File;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

public class CalculateInput {

	public static void main(String[] args) {
		BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("./neuronalesNetzInput_75Percent_reseilientPropagation.eg"));
	}

}
