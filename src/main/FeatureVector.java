package main;

import java.util.HashMap;
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
}
