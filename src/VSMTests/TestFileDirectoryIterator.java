package VSMTests;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import VSMUtilityClasses.VSMUtil;

public class TestFileDirectoryIterator {
	@Test
	public void testFileIterator() {
		File[] files = VSMUtil
				.getFiles("/Users/sameerkhurana10/nltk_data/corpora/treebank/parsed/");
		/*
		 * Display the files and their absolute paths
		 */
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				// showFiles(file.listFiles()); // Calls same method again.
			} else {
				System.out.println("File: " + file.getName()
						+ " absolute path:: " + file.getAbsolutePath());
			}
		}
	}
}
