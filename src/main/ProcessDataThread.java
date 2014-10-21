package main;

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
		printResult(contentData, clean, stemmed);
	}
	
	public static synchronized void printResult(String raw, String clean, String stemmed){
		System.out.println("### TWEET ###\n" + raw + "\n");
    	System.out.println("### KORRIGIERTER TWEET ###\n" + (clean) + "\n");
    	System.out.println("### GESTEMMTER TWEET ###\n" + stemmed + "\n\n");
	}
}
