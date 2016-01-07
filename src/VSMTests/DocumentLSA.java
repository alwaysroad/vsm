package VSMTests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import edu.berkeley.nlp.util.StringUtils;

public class DocumentLSA {

	private static String URI = "/group/project/vsm-afs/treeCorpus/lsa_corpus";
	private static File[] corpusFiles;
	private static PennTreeReader treeReader;
	private static StringBuilder builder;
	private static String mode;
	private static int fileCount = 0;
	private static String sentence;
	private static LinkedList<String> lastTwoTokens;
	private static int sentenceCount;

	static {
		lastTwoTokens = new LinkedList<String>();
	}

	public static void main(String... args) {
		getUserPref(args);
		setFileCount();
		// getCorpusFile();
		combineAllFile(args);
	}

	private static void setFileCount() {
		if (mode.equalsIgnoreCase("Train")) {
			fileCount = 0;
		} else if (mode.equalsIgnoreCase("Test")) {
			fileCount = 35001;
		} else {
			System.out.println("PLEASE GIVE A VALID ARGUMENT");
			System.exit(0);
		}

	}

	private static void getUserPref(String[] args) {
		if (args.length > 0) {
			mode = args[0];
		} else {
			System.out.println("++PLEASE GIVE AN ARGUMENT, Train or Test");
			System.exit(0);
		}
	}

	private static void formLSADocument() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(
					"/group/project/vsm-afs/treeCorpus/cca_doc"
							+ mode.toLowerCase() + ".txt"), true);
			writer.write(sentence);
			writer.write("\n");
			writer.write("\n");
			writer.write("DOCSTART-X-0");
			writer.write("\n");
			writer.write("\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.close();
					sentenceCount = 0;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private static void combineAllFile(String[] args) {

		// System.out.println("Combining all FILES");
		// sortByNumber(corpusFiles);

		// main: for (int j = 0; j < corpusFiles.length; j++) {
		//
		// builder = new StringBuilder();
		//
		// System.out.println("File Name: " + corpusFiles[j].getName());
		getTreeReader(args);

		while (treeReader.hasNext()) {
			sentenceCount++;
			builder = new StringBuilder();
			formStringBuilder(treeReader.next());
			if (!builder.toString().isEmpty()) {
				sentence = builder.toString();
				sentence = sentence.trim();
				sentence = sentence.toLowerCase();
				System.out.println(sentence);
				lastTwoTokens.clear();
				addLastTwoSentenceTokens();
				formLSADocument();
			}
		}

		// if (mode.equalsIgnoreCase("Train")) {
		// if (fileCount == 35000) {
		// //break main;
		// }
		// } else if (mode.equalsIgnoreCase("Test")) {
		// if (fileCount == corpusFiles.length - 1) {
		// //break main;
		// }
	}

	// }

	// System.out.println("DONE");
	//
	// }

	private static void getTreeReader(String[] args) {
		if (args.length > 1) {
			try {
				treeReader = VSMUtil
						.getTreeReader("/group/project/vsm-afs/treeCorpus/"
								+ args[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("+++PLEASE ENTER A CORPUS FILE NAME++");
			System.exit(0);
		}

	}

	private static void addLastTwoSentenceTokens() {

		String[] tokens = sentence.split(" ");
		lastTwoTokens.add(tokens[tokens.length - 2]);
		lastTwoTokens.add(tokens[tokens.length - 1]);
		// last word

	}

	private static void formStringBuilder(Tree<String> next) {
		List<String> tokens = next.getTerminalYield();
		if (!lastTwoTokens.isEmpty()) {
			tokens.addAll(0, lastTwoTokens);
		}
		for (int i = 0; i < tokens.size(); i++) {
			if (org.apache.commons.lang3.StringUtils.isAlphanumeric(tokens
					.get(i))) {

				builder.append(tokens.get(i));
				if (i == tokens.size() - 1) {
				} else {
					builder.append(" ");
				}
			}
		}

	}

	private static String filterSentence(String s) {

		s = s.replaceAll("[^a-zA-Z0-9]", "");
		return s;

	}

	private static void getTreeReader(File file) {
		try {
			treeReader = VSMUtil.getTreeReader(file.getPath());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private static void getCorpusFile() {
		System.out.println("GETTING ALL FILES");
		corpusFiles = new File(URI).listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return !pathname.isHidden();
			}
		});

	}

	// public static void sortByNumber(File[] files) {
	// Arrays.sort(files, new Comparator<File>() {
	//
	// @Override
	// public int compare(File o1, File o2) {
	// int n1 = extractNumer(o1.getName());
	// int n2 = extractNumer(o2.getName());
	// return n1 - n2;
	// }
	//
	// private int extractNumer(String name) {
	//
	// int i = 0;
	//
	// try {
	// int s = name.indexOf('_') + 1;
	// int e = name.lastIndexOf('.');
	// String number = name.substring(s, e);
	// i = Integer.parseInt(number);
	// } catch (Exception e) {
	// i = 0;
	// }
	// return i;
	//
	// }
	// });
	// }

}
