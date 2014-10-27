package partOfSpeechAnalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tartarus.snowball.ext.PorterStemmer;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;

public class PartOfSpeechAnalysis {
	
	//Den Parser binden - in diesem Fall ein Part-of-Speech-Parser fuer englischen Text
    private final static String PCG_MODEL = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";        
    
    //Initialisierung der Stanford-Parser-Komponenten
    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");
    private final LexicalizedParser parser = LexicalizedParser.loadModel(PCG_MODEL);
    private final static PartOfSpeechAnalysis analysis = new PartOfSpeechAnalysis();
    private final static List<String>stringRemainingTags = new ArrayList<String>(Arrays.asList("NN", "VBP", "VBZ", "NN", "NNP"));
    private final static List<String>textFileRemainingTags = new ArrayList<String>(Arrays.asList("JJ", "JJR", "JJS", "NN", "NNP", "NNPS", "NNS", "RB", "RBR", "RBS", "RP", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));

    
    public Tree parse(String str) {                
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer =
            tokenizerFactory.getTokenizer(
                new StringReader(str));    
        return tokenizer.tokenize();
    }

    public static String partOfSpeech(String str, Boolean stamming) { 
    	String result = "";
    	Stemmer stemmer = new Stemmer();
        Tree tree = analysis.parse(str);  
        List<Tree> leaves = tree.getLeaves();
        //Concat words with their tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(tree);
            if(stamming){
            	stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
            	stemmer.stem();
            	result = result.concat((stemmer.toString() + "-" + parent.label().value() + " "));
            }
            else{
            	result = result.concat((leaf.label().value() + "-" + parent.label().value() + " "));
            }
            
        }
        return result;             
    }
    
    
    public static String partOfSpeechWithFilter(String str, Boolean stamming) { 
    	String result = "";
    	Stemmer stemmer = new Stemmer();
        Tree tree = analysis.parse(str); 
        List<Tree> leaves = tree.getLeaves();
        //Concat words with their tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(tree);
            if(stringRemainingTags.contains(parent.label().value())){
            	if(stamming){
                	stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
                	stemmer.stem();
                	result = result.concat((stemmer.toString() + "-" + parent.label().value() + " "));
                }
                else{
                	result = result.concat((leaf.label().value() + "-" + parent.label().value() + " "));
                }
            }
        }
        return result;             
    }
    
    public static void filterTextFile(Boolean stamming) throws IOException{
    	FileReader fr = new FileReader("new_dictionary.txt");
        BufferedReader br = new BufferedReader(fr);
        
        FileWriter fw = new FileWriter("stammed_dictionary.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        
        Stemmer stemmer = new Stemmer();
        
        String temp ="";
        while(br.ready()){
        	temp = br.readLine();
        	Tree tree = analysis.parse(temp);
        	List<Tree> leaves = tree.getLeaves();
        	for(Tree leaf : leaves) {
        		Tree parent = leaf.parent(tree);
        		if(parent.label().value() != null){
        			if(textFileRemainingTags.contains(parent.label().value())){
        				if(stamming){
        					stemmer.add(leaf.label().value().toCharArray(), leaf.label().value().length());
        					stemmer.stem();
        					System.out.println(stemmer.getResultBuffer().toString() + " - "  + stemmer.toString());
        					bw.write(stemmer.toString() + "-" + parent.label().value() + "\n");
        				}
        				else{
        					bw.write(leaf.label().value() + "-" + parent.label().value() + "\n");
        				}
            		
            		}
            	}
        	}	
        }
        br.close();
        bw.close();
    }
    
    
    
    
    
}