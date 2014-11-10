package neuronalNetwork;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

public class TestMain {
	
	public static void main(String[] args) {
		
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(2, 5, 3, 1);
		
		DataSet trainingSet = new DataSet(2, 1);

		trainingSet.addRow (new DataSetRow (new double[]{0, 0}, new double[]{0}));
		trainingSet.addRow (new DataSetRow (new double[]{0, 1}, new double[]{1}));
		trainingSet.addRow (new DataSetRow (new double[]{1, 0}, new double[]{1}));
		trainingSet.addRow (new DataSetRow (new double[]{1, 1}, new double[]{0}));
		
		mlp.learn(trainingSet);
		
		mlp.save("mlp.nnet");
		
		mlp.setInput(1,1);
		
		mlp.calculate();
		
		System.out.println(mlp.getOutput()[0]);
		
	}
}
