package contentSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CSVFile {
	
	File csvFile = null;
	ArrayList<String[]> allLines = new ArrayList<String[]>();
	
	public CSVFile(String filePath) throws IOException{
		csvFile = new File(filePath);
		
		//Datei auslesen
		BufferedReader br = new BufferedReader(new FileReader(csvFile));
		for(String line; (line = br.readLine()) != null; ) {
	        allLines.add(line.split(","));
	    }
		br.close();
	}
	
	public Map<String,Integer> getPostsFromFile(){
		Map<String,Integer> result = new HashMap<String,Integer>();
		
		for(String[] line:allLines){
			int classifiedValue = Integer.parseInt(line[1]);
			String postText = "";
			if(!line[0].equals("Item")){
				if(line.length > 4){
					String temp = "";
					for(int i = 3;i <= line.length-1;i++ ){
						temp = temp + line[i];
					}
					postText = temp;
				} else {
					postText = line[3];
				}
				result.put(postText, classifiedValue);
			}
		}
		
		return result;
	}

}
