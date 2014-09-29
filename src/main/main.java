package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	
		//Erstelle StringCorrectors und Lexika
		String dictionary = "dictionary.txt";
		DictionaryCreator dc;
		SpellingCorrector sc;
		
		dc = new DictionaryCreator(dictionary);
		sc = new SpellingCorrector(dictionary);
		
		dc.addTextFile(new File("texts/sherlockHolmes_theValleyOfFear.txt"));
		dc.addTextFile(new File("texts/edgarWallace_theAngelOfTerror.txt"));
		dc.addTextFile(new File("texts/history_theProudRebel.txt"));
		dc.addTextFile(new File("texts/listOfAllWords.txt"));
		dc.addTextFile(new File("texts/henryReeve_democracyInAmerica.txt"));
		dc.addTextFile(new File("texts/smileys.txt"));
		sc.refresh();
		
		//Erstelle Twitter-Anbindung
		Twitter twitter = TwitterFactory.getSingleton();
	    Query query = new Query();
	    QueryResult result = null;
	    
	    //Setze Twitter-Query-Eigenschaften
	    
	    query.setQuery("#obama");
	    query.setCount(20);
	    query.setSince("2012-11-05");
	    //query.setUntil("2012-11-06");
	    query.setLang("en");
	    
	    result = twitter.search(query);
	    
	    List<String> tweets = new ArrayList<String>();
	    
	    for (Status status : result.getTweets()) {
	        tweets.add(status.getText());
	    }
		
	    for(String ele : tweets){
	    	System.out.println("### TWEET ###\n" + ele);
	    	System.out.println("### KORRIGIERTER TWEET\n" + sc.correctTweet(ele) + "\n");
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
