package main;

import java.util.HashMap;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import partOfSpeechAnalysis.PartOfSpeechAnalysis;
import spellingCorrection.SpellingCorrector;

public class ProcessDataThread implements Runnable{
	private String contentData = "";
	private SpellingCorrector sc = null;
	private static Object printLock = null;
	
	ProcessDataThread(String content,SpellingCorrector sc){
		this.contentData = content;
	}
	
	@Override
	public void run(){
		String clean = sc.correctTweet(contentData);
		String stemmed = PartOfSpeechAnalysis.partOfSpeechWithStaming(clean);
		HashMap<String, Integer> bagOfWords = getBagOfWords(stemmed);
		System.out.println(bagOfWords.toString());
		
		printResult(contentData, clean, stemmed);
	}
	
	public static synchronized void printResult(String raw, String clean, String stemmed){
		System.out.println("### Post ###\n" + raw + "\n");
    	System.out.println("### KORRIGIERTER TWEET ###\n" + (clean) + "\n");
    	System.out.println("### GESTEMMTER TWEET ###\n" + stemmed + "\n\n");
	}

	public static HashMap<String, Integer> getBagOfWords(String stemmed_strings){
		HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
		String[] l = stemmed_strings.split(" "); 
		for(String i : l ){
			if(bagOfWords.containsKey(i)){
				Integer q = bagOfWords.get(i);
				bagOfWords.put(i, q+1);
			} else bagOfWords.put(i, 1);
		}
		return bagOfWords;
	
	}

}
