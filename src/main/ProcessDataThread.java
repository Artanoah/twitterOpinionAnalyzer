package main;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

import normalisation.PartOfSpeechAnalysis;
import spellingCorrection.SpellingCorrector;
import contentSource.RedisConnector;

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
		String stemmed = PartOfSpeechAnalysis.normaliseAndFilterString(clean,true,false);
		HashMap<String, Integer> bagOfWords = getBagOfWords(stemmed);
//		System.out.println(bagOfWords.toString());
		int bewertung = bewertePost(clean);
//		printResult(contentData, clean, stemmed);
		RedisConnector.writeVectorToRedis(new FeatureVector(bagOfWords,bewertung));
	}
	
	public synchronized void printResult(String raw, String clean, String stemmed){
		System.out.println("### Post ###\n" + raw + "\n");
    	System.out.println("### KORRIGIERTER TWEET ###\n" + (clean) + "\n");
    	System.out.println("### GESTEMMTER TWEET ###\n" + stemmed + "\n\n");
	}
	public synchronized int bewertePost(String clean){
		Console console = System.console();
		System.out.println("Bitte bewerte jetzt folgenden Post:");
		System.out.println(clean);
		return Integer.valueOf(console.readLine("Bewertung:"));
	}
	
	//habe ich jemals erwaehnt dass java scheisse ist? falls nicht habe ich es hiermit getan 
	// #signed - Batman
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
	
	Map<Map<String, Integer>, Integer> resultBag = new HashMap<Map<String, Integer>, Integer>();
	
	public void createBagOfWords(Map<String, Integer> map, Integer value,Map<Map<String, Integer>, Integer> resultBag){
 
		resultBag.put(map, value);
				
		
	}


}
