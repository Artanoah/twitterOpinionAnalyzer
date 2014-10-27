package main;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import contentSource.RedisConnector;
import partOfSpeechAnalysis.PartOfSpeechAnalysis;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import contentSource.TweetStream;
import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;


public class main {


	public static void main(String[] args) throws NetworkException{

	
		//Erstelle StringCorrectors und Lexika
		String dictionary = "dictionary.txt";
		DictionaryCreator dc;
		SpellingCorrector sc;
		
		new File(dictionary).delete();
		dc = new DictionaryCreator(dictionary);
		sc = new SpellingCorrector(dictionary);
		
		dc.addTextFile(new File("texts/sherlockHolmes_theValleyOfFear.txt"));
		dc.addTextFile(new File("texts/edgarWallace_theAngelOfTerror.txt"));
		dc.addTextFile(new File("texts/history_theProudRebel.txt"));
		dc.addTextFile(new File("texts/listOfAllWords.txt"));
		dc.addTextFile(new File("texts/henryReeve_democracyInAmerica.txt"));
		dc.addTextFile(new File("texts/big.txt"));
		dc.addSmileyFile(new File("texts/smileys.txt"));
		sc.refresh();
		
		//Hole Daten von Quelle
	    Submission comments = contentSource.RedditPosts.getPost("2j28og");
		
	    //Erstellen des Thread-Pools
	    ExecutorService executor = Executors.newFixedThreadPool(100);
	    
	    //Jedes Comment durch einen eigenen Thread abarbeiten
	    for(Comment ele : comments.getComments()) {
	    	executor.submit(new ProcessDataThread(ele.getBody().toString(),sc));
	    }
	    
	    //Auf Abarbeiten jedes Threads warten, und dann erst beenden
	    System.out.println("Warte auf die Beendigung aller Threads");
	    while(!(executor.isTerminated())){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
	}
		
//public static void main(String[] args) {
//		String dictionary = "dictionary.txt";
//		DictionaryCreator dc;
//		SpellingCorrector sc;
//		String dummy = "fck";
//		
//		dc = new DictionaryCreator(dictionary);
//		sc = new SpellingCorrector(dictionary);
//		
//		dc.addFile(new File("texts/sherlockHolmes_theValleyOfFear.txt"));
//		dc.addFile(new File("texts/edgarWallace_theAngelOfTerror.txt"));
//		dc.addFile(new File("texts/history_theProudRebel.txt"));
//		dc.addFile(new File("texts/listOfAllWords.txt"));
//		dc.addFile(new File("texts/henryReeve_democracyInAmerica.txt"));
//		dc.addFile(new File("texts/smileys.txt"));
//		sc.refresh();
//		
//		System.out.println("Checking " + dummy + ": " + sc.isValid(dummy));
//		System.out.println("Corrected " + dummy + ": " + sc.correctWord(dummy));
//	

}
