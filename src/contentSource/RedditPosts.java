package contentSource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.SubredditPaginator;

import org.json.JSONObject;



public class RedditPosts {
	
	private RedditPosts(){}
	
	/**Laedt die ersten amountPosts vom Subreddit subreddit
	 * @param subreddit subreddit von dem geladen werden soll
	 * @param amountPosts Anzahl der zu ladenden Posts
	 * @return Liste bei dem ein Element die folgenden Dinge enthaelt: (String) Titel des Posts, (String) Beschreibung des Posts, (Listing<Comment>) Alle Kommentare zu diesem Post
	 */
	static public List<List> getSubreddit(String subreddit,int amountPosts){
		RedditClient reddit = new RedditClient("bot/1.0 by name");
		List<List> result = new ArrayList<List>();
		
		SubredditPaginator frontPage = new SubredditPaginator(reddit,subreddit); // Second parameter could be a subreddit
		Listing<Submission> listing = frontPage.next();
	    for (int i = 0 ; i < amountPosts ; i++) {
	    	Submission submission = listing.get(i);
	        result.add(new ArrayList<Object>(Arrays.asList(submission.getTitle(),submission.getSelftext().md(),submission.getComments())));
	    }
		return result;
	}
	
	/**Holt einen ganzen Post von Reddit
	 * @param postId Die eindeutige ID des Posts. (Kriegt man aus dem Link, z.B. "2iwes5")
	 * @return Erhaltener Post, in der JRAW eigenen Implementation
	 * @throws NetworkException Falls der Post nicht übertragen werden kann, wegen Netzwerkporbleme.
	 */
	static public Submission getPost(String postId) throws NetworkException{
		RedditClient reddit = new RedditClient("bot/1.0 by name");
		
		return reddit.getSubmission(postId);
	}
	
	/**Holt Posts von Reddit und schreibt sie als JSON-Objekte in eine Datei. Es wird bereits ein Feld fuer die Bewertung erzeugt, sodass der Bewerter nur noch einen Wert dort eintragen braucht.
	 * @param filePath Pfad zur Datei
	 * @param subreddit eindeutige Reddit-ID des zu holenden Posts
	 * @throws NetworkException
	 */
	static public void writeTrainingsSetToFile(String filePath,String subreddit) throws NetworkException{
		//Posts von Reddit holen
		Submission comments = contentSource.RedditPosts.getPost(subreddit);
		
		//Jedes Kommentar Zeile fuer Zeile in Datei schreiben
		File redditFile = new File(filePath);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(redditFile));
		} catch (IOException e1) {
			System.out.println("ERROR: Fehler beim Oeffnen der TrainingsSet-Datei.");
		}
		String line = "";
		for(Comment ele : comments.getComments()) {
	    	line = "{";
	    	line = line.concat("text:\"" + ele.getBody().toString().replace("\"", "\\\"").replace("\'", "\\\'").replace("\n", "").replace("\r", "") + "\", bewertung:\"\"}");
	    	try {
				writer.write(line + "\n");
			} catch (IOException e) {
				System.out.println("ERROR: Fehler beim Schreiben in TrainingsSet-Datei.");
			}
	    }
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println("ERROR: Fehler beim Schliessen der TrainingsSet-Datei.");
		}
	}
	
	/**Schreibt die Datei, die durch getTrainingsSetToFile() erstellt wurde, in die Redis-Datenbank.
	 * @param filePath Pfad zur Datei. (Der gleiche der auch beim Erstellen der Datei verwendet wurde)
	 * @throws IOException
	 */
	static public void pushTrainingsSetToRedis(String filePath) throws IOException{
		// #### ALLE LINES AUS DEM FILE AUSLESEN #####
		ArrayList<String> allLines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		for(String line; (line = br.readLine()) != null; ) {
	        allLines.add(line);
	    }
		br.close();
		System.out.println(allLines);
		System.out.println();
		// #### UMWANDELN DER LINES IN JSON #####
		ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		for(String line : allLines){
			if(!line.isEmpty()){
				jsonObjects.add(new JSONObject(line));
			}
		}
		
		
		for(JSONObject thing:jsonObjects){
			RedisConnector.writePostToRedis(thing.getString("text"), thing.getInt("bewertung"));
		}
	}
	
	static public Map<String,Integer> getTrainingsSetFromRedis(){
		return RedisConnector.getAllPostsFromRedis();
	}
	static public Map<String,Integer> getTrainingsSetFromFile(String filePath) throws IOException{
		Map<String,Integer> temp = new HashMap<String,Integer>();
		Map<String,Integer> result = new HashMap<String,Integer>();
		int maxPostLengthInWords = 70;
		
		// #### ALLE LINES AUS DEM FILE AUSLESEN #####
		ArrayList<String> allLines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
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
		
		//####### UMWANDELN DER JSONS IN EINE MAP
		for(JSONObject json : jsonObjects){
			temp.put(json.getString("text"), Integer.valueOf(json.getString("bewertung")));
		}
		
		//##### Kuerzen der Post auf bestimmte Laenge ####
		result = shortenPostToMaxWordsLength(temp,maxPostLengthInWords);
		
		return result;
	}
	
	private static Map<String,Integer> shortenPostToMaxWordsLength(Map<String,Integer> posts,int maxLength){
		Map<String,Integer> result = new HashMap<String,Integer>();
		
		posts.forEach((key, value) -> {
			String temp = "";
			String[] array = key.split(" ");
			if(array.length > maxLength){
				for(int i = 0; i < maxLength ; i++){
					temp = temp.concat(array[i] + " ");
				}
			}
			result.put(temp, value);
		});

		
		return result;
	}
}
