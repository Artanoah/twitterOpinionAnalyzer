package neuronalNetwork;

import java.util.Comparator;
import java.util.List;

import main.ListOfAllWords;
import main.Vector;

import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;

public class MLP {
	static DataSet dataSet;
	static MultiLayerPerceptron mlp;
	static ListOfAllWords wordList;
	static Comparator<String> stringComp = (o1, o2) -> o1.compareTo(o2);
	
	public MLP(ListOfAllWords list) {
		wordList = list;
		
		int nInputLayer = list.length();
		int nHiddenLayer = (int) Math.round(nInputLayer * 0.75);
		int nOutputLayer = 1;
		
		mlp = new MultiLayerPerceptron(nInputLayer, nHiddenLayer, nOutputLayer);
		dataSet = new DataSet(nInputLayer, nOutputLayer);
	}
	
	public static void addInput(List<Vector> data) {
		for(Vector vector : data) {
			addInput(vector);
		}
	}
	
	public static void addInput(Vector data) {
		double[] inputAkku = new double[wordList.length()];
		double[] outputAkku = new double[1];
		
		outputAkku[0] = data.getValue();
		
		for(String w : wordList.getList()) {
			inputAkku[wordList.getWordID(w)] = data.getMap().get(w);
		}
		
		dataSet.addRow(inputAkku, outputAkku);
	}
}
