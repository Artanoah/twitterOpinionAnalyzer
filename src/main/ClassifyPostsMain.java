package main;

public class ClassifyPostsMain {

	/**
	 * <Text, Bewertung> Objekte aus der Datenbank holen. Die Texte werden dann 
	 * zuerst von Fehlern bereinigt (Spelling Correction), mit Part-of-Speech-Tagging 
	 * bearbeitet und dann mit Stemming heruntergebrochen. </br>
	 * Aus diesen "korrigierten" Texten werden "Bag-Of-Words" (Hash-Maps) erstellt
	 * die zusammen mit der dazugehoerigen Bewertung in ein Vector-Objekt geschoben werden.</br>
	 * Mit diesen Vector-Objekten werden dann die jeweiligen Lernverfahren angestossen. 
	 * @param args
	 */
	public static void main(String[] args) {
		//###### INITIALISIERUNGEN ######
		
		//###### OBJEKTE AUS DER DATENBANK HOLEN (Kai) ######
		//input: ()
		//output: Map<String, Value> -> Map an Text zu Bewertung
		
		//###### TEXTE VON FEHLERN BEREINIGEN (Steffen) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Korrigierter Text zu Bewertung
		
		//###### PART OF SPEECH TAGGING (Fabian) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Getagter Text zu Bewertung
		
		//###### STEMMING (Fabian) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Gestemmter Text zu Bewertung
		//output: List<String> -> Liste aller zu benutzenden Woerter
		
		//###### BAG-OF-WORDS ERSTELLEN (Kai) ######
		//input: Map<String, Value>
		//output: List<Map<String, Value>> -> Liste an Bags of Words
		
		//###### VOLLSTAENDIGE LISTE ALLER WOERTER ERZEUGEN (Steffen) ######
		
		//###### VECTOR OBJEKTE ERSTELLEN (Steffen) ######
	}

}
