package main;

import java.io.*;
import java.util.*;


/**
 * Diese Klasse ist dazu da alle W�rter die ein beliebiges Lernverfahren kennt
 * zu halten. Hierzu existieren diverse <code>addWord</code>-artige Methoden.
 * Da viele Bibliotheken fuer Lernverfahren mit Vectoren arbeiten laesst sich
 * jedem hinzugefuegten Wort eine eindeutige ID zuweisen. Diese IDs sind von
 * 0 bis n durchgeangig aufgebaut, sodass sie zum Beispiel zum Suchen in einem 
 * Array benutzt werden koennen. </br>
 * Des Weiteren sind alle Methoden in <code>Mutator</code> und <code>Word-
 * File-Methode</code> unterteilt und so in der JavaDoc markiert. </br> 
 * <code>Mutator</code>-Methoden veraendern das Objekt und sollten nicht im 
 * waehrend der Verarbeitung mit einem Lernverfahren benutzt werden, da die 
 * Mutator-Methode auch Indizes veraendert. </br>
 * <code>Word-File</code>-Methoden hingegen sind fuer die Benutzung von 
 * Lernverfahren gedacht und koennen ohne Bedenken verwendet werden.
 * 
 * @author Steffen Giersch
 */
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
	 * 
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
	 * Word-File-Methode </br>
	 * Gibt die Vollstaendige Liste aller Woerter zu diesem Zeitpunkt zurueck
	 * 
	 * @return <code>List&lt;String&gt;</code> Liste aller Woerter
	 */
	public List<String> getList() {
		return listOfWords;
	}
	
	/**
	 * Word-File-Methode </br>
	 * Gibt die Anzahl der eingespeisten Woerter zurueck.
	 * 
	 * @return <code>int</code> Anzahl der Woerter
	 */
	public int length() {
		return listOfWords.size();
	}
	
	/**
	 * Word-File-Methode </br>
	 * Gibt die Anzahl der eingespeisten Woerter zurueck.
	 * 
	 * @return <code>int</code> Anzahl der Woerter
	 */
	public int size() {
		return length();
	}
}
