package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final int POS = 1;
	public static final int NEU = 0;
	public static final int NEG = -1;
	public static final int MAX_NUM_OF_WORDS = 150;
	public static final int MAX_THREADS = 2;
	public static final List<String> REMAININGTAGS = new ArrayList<String>(Arrays.asList("JJ", "JJR", "JJS", "NN", "NNP", "NNPS", "NNS", "RB", "RBR", "RBS", "RP", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
	public static final double ERROR_TOLLERANCE = 0.3;
	public static final int AMOUNT_RANDOM_TREES = 30;
}
