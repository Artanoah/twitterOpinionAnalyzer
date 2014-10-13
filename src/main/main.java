package main;

import java.io.File;
import java.util.List;

import partOfSpeechAnalysis.PartOfSpeechAnalysis;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import ContentSource.TweetStream;
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
	    
	    Submission comments = ContentSource.RedditPosts.getPost("2j28og");
	    String temp = "";
		
	    for(Comment ele : comments.getComments()) {
	    	System.out.println("### TWEET ###\n" + ele.getBody().toString() + "\n");
	    	System.out.println("### KORRIGIERTER TWEET ###\n" + (temp = sc.correctTweet(ele.getBody().toString())) + "\n");
	    	System.out.println("### GESTEMMTER TWEET ###\n" + PartOfSpeechAnalysis.partOfSpeechWithStaming(temp) + "\n\n");
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
