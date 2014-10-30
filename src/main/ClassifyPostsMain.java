package main;

public class ClassifyPostsMain {

	/**
	 * <Text, Bewertung> Objekte aus der Datenbank holen. Die Texte werden dann 
	 * zuerst von Fehlern bereinigt (Spelling Correction), mit Part-of-Speech-Tagging 
	 * bearbeitet und dann mit Stemming heruntergebrochen. </br>
	 * Aus diesen "korrigierten" Texten werden "Bag-Of-Words" (Hash-Maps) erstellt
	 * die zusammen mit der dazugehoerigen Bewertung in ein FeatureVector-Objekt geschoben werden.</br>
	 * Mit diesen FeatureVector-Objekten werden dann die jeweiligen Lernverfahren angestossen. 
	 * @param args
	 */
	public static void main(String[] args) {
		//###### INITIALISIERUNGEN ######
		
		//###### OBJEKTE AUS DER DATENBANK HOLEN (Birger) ######
		//input: ()
		//output: Map<String, Value> -> Map an Text zu Bewertung
		
		//###### TEXTE VON FEHLERN BEREINIGEN (Steffen) ######
		//input: Map<String, Value>
		//output: Map<String, Value> -> Korrigierter Text zu Bewertung
		
		//###### PART OF SPEECH TAGGING + STEMMING (Fabian) ######
		//input: Map<String, Value>
		//for(String s : input.keyset){
		//	input.put(normalisation.PartOfSpeechAnalysis.normaliseAndFilterString(s, true), input.get(s));
		//}
		//return input;
		//output: Map<String, Value> -> Getagter Text zu Bewertung
		//output: List<String> -> Liste aller zu benutzenden Woerter
		
		
		//###### BAG-OF-WORDS ERSTELLEN (Kai) ######
		//input: Map<String, Value>
		//output: Map<Map<String, Value>, Value> -> Liste an Bags of Words (Map<Map<Wort, Haeufigkeit>, Bewertung>)
		
		//###### VOLLSTAENDIGE LISTE ALLER WOERTER ERZEUGEN (Steffen) ######
		//input: List<String>
		//output: ListOfAllWords
		
		//###### VECTOR OBJEKTE ERSTELLEN (Steffen) ######
		//input: Map<Map<String, Value>, Value> -> Liste an Bags of Words (Map<Map<Wort, Haeufigkeit>, Bewertung>)
		//output: List<FeatureVector> -> Hier sind nun auch nicht vorkommende Woerter in der Map enthalten
	}

}
