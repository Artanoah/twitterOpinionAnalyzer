package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SVM_Main {


	/**
	 * Erstellt für ein gegenes Textfile von Reddit-Posts einen Eingabevektoren für eine externe
	 * Verarbeitung durch eine libsvm-Support-Vector-Machine
	 * @param args 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		ListOfAllWords listOfAllWords = new ListOfAllWords();
		listOfAllWords.loadFromFile("listOfAllWords.dump");
		String svm_input = "postsToTest";
	    BufferedWriter svm_bw = new BufferedWriter(new FileWriter(svm_input));
		List<FeatureVector> featureVectors = Util.getStemmedPostsAndCreateFiles("./reddit_testset_svm.txt");
		final float ZERO = (float) 0.0;
		
	    for(FeatureVector fv : featureVectors){
	        if (fv.getValue() > 0){
	        	svm_bw.write("+" + Integer.toString(fv.getValue()));		
	        }
	        else if (fv.getValue() < 0){
	        	svm_bw.write(Integer.toString(fv.getValue()));
	        }
	        else {
	        	continue;
	        };
	        List<String> stringsFromFeatureVectorOutOfTrainingsData = listOfAllWords.getList();
	        Map<String, Integer>fvMap = fv.getMap();
	        Integer sLength = FeatureVector.countAppearingWordsOfVector(fvMap);
	        Set<String> wordsFromThisFeatureVector = fvMap.keySet();
	        int i = 1;
	        
	        for(String s : stringsFromFeatureVectorOutOfTrainingsData){
	        	svm_bw.write(" ");
	        	if(wordsFromThisFeatureVector.contains(s)){
		        	svm_bw.write(i + ":" + (float)fvMap.get(s)/(float)sLength);
	        	}
	        	else{
	        		svm_bw.write(i + ":" + ZERO);
	        	}
	        	i = i+1;
	        }
	        svm_bw.write("\n");
	    }
	    svm_bw.close();
	}
}
