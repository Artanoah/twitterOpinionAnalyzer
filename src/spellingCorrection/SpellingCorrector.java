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

/**
 * Diese Klasse dient als Rechtschreibkorrektur. Sie basiert auf
 * einem von der Klasse {@link DictionaryCreator} erstellten 
 * Lexikon. </br>
 * Es gibt diverse Methoden zum korrigieren von einzelnen Woertern
 * oder auch ganzen Texten.
 * 
 * @author Steffen Giersch
 */
public class SpellingCorrector {
	
	private static Map<String, Integer> numberOfWords = new HashMap<String, Integer>();
	private static String dictionary;
	
	/**
	 * Erstellt einen SpellingCorrector. Das dictionary muss zu diesem 
	 * Zeitpunkt nicht zwangsweise existieren, aber der Pfad muss angegeben 
	 * werden.
	 * 
	 * @param dictionary Datei die das zu verwendene Lexikon enth�lt
	 */
	public SpellingCorrector(String dictionary) {
		this.dictionary = dictionary;
		refresh();
	}
	
	/**
	 * Versucht den String inputWord zu korrigieren. Wenn das Wort zur 
	 * Englischen Sprache geh�rt, dann wird es unver�ndert zur�ckgegeben. 
	 * Wenn das Wort nicht zur Englischen Sprache geh�rt wird in zwei 
	 * Schritten versucht es zu korrigieren. Dabei wird das am 
	 * wahrscheinlichsten korrekte Wort zur�ckgeliefert. Die 
	 * Wahrscheinlichkeit berechnet sich anhand der Menge der Nutzungen 
	 * dieses Wortes in der Englischen Sprache.
	 * 
	 * @param inputWord <code>String</code> Zu korrigierendes Wort
	 * @return <code>String</code> Korrigertes Wort
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
	
	/**
	 * Versucht die Woerter des Strings <code>inputSentence</code> mit 
	 * der Methode {@link SpellingCorrector.correctWort} zu korrigieren.</br>
	 * Hierbei werden URLs und Zahlen geloescht, Sonderzeichen mit
	 * Leerzeichen isoliert und Hashtags und TwitterUsernamen ignoriert.
	 * 
	 * @param inputSentence <code>String</code> Zu korrigierender Tweet 
	 * oder Satz
	 * @return <code>String</code> Korrigierter Tweet oder Satz
	 */
	public static String correctTweet(String inputSentence) {
		String akku = "";
		String temp = new String(inputSentence);
		
		//L�sche Links
		while(temp.indexOf("http") != -1) {
			int i = temp.indexOf("http");
			int j = temp.indexOf(' ', i);
			
			temp = temp.subSequence(0, i-1) + temp.substring(j < 0 ? temp.length() - 1 : j);
		}
		
		//L�sche Zahlen
		for(int i = 0; i < temp.length() - 2; i++) {
			if(twitterIsNum(temp.charAt(i + 1))) {
				temp = temp.substring(0, i) + temp.substring(i + 2);
			}
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
		
		//L�sche �berfl�ssige Punkte
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
	 * ausgef�hrt werden um die Ver�nderungen zu nutzen.
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
	 * Pr�ft ob das wort inputWort zur englischen Sprache geh�rt.
	 * @param inputWord Zu �berpr�fendes Wort
	 * @return True wenn das Wort zur englischen Sprache geh�rt, False wenn nicht.
	 */
	public static boolean isValid(String inputWord) {
		String word = inputWord.toLowerCase();
		return numberOfWords.containsKey(word);
	}
	
	/**
	 * Erzeugt diverse "Falschschreibweisen" des eingegebenen Wortes
	 * 
	 * @param word <code>String</code> Zu veraenderndes Wort
	 * @return <code>String</code> Veraendertes Wort
	 */
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
	
	private static boolean twitterIsNum(char c) {
		return c >= '0' && c <= '9';
	}
}
