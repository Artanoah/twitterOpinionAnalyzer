package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListOfAllWords {
	private List<String> listOfWords = new ArrayList<String>();
	private Comparator<String> stringComp = (a, b) -> a.compareTo(b);
	
	
	//######## Methoden ########
	
	/**
	 * Mutator </br>
	 * Fuege ein Wort zur Wortliste hinzu. Dies sollte nur VOR der Nutzung der Word-File-Methoden genutzt werden.
	 * @param word Hinzuzufuegendes Wort
	 */
	public void addWord(String word){
		if(!listOfWords.contains(word)) {
			listOfWords.add(word);
		}
		
		sort();
	}
	
	/**
	 * Mutator </br>
	 * Fuege eine Liste Woerter zur Wortliste hinzu. Dies sollte nur VOR der Nutzung der Word-File-Methoden genutzt werden.
	 * @param word Hinzuzufuegende Woerter
	 */
	public void addWords(List<String> words) {
		words.stream().forEach(word -> {
			if(!listOfWords.contains(word)) {
				listOfWords.add(word);
			}
		});
		
		sort();
	}
	
	/**
	 * Mutator </br>
	 * Fuege eine Wort-File zur Wortliste hinzu. Dies sollte nur VOR der Nutzung der Word-File-Methoden genutzt werden.
	 * @param word File mit hinzuzufuegenden Woertern
	 */
	public void addWordFile(String path) throws IOException {
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		while(br.ready()) {
			String word = br.readLine().trim();
			if(!word.isEmpty() && !listOfWords.contains(word)) {
				listOfWords.add(word);
			}
		}
		
		sort();
	}
	
	/**
	 * Word-File-Methode </br>
	 * Erweitert den Hash map um alle bekannte Woerter und loescht alle nicht bekannten Woerter
	 * @param map Zu erweiternder Hash
	 * @return Erweiterter Hash
	 */
	public Map<String, Integer> createCompleteHash(Map<String, Integer> map) {
		Map<String, Integer> akku = new HashMap<String, Integer>();
		
		listOfWords.stream().forEach(word -> akku.put(word, 0));
		
		map.keySet().forEach(key -> {
			if(akku.containsKey(key)) {
				akku.put(key, map.get(key));
			}
		});
		
		return akku;
	}
	
	/**
	 * Word-File-Methode </br>
	 * Gibt einen eindeutigen Key zu einem Wort zurueck
	 * @param word Eingabewort
	 * @return Eindeutiger Key
	 */
	public int getWordID(String word) {
		return listOfWords.indexOf(word);
	}
	
	/**
	 * Mutator </br>
	 * Laed eine zuvor erstellte Wortdatei. Dabei werden alle bisher erzeugten Eintraege in diesem Objekt geloescht!
	 * @param file <code>String</code> Datei die geladen werden soll.
	 * @throws IOException
	 */
	public void loadFromFile(String file) throws IOException {
		listOfWords = new ArrayList<String>();
		
		addWordFile(file);
	}
	
	/**
	 * Word-File-Methode </br>
	 * Erstellt eine wieder ladbare Datei aus dieser <code>ListOfAllWords</code>.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void dumpToFile(String file) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(file);
		
		listOfWords.forEach(w -> pw.println(w));
		pw.flush();
	}
	
	//######## Interne Methoden ########
	
	private void sort() {
		Collections.sort(listOfWords, stringComp);
	}
	
	
	//######## Getter und Setter ########
	
	/**
	 * Gibt die Vollstaendige Liste aller Woerter zu diesem Zeitpunkt zurueck
	 * @return Liste aller Woerter
	 */
	public List<String> getList() {
		return listOfWords;
	}
	
	public int length() {
		return listOfWords.size();
	}
}
