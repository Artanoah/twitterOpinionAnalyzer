package main;

import java.io.IOException;

import contentSource.RedditPosts;

public class PushToDatabaseMain {

	/**
	 * JSon Objekte werden aus der Datei mit per Hand hinzugefuegter Bewertung ausgelesen. 
	 * Dann werden Tupel <Post, Bewertung> generiert und in die Datenbank gepusht.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//###### INITIALISIERUNGEN ######

		//###### TUPEL AUSLESEN + TUPEL IN DIE DATENBANK SCHIEBEN(Birger) ######
		RedditPosts.pushTrainingsSetToRedis("./reddit.txt");
	}
}
