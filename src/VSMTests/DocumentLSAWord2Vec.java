package VSMTests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class DocumentLSAWord2Vec {

	private static String URI = "/group/project/vsm-afs/treeCorpus/lsa_corpus";
	private static File[] corpusFiles;
	private static PennTreeReader treeReader;
	private static StringBuilder builder;
	private static String corpus;
	private static File sickFile;

	public static void main(String... args) {
		getUserInput(args);
		getCorpusFile();
		combineAllFile();
	}

	private static void getUserInput(String[] args) {
		if (args.length > 0) {
			corpus = args[0];
		}

	}

	private static void formLSADocument() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(
					"/group/project/vsm-afs/treeCorpus/w2v_1.txt"), true);
			writer.write(builder.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private static void combineAllFile() {

		System.out.println("Combining all FILES");

		if (corpusFiles != null) {
			sortByNumber(corpusFiles);
			for (File file : corpusFiles) {
				builder = new StringBuilder();
				System.out.println("File Name: " + file.getName());
				getTreeReader(file);
				int count = 0;
				inner: while (treeReader.hasNext()) {
					count++;
					formStringBuilder(treeReader.next());
					if (count == 300) {
						count = 0;
						break inner;
					}
				}
			}
		} else {
			builder = new StringBuilder();
			System.out.println("File Name: " + sickFile.getName());
			getTreeReader(sickFile);
			while (treeReader.hasNext()) {
				formStringBuilder(treeReader.next());
			}
		}

		// builder.append("\n");
		// builder.append("DOCSTART-X-0");
		// builder.append("\n");
		// builder.append("\n");
		formLSADocument();

		System.out.println("DONE");

	}

	private static void formStringBuilder(Tree<String> next) {
		List<String> tokens = next.getTerminalYield();
		for (String token : tokens) {
			builder.append(token);
			builder.append(" ");
		}
		builder.append("\n");
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
		if (!corpus.equalsIgnoreCase("SICK")) {
			corpusFiles = new File(URI).listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// TODO Auto-generated method stub
					return !pathname.isHidden();
				}
			});

		} else {
			sickFile = new File(
					"/group/project/vsm-afs/bllip-parser/SICK_parsed_trees");
		}
	}

	public static void sortByNumber(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				int n1 = extractNumer(o1.getName());
				int n2 = extractNumer(o2.getName());
				return n1 - n2;
			}

			private int extractNumer(String name) {

				int i = 0;

				try {
					int s = name.indexOf('_') + 1;
					int e = name.lastIndexOf('.');
					String number = name.substring(s, e);
					i = Integer.parseInt(number);
				} catch (Exception e) {
					i = 0;
				}
				return i;

			}
		});
	}

}
