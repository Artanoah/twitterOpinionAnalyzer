package main;

import java.io.File;

import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;

public class main {

	public static void main(String[] args) {
		String dictionary = "dictionary.txt";
		DictionaryCreator dc;
		SpellingCorrector sc;
		String dummy = "fck";
		
		dc = new DictionaryCreator(dictionary);
		sc = new SpellingCorrector(dictionary);
		
		dc.addFile(new File("texts/sherlockHolmes_theValleyOfFear.txt"));
		dc.addFile(new File("texts/edgarWallace_theAngelOfTerror.txt"));
		dc.addFile(new File("texts/history_theProudRebel.txt"));
		dc.addFile(new File("texts/listOfAllWords.txt"));
		dc.addFile(new File("texts/henryReeve_democracyInAmerica.txt"));
		dc.addFile(new File("texts/smileys.txt"));
		sc.refresh();
		
		System.out.println("Checking " + dummy + ": " + sc.isValid(dummy));
		System.out.println("Corrected " + dummy + ": " + sc.correctWord(dummy));
	}

}
