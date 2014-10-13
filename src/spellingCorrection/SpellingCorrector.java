package spellingCorrection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellingCorrector {
	
	private static Map<String, Integer> numberOfWords = new HashMap<String, Integer>();
	private static String dictionary;
	
	/**
	 * Erstellt einen SpellingCorrector. Das dictionary muss zu diesem 
	 * Zeitpunkt nicht zwangsweise existieren, aber der Pfad muss angegeben 
	 * werden.
	 * @param dictionary Zu verwendenes Lexikon
	 */
	public SpellingCorrector(String dictionary) {
		this.dictionary = dictionary;
		refresh();
	}
	
	/**
	 * Versucht den String inputWord zu korrigieren. Wenn das Wort zur 
	 * Englischen Sprache gehört, dann wird es unverändert zurückgegeben. 
	 * Wenn das Wort nicht zur Englischen Sprache gehört wird in zwei 
	 * Schritten versucht es zu korrigieren. Dabei wird das am 
	 * wahrscheinlichsten korrekte Wort zurückgeliefert. Die 
	 * Wahrscheinlichkeit berechnet sich anhand der Menge der Nutzungen 
	 * dieses Wortes in der Englischen Sprache.
	 * @param inputWord Zu korrigierendes Wort
	 * @return Korrigertes Wort
	 */
	public static String correctWord(String inputWord) {
		String word = inputWord.toLowerCase();
		
		if(isValid(word)) {
			return word;
		}
		
		List<String> permutations = getPermutations(word);
		Map<Integer, String> candidates = new HashMap<Integer, String>();
		
		for(String s : permutations) {
			if(isValid(s)) {
				candidates.put(numberOfWords.get(s), s);
			}
		}
		
		if(candidates.size() > 0) {
			return candidates.get(Collections.max(candidates.keySet()));
		}
		
		for(String s : permutations) {
			for(String w : getPermutations(s)) {
				if(isValid(s)) {
					candidates.put(numberOfWords.get(w), w);
				}
			}
		}
		
		if(candidates.size() > 0) {
			return candidates.get(Collections.max(candidates.keySet()));
		} else {
			return "";
		}
	}
	
	public static String correctTweet(String inputSentence) {
		String akku = "";
		String temp = new String(inputSentence);
		
		//Lösche Links
		while(temp.indexOf("http") != -1) {
			int i = temp.indexOf("http");
			int j = temp.indexOf(' ', i);
			
			temp = temp.subSequence(0, i-1) + temp.substring(j < 0 ? temp.length() - 1 : j);
		}
		
		//Isoliere Sonderzeichen
		for(int i = 0; i < temp.length() - 1; i++) {
			if((!twitterIsChar(temp.charAt(i))) && twitterIsChar(temp.charAt(i+1))) {
				if(temp.charAt(i+1) != ' ') {
					temp = temp.substring(0, i) + " " + temp.substring(i);
					i++;
				}
			}
		}
		
		for(int i = 1; i < temp.length(); i++) {
			if(twitterIsChar(temp.charAt(i-1)) && !twitterIsChar(temp.charAt(i))) {
				if(temp.charAt(i-1) != ' ') {
					temp = temp.substring(0, i) + " " + temp.substring(i);
					i++;
				}
			}
		}
		
		//Lösche überflüssige Punkte
		temp = temp.replaceAll("[\\.]\\1+", "");
		
		for(String word : temp.split(" ")) {
			if(word.equals("")){
				akku += "";
			}else if(word.indexOf('#') == 0 		||
					word.indexOf('@') == 0 	||
					!twitterIsChar(word.charAt(0))) {
				akku += " " + word;
			} else {
				akku += " " + correctWord(word);
			}
		}
		
		return akku;
	}
	
	/**
	 * Liest das dictionary neu ein. Diese Methode muss nach jedem 
	 * "DictionaryCreator#addTextFile" oder "DictionaryCreator#addSmileyFile" 
	 * ausgeführt werden um die Veränderungen zu nutzen.
	 */
	public static void refresh() {
		BufferedReader br;
		numberOfWords = new HashMap<String, Integer>();
		
		try {
			br = new BufferedReader(new FileReader(dictionary));

			while(br.ready()) {
				String line = br.readLine();
				numberOfWords.put(line.split(" ")[0], 
						Integer.valueOf(line.split(" ")[1]));
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Prüft ob das wort inputWort zur englischen Sprache gehört.
	 * @param inputWord Zu überprüfendes Wort
	 * @return True wenn das Wort zur englischen Sprache gehört, False wenn nicht.
	 */
	public static boolean isValid(String inputWord) {
		String word = inputWord.toLowerCase();
		return numberOfWords.containsKey(word);
	}
	
	private static List<String> getPermutations(String word) {
		List<String> akku = new ArrayList<String>();
		
		for(int i=0; i < word.length(); ++i) {
			akku.add(word.substring(0, i) + word.substring(i+1));
		}
		
		for(int i=0; i < word.length()-1; ++i) {
			akku.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2));
		}
		
		for(int i=0; i < word.length(); ++i) {
			for(char c='a'; c <= 'z'; ++c) {
				akku.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
			}
		}
		
		for(int i=0; i <= word.length(); ++i) {
			for(char c='a'; c <= 'z'; ++c) {
				akku.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
			}
		}
		
		return akku;
	}
	
	private static boolean twitterIsChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ' ' || c == '@' || c == '#';
	}
}
