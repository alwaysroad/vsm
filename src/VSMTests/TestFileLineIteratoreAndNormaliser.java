package VSMTests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.berkeley.nlp.syntax.Tree;
import VSMUtilityClasses.FileLineIterator;
import VSMUtilityClasses.PTBTreeNormaliser;

public class TestFileLineIteratoreAndNormaliser {
	@Test
	public void testFileLineIterator() {
		String URI = "/Users/sameerkhurana10/nltk_data/corpora/treebank/combined/12799-ny950322-3000-3499.txt";
		PTBTreeNormaliser ptbTreeNormaliser = new PTBTreeNormaliser(true);
		FileLineIterator fileLineIterator = new FileLineIterator(URI,
				ptbTreeNormaliser);
		while (fileLineIterator.hasNext()) {
			Tree<String> normalisedTree = fileLineIterator.next();
			System.out.println(normalisedTree);

		}
	}
}
