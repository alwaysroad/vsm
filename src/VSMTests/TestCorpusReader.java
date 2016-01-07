package VSMTests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorException;
import org.junit.Before;
import org.junit.Test;

import VSMUtilityClasses.BLLIPCorpusReader;

public class TestCorpusReader {

	@Test
	public void testGetBufferedReaderForBZ2File() throws CompressorException,
			IOException {
		String URI = "/Users/sameerkhurana10/nltk_data/corpora/00199-lw940626-0-499.bz2";
		BufferedReader brZ = BLLIPCorpusReader.getBufferedReaderForBZ2File(URI);
		/*
		 * Now the code for storing the trees in a file, named trees.txt
		 */
		File file = new File("/Users/sameerkhurana10/Documents/trees.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		/*
		 * True will ensure that we append the tree to our already existing file
		 * and not overwrite it
		 */
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		String line = null;
		boolean flag = false;
		int count = 1;
		int count1 = 0;
		while ((line = brZ.readLine()) != null) {
			/*
			 * When the line begins with 5 put the flag to true
			 */
			if (line.contains("50") && count1 == 0) {
				flag = true;
				System.out.println("hi");
				count1 = 1;
			}

			/*
			 * If the flag is true and the line starts with a bracket i.e. it is
			 * a tree, get that line
			 */
			if ((flag == true) && (line.charAt(0) == '(')) {
				System.out.println(line);
				bw.write(line);
				bw.newLine();
				bw.newLine();
				count++;
			}

			/*
			 * After getting three trees wait for 50 to come
			 */
			if (count == 1) {
				flag = false;
				/*
				 * Reseting the count of course
				 */
				count = 0;
				count1 = 0;
			}
		}

	}
}
