package main;

import java.io.IOException;

import net.dean.jraw.http.NetworkException;
import contentSource.RedditPosts;

public class GetPostsMain {
	/**
	 * Posts von Reddit abholen und als JSon Objekte in eine Textdatei speichern. Diese Objekte koennen dann bewertet werden.
	 * @param args
	 */
	public static void main(String[] args) throws NetworkException, IOException {
		/**###### INITIALISIERUNGEN ######*/
		
		//###### POSTS VON REDDIT ABHOLEN + POSTS IN EINE DATEI SCHREIBEN(Birger) ######
		RedditPosts.writeTrainingsSetToFile("./reddit.txt", "2kdpuj");
		
	}

}
