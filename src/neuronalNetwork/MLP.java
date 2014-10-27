package neuronalNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;

public class MLP {
	static DataSet dataSet;
	static MultiLayerPerceptron mlp;
	static Comparator<String> stringComp = (o1, o2) -> o1.compareTo(o2);
	
	public static void setInput(List<HashMap<String, Integer>> data) {
		int nInputLayer = data.get(0).keySet().size();
		int nHiddenLayer = (int) Math.round(nInputLayer * 0.75);
		int nOutputLayer = 1;
		
		mlp = new MultiLayerPerceptron(nInputLayer, nHiddenLayer, nOutputLayer);
		dataSet = new DataSet(nInputLayer, nOutputLayer);
		
		for(HashMap<String, Integer> set : data) {
			dataSet.addRow(new DataSetRow(input, desiredOutput));
			
			for(String key : keys) {
				
			}
		}
	}
}
