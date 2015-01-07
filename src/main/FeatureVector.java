package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse zum Halten einer <code>Map&lt;String, Integer&gt;</code> und
 * eines dazugehoerigen Wertes. Die Map soll das Verhaeltnis von Wort
 * zu Haeufigkeit des Wortes herstellen und der dazugehoerige Wert die
 * Bewertung dieser Map.
 * 
 * @author Steffen Giersch
 */
public class FeatureVector implements Serializable {

	private static final long serialVersionUID = 1L;
	Map<String, Integer> map = new HashMap<String, Integer>();
	int value = 0;
	
	/**
	 * Erstellt einen neuen FeatureVector mit der Map <code>map</code>
	 * und der Bewertung <code>classifyer</code>.
	 * 
	 * @param map <code>Map&lt;String, Integer&gt;</code> 
	 * @param classifyer <code>int</code>
	 */
	public FeatureVector(Map<String, Integer> map, int classifyer) {
		this.map = map;
		value = classifyer;
	}
	
	//###### Getter und Setter ######
	
	public Map<String, Integer> getMap() {
		return map;
	}

	public int getValue() {
		return value;
	}
	
	public String toString() {

		String akku = "";
		
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			if(entry.getValue() != 0) {
				akku += entry.getKey() + "=" + entry.getValue() + ", ";
			}
		}
		
		return akku;
	}

    public static List<String>seperateWordsOfString(Map<String, Integer> input){
    	List<String> result = new ArrayList<String>();
    	input.forEach((key, value) -> {
			String[] array = key.split(" ");
			for(int i = 0; i < array.length ; i++){
				if(!(result.contains(array[i]))){
					result.add(array[i]);
				}
			}
		});
    	return result;
    }
    
    /**
     * Zaehlt wie viele verschiedene Woerter ein mal oder oefter in der
     * Map <code>intput</code> enthalten sind 
     * 
     * @param input <code>Map&lt;String, Integer&gt;</code>
     * @return <code>int</code>
     */
    public static Integer countAppearingWordsOfVector(Map<String, Integer> input){
		Integer result = 0;	
		for(String s : input.keySet()){
			if(input.get(s) > 0){
				result = result +1;
			}
		}
		return result;
    }
}
