package neuronalNetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import main.Constants;
import main.FeatureVector;
import main.ListOfAllWords;

import org.encog.engine.network.activation.ActivationCompetitive;
import org.encog.engine.network.activation.ActivationGaussian;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSIN;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * Klasse zur vereinfachten Benutzung des Encog-Frameworks. Diese Klasse enthält alle 
 * wichtigen Methoden zur Benutzung eines Multi-Layer-Perceptronen-Netzes. Änderungen
 * an der Befüllung oder am Aufbau müssen direkt in den Methoden vorgenommen werden. </br>
 * In dieser Klasse benutzte Konstanten befinden sich in der Klasse {@link main.Constants}
 * </br>
 * Encog-API: http://heatonresearch-site.s3-website-us-east-1.amazonaws.com/javadoc/encog-3.3/index.html
 * 
 * @author Steffen Giersch
 */
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
	
	static int VALUE_CORRECTION = 0;
	
	
	/**
	 * Erzeugt ein Multi-Layer-Percepronen Netzwerk mit einem Lexikon list. </br>
	 * Zur Veraenderung des Aufbaus des Netzes sollten die <code>network.addLayer</code>
	 * und <code>n*Layer</code> Aufrufe in dieser Funktion veraendert werden.
	 * 
	 * @param list <code>ListOfAllWords</code> Vollstaendiges Lexikon aller zu verwendenen Woerter
	 */
	public EncogMLP(ListOfAllWords list) {
		wordList = list;
				
		nInputLayer = list.length();
		nHiddenLayer = (int) Math.round(nInputLayer * 1.25);
		nOutputLayer = 1;
		
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, nInputLayer));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, nHiddenLayer));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, Math.round(nHiddenLayer / 2)));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, nOutputLayer));
		
		network.getStructure().finalizeStructure();
		network.reset();
	}
	
	/**
	 * Fuege eine Liste Input-Vectoren vom Typ <code>List&lt;FeatureVector&gt;</code> zum 
	 * Trainingsset hinzu. </br> 
	 * Erst wenn alle benoigten Trainingssets hinzugefuegt wurden sollte mit dem Training begonnen
	 * werden. </br>
	 * Es ist zu beachten, dass nur die Woerter eines Feature-Vectors zum Trainingsset
	 * hinzugefuegt werden die in der <code>ListOfALlWords</code> aus dem Constructor 
	 * dieses Objekts enthalten sind. Woerter die nicht enthalten sind werden ignoriert.
	 * Falls ein in einem der Feature-Vectoren kein Wort mehr als null mal auftaucht, 
	 * dann wird dieser ignoriert.
	 * 
	 * @param data <code>List&lt;FeatureVector&gt;</code> Liste der Input-Vectoren
	 */
	public static void addVector(List<FeatureVector> data) {
		for(FeatureVector vector : data) {
			addVector(vector);
		}
	}
	
	/**
	 * Fuege einen einzelnen Input-Vectoren zum Trainingsset hinzu. </br>
	 * Erst wenn alle nenoetigten Trainingssets hinzugefuegt wurden sollte mit dem 
	 * Training begonnen werden. </br>
	 * Es ist zu beachten, dass nur die Woerter des Feature-Vectors zum Trainingsset
	 * hinzugefuegt werden die in der <code>ListOfALlWords</code> aus dem Constructor 
	 * dieses Objekts enthalten sind. Woerter die nicht enthalten sind werden ignoriert.
	 * Falls in dem Feature-Vector kein Wort mehr als null mal auftaucht, nicht 
	 * hinzugefuegt.
	 * 
	 * @param data <code>FeatureVector</code> Input-Vector
	 */
	public static void addVector(FeatureVector data) {
		if(data.getMap().size() != 0) {
			double inputAkku[] = new double[wordList.length()];
			double outputAkku = data.getValue() + VALUE_CORRECTION;
			
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
	 * Laesst das Multi-Layer-Perceptron mit dem aktuellen Trainingsset 
	 * und der Methode ResilientPropagation lernen. </br>
	 * Essentiell am beim anlernen eines Neuronalen-Netzes ist die Abbruchbedingung.
	 * Diese befindet sich am Ende der <code>do-while</code>-Schleife. </br>
	 * Diese Lernmethode nutzt automatische Parallelisierung.
	 * 
	 * @return <code>int</code> Anzahl der gelernten Iterationen.
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
	
	/**
	 * Laesst das Multi-Layer-Perceptron mit dem aktuellen Trainingsset 
	 * und der Methode Error-Backpropagation lernen. </br>
	 * Essentiell am beim anlernen eines Neuronalen-Netzes ist die Abbruchbedingung.
	 * Diese befindet sich am Ende der <code>do-while</code>-Schleife. </br>
	 * Diese Lernmethode nutzt automatische Parallelisierung.
	 * 
	 * @return <code>int</code> Anzahl der gelernten Iterationen.
	 */
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
	
	private static double[] toDoubleArray(List<Double> list) {
		double[] ret = new double[list.size()];
		for(int i = 0;i < ret.length;i++)
	    ret[i] = list.get(i);
		return ret;
	}
	
	/**
	 * Berechne den Output mit dem Input <code>input</code>und gibt den erzeugten Output zurueck.
	 * @param input <code>double[]</code> Input-Array. Die Woerter muessen auf dem selben
	 * <code>ListOfAllWords</code> Objekt basieren - ansonsten ist das Ergebnis nicht 
	 * deterministisch!
	 * 
	 * @return Output der Berechnung
	 */
	public static double[] calculate(double[] input) {
		return network.compute(new BasicMLData(input)).getData();
	}
	
	public static double calculate(FeatureVector fv) {
		int size = wordList.length();
		double[] akku = new double[size];
		
		for(int i = 0; i < size; i++) {
			akku[i] = 0.0;
		}
		
		fv.getMap().forEach((key, value) -> {
			if(wordList.getWordID(key) > -1) {
				akku[wordList.getWordID(key)] = value;
			}
		});
		
		return calculate(akku)[0] - VALUE_CORRECTION;
	}
	
	/**
	 * Laed ein angelerntes Netzwerk aus der Datei <code>networkFile</code>. Das Netzwerk
	 * sollte auf dem selben <code>ListOfAllWOrds</code>-Objekt basieren wie dieses 
	 * <code>EncocMLP</code>-Objekt. 
	 * Siehe: {@link neuronalNetwork.EncogMLP#saveNetwork(String)}
	 * 
	 * @param networkFile <code>String</code> Name der Datei die gespeichert wird
	 */
	public static void loadNetwork(String networkFile) {
		network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(networkFile));
	}
	
	
	/**
	 * Speichert dieses Netzwerk als Datei <code>file</code>. Dabei wird NICHT das
	 * dazugehoerige <code>ListOfAllWords</code>-Objekt gespeichert. Dies muss beim
	 * spaeteren Laden zuvor mit der Methode {@link main.ListOfAllWords#loadFromFile(String)}
	 * wieder hergestellt werden.
	 * 
	 * @param file <code>String</code> Name der Datei die erzeugt wird
	 */
	public static void saveNetwork(String file) {
		EncogDirectoryPersistence.saveObject(new File(file), network);
	}
}
