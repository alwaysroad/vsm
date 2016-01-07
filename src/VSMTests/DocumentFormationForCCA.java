package VSMTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class DocumentFormationForCCA {

	private static PennTreeReader treeReader;

	private static List<String> wordList;

	private static Tree<String> insideTree;

	private static FileWriter writer;

	private static StringBuilder sentenceBuilder;

	public static void main(String... args) {

		if (args.length > 0) {
			getTreeReader(args);
		} else {
			System.out.println("PLEASE GIVE THE CORPUS FILE");
			System.exit(0);
		}
		try {
			writer = new FileWriter(new File(
					"/group/project/vsm-afs/treeCorpus/doc_cca.txt"), true);
			int i = 0;
			main: while (treeReader.hasNext()) {
				if (args.length > 1) {
					if (i >= Integer.parseInt(args[1])) {
						break main;
					}
				}
				System.out.println("Writing sentences to file++ " + i++);
				insideTree = treeReader.next();
				wordList = insideTree.getTerminalYield();
				String sentence = buildSentence();
				writer.write(sentence);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.write("\n");
				writer.write("DOCSTART-X-0");
				writer.write("\n");
				writer.write("\n");
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void getTreeReader(String[] args) {
		try {
			treeReader = VSMUtil
					.getTreeReader("/group/project/vsm-afs/treeCorpus/"
							+ args[0] + ".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String buildSentence() {
		sentenceBuilder = new StringBuilder();
		for (String word : wordList) {
			sentenceBuilder.append(word);
			sentenceBuilder.append(" ");
		}
		sentenceBuilder.append("\n");
		return sentenceBuilder.toString();
	}
}
