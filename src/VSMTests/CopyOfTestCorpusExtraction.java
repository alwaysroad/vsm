package VSMTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.compress.compressors.CompressorException;

import VSMUtilityClasses.VSMUtil;

public class CopyOfTestCorpusExtraction {

	private static StringBuilder builder;
	private static int i = 27155;

	public static void main(String... args) {

		/*
		 * Checking whether the program is running on bravas or not
		 */
		InetAddress ip;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Your current IP address : " + ip);
			System.out.println("Your current Hostname : " + hostname);

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		/*
		 * The directory root
		 */
		String directoryRoot = "/group/corpora/public/bllip/bllip_nanc_mem/data";

		/*
		 * Get all the directories into the data directory
		 */
		File[] files = VSMUtil.getFiles(directoryRoot);

		Collections.sort(Arrays.asList(files), new Comparator<File>() {
			public int compare(File file1, File file2) {
				String a = file1.getName().replaceFirst("^0+(?!$)", "");
				String b = file2.getName().replaceFirst("^0+(?!$)", "");
				return Integer.parseInt(a) - Integer.parseInt(b);
			}
		});

		for (File file : files) {
			System.out.println(file.getName());
		}

		ArrayList<File> filePaths = VSMUtil.getFilePaths(files);

		System.out.println("***Total Files in BLLIP Corpus***"
				+ filePaths.size());
		/*
		 * Testing
		 */

		for (int j = 27155; j < filePaths.size(); j++) {

			File file = filePaths.get(i);
			System.out.println(file.getName());

			formDocument(file.getAbsolutePath());
			writeDocument();

		}

	}

	private static void writeDocument() {
		i++;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(
					"/group/project/vsm-afs/treeCorpus/lsa_corpus/doc_" + i
							+ ".txt"));
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	private static void formDocument(String path) {
		builder = new StringBuilder();

		ArrayList<String> trees = VSMUtil.extractTrees(path);
		for (String tree : trees) {
			builder.append(tree);
			builder.append("\n");

		}
	}
}
