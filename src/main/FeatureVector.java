package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureVector {
	Map<String, Integer> map = new HashMap<String, Integer>();
	int value = 0;
	
	/**
	 * 
	 * @param map
	 * @param classifyer
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
