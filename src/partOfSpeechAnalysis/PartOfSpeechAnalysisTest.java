package partOfSpeechAnalysis;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class PartOfSpeechAnalysisTest {

	@Test
	public void test() {
		ArrayList<String> stamming = new ArrayList<String>();
		stamming.add("PRP");
		stamming.add("VBP");
		stamming.add("VBZ");
		stamming.add("NN");
		stamming.add("NNP");
		System.out.println(PartOfSpeechAnalysis.partOfSpeech("I fucking hate the stupid pig Obama"));
	}

}
