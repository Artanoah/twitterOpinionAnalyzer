package neuronalNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import main.Constants;
import main.FeatureVector;
import main.ListOfAllWords;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

public class EncogMLP {
	static BasicNetwork network;
	static int nInputLayer;
	static int nHiddenLayer;
	static int nOutputLayer;
	
	static MLDataSet trainingSet = new BasicMLDataSet();
	static List<List<Double>> INPUT = new ArrayList<List<Double>>();
	static List<List<Double>> OUTPUT = new ArrayList<List<Double>>();
	
	static ListOfAllWords wordList;
	static Comparator<String> stringComp = (o1, o2) -> o1.compareTo(o2);
	
	
	/**
	 * Erzeugt ein Multi-Layer-Percepronen Netzwerk mit einem Lexikon list.
	 * @param list Vollstaendiges Lexikon aller zu verwendenen Woerter
	 */
	public EncogMLP(ListOfAllWords list) {
		wordList = list;
				
		nInputLayer = list.length();
		nHiddenLayer = (int) Math.round(nInputLayer * 0.75);
		nOutputLayer = 1;
		
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, nInputLayer));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, nHiddenLayer));
		//network.addLayer(new BasicLayer(new ActivationSigmoid(), true, Math.round(nHiddenLayer / 2)));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, nOutputLayer));
		
		network.getStructure().finalizeStructure();
		network.reset();
	}
	
	/**
	 * Fuege eine Liste Input-Vectoren zum Trainingsset hinzu.
	 * @param data Liste der Input-Vectoren
	 */
	public static void addVector(List<FeatureVector> data) {
		for(FeatureVector vector : data) {
			addVector(vector);
		}
	}
	
	/**
	 * Fuege einen einzelnen Input-Vectoren zum Trainingsset hinzu.
	 * @param data Input-Vector
	 */
	public static void addVector(FeatureVector data) {
		if(data.getValue() != 0d) {
			double inputAkku[] = new double[wordList.length()];
			double outputAkku = data.getValue();
			
			for(String w : wordList.getList()) {
				inputAkku[wordList.getWordID(w)] = data.getMap().get(w);
			}
			
			ArrayList<Double> inputTemp = new ArrayList<Double>();
			
			for(int i = 0; i < inputAkku.length; i++) {
				inputTemp.add(inputAkku[i]);
			}
	
			INPUT.add(inputTemp);
			OUTPUT.add(new ArrayList<Double>(Arrays.asList(outputAkku)));
		}
	}
	
	/**
	 * Laesst das Multi-Layer-Perceptron mit dem aktuellen Trainingsset lernen
	 */
	public static int learnWithResilientPropagation() {
		System.out.println("###### MLP: Starte lernen #####");
		System.out.println("MLP: " + nInputLayer + " Input-Layer");
		System.out.println("MLP: " + nHiddenLayer + " Hidden-Layer");
		System.out.println("MLP: " + nOutputLayer + " Output-Layer");
		
		double[][] inputArray = new double[INPUT.size()][];
		double[][] outputArray = new double[OUTPUT.size()][];
		
		for(int i = 0; i < INPUT.size(); i++) {
			inputArray[i] = toDoubleArray(INPUT.get(i));
		}
		
		for(int i = 0; i < OUTPUT.size(); i++) {
			outputArray[i] = toDoubleArray(OUTPUT.get(i));
		}
		
		trainingSet = new BasicMLDataSet(inputArray, outputArray);
		ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		int iteration = 1;
		
		do {
			train.iteration();
			System.out.println("MLP: Iteration: " + iteration + "\t Error: " + train.getError());
			iteration++;
		} while(train.getError() > Constants.ERROR_TOLLERANCE && iteration < Constants.MAXIMAL_ITERATIONS);
		
		train.finishTraining();
		return iteration;
	}
	
	
	public static int learnWithErrorBackPropagation() {
		System.out.println("###### MLP: Starte lernen #####");
		System.out.println("MLP: " + nInputLayer + " Input-Layer");
		System.out.println("MLP: " + nHiddenLayer + " Hidden-Layer");
		System.out.println("MLP: " + nOutputLayer + " Output-Layer");
		
		double[][] inputArray = new double[INPUT.size()][];
		double[][] outputArray = new double[OUTPUT.size()][];
		
		for(int i = 0; i < INPUT.size(); i++) {
			inputArray[i] = toDoubleArray(INPUT.get(i));
		}
		
		for(int i = 0; i < OUTPUT.size(); i++) {
			outputArray[i] = toDoubleArray(OUTPUT.get(i));
		}
		
		trainingSet = new BasicMLDataSet(inputArray, outputArray);
		Backpropagation train = new Backpropagation(network, trainingSet);
		int iteration = 1;
		
		do {
			train.iteration();
			System.out.println("MLP: Iteration: " + iteration + "\t Error: " + train.getError());
			iteration++;
		} while(train.getError() > Constants.ERROR_TOLLERANCE && iteration < Constants.MAXIMAL_ITERATIONS);
		
		train.finishTraining();
		return iteration;
	}
	
	static double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		for(int i = 0;i < ret.length;i++)
	    ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Berechne den Output mit dem aktuellen Input und gibt den erzeugten output zurueck.
	 * @param input <code>double[]</code> Input-Array. Die Woerter muessen in der selben 
	 * Reihenfolge wie beim Lernen sein.
	 * 
	 * @return Output der Berechnung
	 */
	public static double[] calculate(double[] input) {
		return network.compute(new BasicMLData(input)).getData();
	}
	
	/**
	 * Loads a leaned network from the file <code>networkFile</code>.
	 * @param networkFile <code>String</code> Networkfile to load as a network.
	 */
	public static void loadNetwork(String networkFile) {
		network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(networkFile));
	}
	
	public static void saveNetwork(String file) {
		EncogDirectoryPersistence.saveObject(new File(file), network);;
	}
}
