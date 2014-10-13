package ContentSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

public class TweetFile {
	private File tweetFile = null;
	
	/**Initialisiert den TweetFile-Leser. Datei ist noch nicht im Zugriff!
	 * @param filePath Vollstaendiger oder relativer Pfad zur Textdatei. Jede Zeile muss ein vollstaendiges JSON-Objekt sein. Das JSON-Objekt muss mindestens die Keys "created_at" und "text" enthalten.
	 */
	public TweetFile(String filePath){
		tweetFile = new File(filePath);
	}
	
	/**Gibt den Content der einzelnen Tweets wieder. Datei ist im Zugriff!
	 * @return Jeder Index ist der Inhalt eines Tweets.
	 * @throws IOException Falls auf Datei nicht zugegriffen werden kann.
	 */
	public ArrayList<String> getTweetContent() throws IOException{
		
		// #### ALLE LINES AUS DEM FILE AUSLESEN #####
		ArrayList<String> allLines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(tweetFile));
		for(String line; (line = br.readLine()) != null; ) {
	        allLines.add(line);
	    }
		br.close();
		
		// #### UMWANDELN DER LINES IN JSON #####
		ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		for(String line : allLines){
			if(!line.isEmpty()){
				jsonObjects.add(new JSONObject(line));
			}
		}
		
		// #### AUSLESEN DES CONTENTS AUS DEN JSON-OBJECTS
		ArrayList<String> result = new ArrayList<String>();
		for(JSONObject json : jsonObjects){
			if(json.has("created_at")){
				result.add(json.getString("text"));
			}
		}
		
		
		return result;
	}
}
