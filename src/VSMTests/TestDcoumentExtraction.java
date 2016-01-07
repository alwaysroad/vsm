package VSMTests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class TestDcoumentExtraction {

	public static void main(String... args) throws Exception {

		PTBTreeNormaliser treeNormalizer = new PTBTreeNormaliser(true);

		StringBuilder allDocBuilder = new StringBuilder();

		File[] documents = new File(
				"/group/project/vsm-afs/treeCorpus/lsa_corpus")
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return !pathname.isHidden();
					}
				});

		sortByNumber(documents);

		for (File document : documents) {

			System.out.println("****Forming Document number****"
					+ document.getName());

			/*
			 * Iterator over the parse trees that will gives us the sentences
			 */
			PennTreeReader documentReader = VSMUtil.getTreeReader(document
					.getPath());
			edu.berkeley.nlp.syntax.Tree<String> syntaxTree = null;

			int count = 0;

			while (documentReader.hasNext()) {

				count++;

				try {
					syntaxTree = documentReader.next();
				} catch (RuntimeException e) {
					System.out.println("exception" + e + " ::tree  "
							+ syntaxTree);
				}

				syntaxTree = treeNormalizer.process(syntaxTree);

				List<edu.berkeley.nlp.syntax.Tree<String>> terminalsTreeFormat = syntaxTree
						.getTerminals();

				String[] sentenceTokens = new String[terminalsTreeFormat.size()];

				for (int i = 0; i < terminalsTreeFormat.size(); i++) {
					edu.berkeley.nlp.syntax.Tree<String> terminalTree = terminalsTreeFormat
							.get(i);

					sentenceTokens[i] = terminalTree.toString();
				}

				for (String s : sentenceTokens) {
					allDocBuilder.append(s);
					allDocBuilder.append(" ");
				}

				allDocBuilder.append("\n");

				if (count == 100) {

					allDocBuilder.append("\n");
					allDocBuilder.append("DOCSTART-X-0");

					allDocBuilder.append("\n");
					allDocBuilder.append("\n");
					count = 0;
					writeToDoc(allDocBuilder.toString());

				}

			}

		}
	}

	private static void writeToDoc(String document) {
		FileWriter out = null;
		try {
			out = new FileWriter(
					"/group/project/vsm-afs/treeCorpus/lsa_doc.txt", true);
			out.write(document);
			// out.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
