package normalisation;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

/**
 * Annotates the part-of-speech attribute to every word of the given String str
 * whereas just the part-of-speech types in normalisation.PartOfSpeechAnalysis.stringRemainingTags will remain
 * If Boolean stamming is true, the words are also transformed into the stem
 * @param str the String to normalise
 * @param stamming whether the words in the String str should be stemmed
 * @return the normalised String
 */
public class NormaliseAndFilterString extends PartOfSpeechAnalysis{
	
	String str;
	int value;
	Boolean stamming;
	Boolean partOfSpeech;
	int packageFlag;
	
	public NormaliseAndFilterString(String str, int value, Boolean stamming, Boolean partOfSpeech, int packageFlag){
		this.str = str;
		this.value = value;
		this.stamming = stamming;
		this.partOfSpeech = partOfSpeech;
		this.packageFlag = packageFlag;
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
                	if(stringRemainingTags.contains(parent.label().value())){
                    	stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
                    	stemmer.stem();
                    	 result = result.concat((stemmer.toString() + "-" + parent.label().value() + " "));
                    }
                }
            	else{
            		result = result.concat((leaf.label().value() + "-" + parent.label().value() + " "));
                }
            }
            else{
            	if(stamming){
                	if(stringRemainingTags.contains(parent.label().value())){
                    	stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
                    	stemmer.stem();
                    	result = result.concat((stemmer.toString() + " "));
                    }
            	}
            	else{
            		result = result.concat((leaf.label().value() + " "));
                }
            }
        }
        if(!(result.equals(""))){
        	if(packageFlag == 0){
        		main.ClassifyPostsMain.addStemmedPost(result, value);
        	}
        	else if(packageFlag == 1){
        		main.Util.addStemmedPost(result, value);
        	}
        
        }
    }
}
