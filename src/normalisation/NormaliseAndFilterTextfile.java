package normalisation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.trees.Tree;

/**
 * Annotates the part-of-speech attribute to every Word of the given String str
 * whereas just the part-of-speech types in normalisation.PartOfSpeechAnalysis.textFileRemainingTags will remain
 * the results are saved in stammed_dictionary.txt whereas identical words aren't listed
 * If Boolean stamming is true, the words are also transformed into the stem
 * @param textfile the textfile to normalise
 * @param stamming whether the words in the textfile should be stemmed
 */
public class NormaliseAndFilterTextfile extends PartOfSpeechAnalysis{
	 
	String textfile;
	Boolean stamming;
	
	public NormaliseAndFilterTextfile(String textfile, Boolean stamming){
		this.textfile = textfile;
		this.stamming = stamming;
	}
    
	
	public  void run(){
    	FileReader fr = null;
		try {
			fr = new FileReader(textfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter fw = null;
		try {
			fw = new FileWriter("stammed_dictionary.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedWriter bw = new BufferedWriter(fw);
        
        Set<String> result = new HashSet<String>();
        
        Stemmer stemmer = new Stemmer();
        
        String temp ="";
        try {
			while(br.ready()){
				temp = br.readLine();
				Tree tree = parse(temp);
				List<Tree> leaves = tree.getLeaves();
				for(Tree leaf : leaves) {
					Tree parent = leaf.parent(tree);
					if(parent.label().value() != null){
						if(stamming){
							if(textFileRemainingTags.contains(parent.label().value())){
								stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
								stemmer.stem();
								System.out.println(stemmer.getResultBuffer().toString() + " - "  + stemmer.toString());
								result.add(stemmer.toString());
							}
							else{
								result.add(leaf.label().value());
							}
			    		
			    		}
			    	}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for(String s : result){
    		try {
				bw.write(s + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
