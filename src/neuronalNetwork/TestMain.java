package neuronalNetwork;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

public class TestMain {
	
	public static void main(String[] args) {
		//NeuralNetwork neuralNetwork = new Perceptron(2, 1);
		//neuralNetwork.addLayer(new Layer(3));
		
		MultiLayerPerceptron mlp = new MultiLayerPerceptron(2, 5, 3, 1);
		
		// create training set
		DataSet trainingSet = new DataSet(2, 1);
		// add training data to training set (logical OR function)
		trainingSet.addRow (new DataSetRow (new double[]{0, 0}, new double[]{0}));
		trainingSet.addRow (new DataSetRow (new double[]{0, 1}, new double[]{1}));
		trainingSet.addRow (new DataSetRow (new double[]{1, 0}, new double[]{1}));
		trainingSet.addRow (new DataSetRow (new double[]{1, 1}, new double[]{1}));
		
		mlp.learn(trainingSet);
		
		mlp.save("or_mlp.nnet");
		
		mlp.setInput(0,0);
		
		mlp.calculate();
		
		System.out.println(mlp.getOutput()[0]);
		
//		neuralNetwork.learn(trainingSet);
//		
//		neuralNetwork.save("learned/or_perceptron.nnet");
//		
//		neuralNetwork.setInput(0,0);
//		
//		neuralNetwork.calculate();
		//
//		System.out.println(neuralNetwork.getOutput()[0]);
	}
}
