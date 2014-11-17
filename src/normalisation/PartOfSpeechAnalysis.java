package normalisation;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import main.Constants;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Tree;

public abstract class PartOfSpeechAnalysis extends Thread{

    //Initialise the Stanford-Parser-Components
    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "invertible=true");
    //Bind the part-of-speech-parser for english language
    private final static String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    //Options for the Lexparser
    private final static String[] options = { "-maxLength", Integer.toString(Constants.MAX_NUM_OF_WORDS)};
    private final  LexicalizedParser parser = LexicalizedParser.loadModel(grammar, options);
    //Kind of Words to remain filtering a String
    protected final static List<String>stringRemainingTags = new ArrayList<String>(Constants.REMAININGTAGS);
    //Kind of Words to remain filtering a .txt-file
    protected final static List<String>textFileRemainingTags = new ArrayList<String>(Constants.REMAININGTAGS);


    protected Tree parse(String str) {
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    
    protected List<CoreLabel> tokenize(String str) {
        Tokenizer<CoreLabel> tokenizer = tokenizerFactory.getTokenizer(new StringReader(str));    
        return tokenizer.tokenize();
    }

    
    
    
}