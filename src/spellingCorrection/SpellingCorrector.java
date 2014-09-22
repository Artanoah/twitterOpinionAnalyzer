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
	
	public SpellingCorrector(String dictionary) {
		this.dictionary = dictionary;
		refresh();
	}
	
	public static String correctWord(String inputWord) {
		String word = inputWord.toLowerCase();
		
		if(isValid(word)) {
			return word;
		}
		
		List<String> permutations = getPermutations(word);
		Map<Integer, String> candidates = new HashMap<Integer, String>();
		
		for(String s : permutations) {
			if(numberOfWords.containsKey(s)) {
				candidates.put(numberOfWords.get(s), s);
			}
		}
		
		if(candidates.size() > 0) {
			return candidates.get(Collections.max(candidates.keySet()));
		}
		
		for(String s : permutations) {
			for(String w : getPermutations(s)) {
				if(numberOfWords.containsKey(w)) {
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
}
