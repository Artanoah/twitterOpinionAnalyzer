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
	public static final int CLASSIFY_POST_MAIN_FLAG = 0;
	public static final int UTIL_FLAG = 1;
	public static final List<String> REMAININGTAGS = new ArrayList<String>(Arrays.asList("JJ", "JJR", "JJS", "RB", "RBR", "RBS", "RP", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
	public static final double ERROR_TOLLERANCE = 0.01;
	public static final int MAXIMAL_ITERATIONS = 2000;
	public static final int AMOUNT_RANDOM_TREES = 300;
}
