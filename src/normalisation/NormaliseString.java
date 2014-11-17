package normalisation;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

/**
 * Annotates the part-of-speech attribute to every word of the given String str
 * If Boolean stamming is true, the words are also transformed into the stem
 * @param str the String to normalise
 * @param stamming whether the words in the String str should be stemmed
 * @return the normalised String
 */
public class NormaliseString extends PartOfSpeechAnalysis{
	
	String str;
	int value;
	Boolean stamming;
	Boolean partOfSpeech;
	
	public NormaliseString(String str, int value, Boolean stamming, Boolean partOfSpeech){
		this.str = str;
		this.value = value;
		this.stamming = stamming;
		this.partOfSpeech = partOfSpeech;
	}
	
	
    public void run() {
    	String result = "";
    	Stemmer stemmer = new Stemmer();
        Tree tree = parse(str);  
        List<Tree> leaves = tree.getLeaves();
        //Concat words with their tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(tree);
            if(partOfSpeech){
            	if(stamming){
                    stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
                    stemmer.stem();
                    result = result.concat((stemmer.toString() + "-" + parent.label().value() + " "));
                }
            	else{
            		result = result.concat((leaf.label().value() + "-" + parent.label().value() + " "));
                }
            }
            else{
            	if(stamming){
                    stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
                    stemmer.stem();
                    result = result.concat((stemmer.toString() +  " "));
            	}
            	else{
            		result = result.concat((leaf.label().value() + " "));
                }
            }
        
        }
        if(!(result.equals(""))){
            main.ClassifyPostsMain.addStemmedPost(result, value);
        }
    }
	
}
