package normalisation;


import java.io.IOException;

import org.junit.Test;

public class partofSpeechAnalysisText {

	@Test
	public void test() throws IOException {
		PartOfSpeechAnalysis.normaliseAndFilterTextFile("new_dictionary.txt", true);
	}

}
