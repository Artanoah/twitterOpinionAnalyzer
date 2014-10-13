package partOfSpeechAnalysis;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final static List<String>remainingTags = new ArrayList<String>(Arrays.asList("NN", "VBP", "VBZ", "NN", "NNP"));

    
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

    public static String partOfSpeech(String str) { 
    	String result = "";
        Tree tree = analysis.parse(str);  

        List<Tree> leaves = tree.getLeaves();
        //Concat words with their tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(tree);
            result = result.concat((leaf.label().value() + "-" + parent.label().value() + " "));
        }
        return result;             
    }
    
    
    public static String partOfSpeechWithStaming(String str) { 
    	String result = "";
        Tree tree = analysis.parse(str); 
        List<Tree> leaves = tree.getLeaves();
        //Concat words with their tags
        for (Tree leaf : leaves) { 
            Tree parent = leaf.parent(tree);
            if(remainingTags.contains(parent.label().value())){
            	result = result.concat(leaf.label().value() + "-" + parent.label().value() + " ");
            }
        }
        return result;             
    }
    
    
    
    
    
}