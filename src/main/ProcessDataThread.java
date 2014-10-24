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
	
	//habe ich jemals erwaehnt dass java scheisse ist? falls nicht habe ich es hiermit getan
	public static HashMap<String, Integer> getBagOfWords(String stemmed_strings){
		HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
		String[] splitted_string = stemmed_strings.split(" "); 
		for(String ele : splitted_string ){
			if(bagOfWords.containsKey(ele)){
				Integer count = bagOfWords.get(ele);
				bagOfWords.put(ele, count+1);
			} else bagOfWords.put(ele, 1);
		}
		return bagOfWords;
	
	}

}
