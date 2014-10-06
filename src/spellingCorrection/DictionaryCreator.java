package spellingCorrection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryCreator {
	String dictionary;
	Map<String, Integer> numberOfWords = new HashMap<String, Integer>();
	
	/**
	 * Erzeugt einen DictionaryCreator.
	 * @param dictionary Zu erzeugenes Lexikon.
	 */
	public DictionaryCreator(String dictionary) {
		this.dictionary = dictionary;
		BufferedReader br;
		File dummy = new File(dictionary);
		
		try {
			dummy.createNewFile();
			br = new BufferedReader(new FileReader(this.dictionary));
			
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
	 * F�ge eine Textdatei hinzu. Diese sollte ausschlie�lich korrekte 
	 * W�rter englischer Sprache enthalten.
	 * @param text Hinzuzuf�gende Textdatei
	 */
	public void addTextFile(File text) {
		BufferedReader in;
		PrintWriter pw;
		
		try {
			Pattern p = Pattern.compile("\\w+");
			pw = new PrintWriter(dictionary);
			in = new BufferedReader(new FileReader(text));

			while(in.ready()) {
				String line = in.readLine();
				Matcher m = p.matcher(line.toLowerCase());
				
				while(m.find()) {
					String word = m.group();
					if(numberOfWords.containsKey(word)) {
						numberOfWords.put(word
								, numberOfWords.get(word) + 1);
					} else {
						numberOfWords.put(word, 1);
					}
				}
			}
			
			for(Map.Entry<String, Integer> entry : numberOfWords.entrySet()) {
				pw.println(entry.getKey() + " " + entry.getValue());
			}

			in.close();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSmileyFile(File text) {
		BufferedReader in;
		PrintWriter pw;
		
		try {
			pw = new PrintWriter(dictionary);
			in = new BufferedReader(new FileReader(text));

			while(in.ready()) {
				String line = in.readLine();
				
				for(String word : line.split(" ")){
					if(numberOfWords.containsKey(word)) {
						numberOfWords.put(word
								, numberOfWords.get(word) + 1);
					} else {
						numberOfWords.put(word, 1);
					}
				}
			}
			
			for(Map.Entry<String, Integer> entry : numberOfWords.entrySet()) {
				pw.println(entry.getKey() + " " + entry.getValue());
			}

			in.close();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
