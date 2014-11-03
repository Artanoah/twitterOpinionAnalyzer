package neuronalNetwork;

import java.util.Comparator;
import java.util.List;

import main.FeatureVector;
import main.ListOfAllWords;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.comp.layer.InputLayer;

public class EncogMLP {
	static MLDataSet trainingSet = new BasicMLDataSet();
	static BasicNetwork network;
	static ListOfAllWords wordList;
	static Comparator<String> stringComp = (o1, o2) -> o1.compareTo(o2);
	
	static int nInputLayer;
	static int nHiddenLayer;
	static int nOutputLayer;
	
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
		System.out.println("###### MLP: Vector hinzugefuegt #####");
		double[] inputAkku = new double[wordList.length()];
		double[] outputAkku = new double[1];
		
		outputAkku[0] = data.getValue();
		
		for(String w : wordList.getList()) {
			inputAkku[wordList.getWordID(w)] = data.getMap().get(w);
		}
		
		dataSet.addRow(inputAkku, outputAkku);
	}
	
	/**
	 * Laesst das Multi-Layer-Perceptron mit dem aktuellen Trainingsset lernen
	 */
	public static void learn() {
		System.out.println("###### MLP: Starte lernen #####");
		System.out.println("MLP: " + nInputLayer + " Input-Layer");
		System.out.println("MLP: " + nHiddenLayer + " Hidden-Layer");
		System.out.println("MLP: " + nOutputLayer + " Output-Layer");
		mlp.learn(dataSet);
	}
	
	/**
	 * Speichert die errechneten Ergebnisse in einer Datei zwischen
	 * @param file Dateipfad zum Speicherort
	 */
	public static void save(String file) {
		mlp.save(file);
	}
	
	/**
	 * Setze die aktuellen input-Parameter.
	 * @param vector Input-Vector. Der zugehoerige Value wird ignoriert
	 */
	public static void setInput(FeatureVector vector) {
		double[] inputAkku = new double[wordList.length()];
		
		for(String w : wordList.getList()) {
			inputAkku[wordList.getWordID(w)] = vector.getMap().get(w);
		}
		
		mlp.setInput(inputAkku);
	}
	
	/**
	 * Berechne den Output mit dem aktuellen Input und gibt den erzeugten output zurueck.
	 * @return Output der Berechnung
	 */
	public static double[] calculate() {
		mlp.calculate();
		return mlp.getOutput();
	}
}
