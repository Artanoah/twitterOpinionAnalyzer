package normalisation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.Constants;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.parser.lexparser.Options.LexOptions;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;

public class PartOfSpeechAnalysis {

    //Initialise the Stanford-Parser-Components
    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");
    //Bind the part-of-speech-parser for english language
    private final static String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    //Options for the Lexparser
    private final static String[] options = { "-maxLength", Integer.toString(Constants.MAX_NUM_OF_WORDS)};
    private final static LexicalizedParser parser = LexicalizedParser.loadModel(grammar, options);
    private final static PartOfSpeechAnalysis analysis = new PartOfSpeechAnalysis();
    //Kind of Words to remain filtering a String
    private final static List<String>stringRemainingTags = new ArrayList<String>(Constants.REMAININGTAGS);
    //Kind of Words to remain filtering a .txt-file
    private final static List<String>textFileRemainingTags = new ArrayList<String>(Arrays.asList("JJ", "JJR", "JJS", "NN", "NNP", "NNPS", "NNS", "RB", "RBR", "RBS", "RP", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));

    
    /**
     * Annotates the part-of-speech attribute to every word of the given String str
     * If Boolean stamming is true, the words are also transformed into the stem
     * @param str the String to normalise
     * @param stamming whether the words in the String str should be stemmed
     * @return the normalised String
     */
    public static String normaliseString(String str, Boolean stamming, Boolean partOfSpeech) {
    	String result = "";
    	Stemmer stemmer = new Stemmer();
        Tree tree = analysis.parse(str);  
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
        return result;             
    }
    
    
    /**
    * Annotates the part-of-speech attribute to every word of the given String str
    * whereas just the part-of-speech types in normalisation.PartOfSpeechAnalysis.stringRemainingTags will remain
    * If Boolean stamming is true, the words are also transformed into the stem
    * @param str the String to normalise
    * @param stamming whether the words in the String str should be stemmed
    * @return the normalised String
    */
    public static String normaliseAndFilterString(String str, Boolean stamming, Boolean partOfSpeech) { 
    	String result = "";
    	Stemmer stemmer = new Stemmer();
        Tree tree = analysis.parse(str); 
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
        return result;             
    }
    
    /**
     * Annotates the part-of-speech attribute to every Word of the given String str
     * whereas just the part-of-speech types in normalisation.PartOfSpeechAnalysis.textFileRemainingTags will remain
     * the results are saved in stammed_dictionary.txt whereas identical words aren't listed
     * If Boolean stamming is true, the words are also transformed into the stem
     * @param textfile the textfile to normalise
     * @param stamming whether the words in the textfile should be stemmed
     */
    public static void normaliseAndFilterTextFile(String textfile, Boolean stamming) throws IOException{
    	FileReader fr = new FileReader(textfile);
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter fw = new FileWriter("stammed_dictionary.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        
        Set<String> result = new HashSet<String>();
        
        Stemmer stemmer = new Stemmer();
        
        String temp ="";
        while(br.ready()){
        	temp = br.readLine();
        	Tree tree = analysis.parse(temp);
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
        for(String s : result){
    		bw.write(s + "\n");
    	}
        br.close();
        bw.close();
    }
    
    

    private Tree parse(String str) {
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    
    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer = tokenizerFactory.getTokenizer(new StringReader(str));    
        return tokenizer.tokenize();
    }

    
    
    
}