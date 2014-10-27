package partOfSpeechAnalysis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class partofSpeechAnalysisText {

	@Test
	public void test() throws IOException {
		PartOfSpeechAnalysis.filterTextFile(true);
	}

}
