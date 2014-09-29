package main;

import java.io.File;

import spellingCorrection.DictionaryCreator;
import spellingCorrection.SpellingCorrector;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class main {


	public static void main(String[] args) throws TwitterException {
	
		
		
		Twitter twitter = TwitterFactory.getSingleton();
//	    Status status = twitter.updateStatus(latestStatus);
//	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
	    Query query = new Query("lol obama");
	    QueryResult result = twitter.search(query);
	    for (Status status : result.getTweets()) {
	        System.out.println(status.getText());
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
