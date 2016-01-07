package VSMTests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import VSMUtilityClasses.VSMUtil;

public class TestSickCorpusReader {

	public static void main(String... args) throws IOException {
		List<String> lines = new ArrayList<String>();
		InputStream is = null;
		BufferedReader br = null;
		URL url = null;
		List<CombinationPairs> pairs = new ArrayList<CombinationPairs>();

		FileWriter tulika = new FileWriter(
				new File(
						"/Users/sameerkhurana10/training_corpus/SICK_trial_trees_for_tagger.txt"));
		HashMap<Integer, Double> map = VSMUtil.getGoldStandard();
		System.out.println(map);
		FileWriter tulika1 = new FileWriter(new File(
				"/Users/sameerkhurana10/training_corpus/gold-trial.txt"));

		try {

			is = new FileInputStream(new File(
					"/Users/sameerkhurana10/training_corpus/SICK_trial.txt"));
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strLine;
			while ((strLine = br.readLine()) == null) {
				System.out.println("+++++NULL+++");
			}

			while ((strLine = br.readLine()) != null) {
				lines.add(strLine);
			}

			System.out.println(lines.size());
		} catch (IOException e) {
			System.out.println("++EXCPETION WHILE READING THE FILE++++" + e);
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(is);
		}

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			String[] linesplit = line.split("\t");

			// pair_ID sentence_A sentence_B relatedness_score
			// entailment_judgment
			String text1 = linesplit[1];
			String text2 = linesplit[2];

			CombinationPairs pair = new CombinationPairs(
					"/Users/sameerkhurana10/training_corpus/SICK_trial.txt");
			pair.setID1(linesplit[0] + "_1");
			pair.setID2(linesplit[0] + "_2");
			pair.setText1(text1);
			pair.setText2(text2);

			pairs.add(pair);
		}

		System.out.println(pairs.size());

		int count = 0;
		for (CombinationPairs pair : pairs) {
			// System.out.println(count++);
			tulika.write("<s> " + pair.getText1() + " </s>");
			tulika.write("\n");
			tulika.write("<s> " + pair.getText2() + " </s>");
			tulika.write("\n");
		}

		tulika.close();

		for (int i = 0; i < map.keySet().size(); i++) {
			tulika1.write(Double.toString(map.get(i + 1)));
			tulika1.write("\n");
		}

		tulika1.close();

	}

}
