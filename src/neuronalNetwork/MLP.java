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
	
	/**
	 * Erzeugt ein Multi-Layer-Percepronen Netzwerk mit einem Lexikon list.
	 * @param list Vollstaendiges Lexikon aller zu verwendenen Woerter
	 */
	public MLP(ListOfAllWords list) {
		wordList = list;
		
		int nInputLayer = list.length();
		int nHiddenLayer = (int) Math.round(nInputLayer * 0.75);
		int nOutputLayer = 1;
		
		mlp = new MultiLayerPerceptron(nInputLayer, nHiddenLayer, nOutputLayer);
		dataSet = new DataSet(nInputLayer, nOutputLayer);
	}
	
	/**
	 * Fuege eine Liste Input-Vectoren zum Trainingsset hinzu.
	 * @param data Liste der Input-Vectoren
	 */
	public static void addVector(List<Vector> data) {
		for(Vector vector : data) {
			addVector(vector);
		}
	}
	
	/**
	 * Fuege einen einzelnen Input-Vectoren zum Trainingsset hinzu.
	 * @param data Input-Vector
	 */
	public static void addVector(Vector data) {
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
	public static void setInput(Vector vector) {
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
