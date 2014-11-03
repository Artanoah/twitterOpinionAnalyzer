package normalisation;


import java.io.IOException;

import org.junit.Test;

public class partofSpeechAnalysisText {

	@Test
	public void test() throws IOException {
		PartOfSpeechAnalysis.normaliseString("I have big cock", true, true);
	}

}
