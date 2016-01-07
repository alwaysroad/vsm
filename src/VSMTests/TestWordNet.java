package VSMTests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import edu.mit.jwi.*;
import edu.mit.jwi.data.*;
import edu.mit.jwi.item.*;

public class TestWordNet {

	public static void main(String... args) throws IOException,
			InterruptedException {

		URL url = new URL("file", null,
				"/group/project/vsm-nfs/WordNet-3.0/dict");
		IDictionary wnDir = new Dictionary(url);
		IRAMDictionary dict = new RAMDictionary(wnDir, ILoadPolicy.NO_LOAD);
		dict.open();
		dict.load(true);

		String word = "dog";
		for (POS pos : POS.values()) {
			IIndexWord idxWord = dict.getIndexWord("jas mnsdbdmnf mnsf mndf ",
					pos);
			IWordID wordID = null;
			IWord word1 = null;
			if (idxWord != null) {
				wordID = idxWord.getWordIDs().get(0);
				word1 = dict.getWord(wordID);
				System.out.println(" Lemma = " + word1.getLemma());
				System.out.println(" Gloss = " + word1.getSynset().getGloss());
			} else {
				System.out.println("idxWord is null");
			}

		}
	}
}
