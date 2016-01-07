package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import weka.core.Stopwords;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMSerializeWordDictionary;
import VSMSerialization.VSMWordDictionaryBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class VSMWordDic {

	private static Stopwords stopWords;
	private static PTBTreeNormaliser treeNormalizer;
	private static ArrayList<String> parsedTreeCorpus;
	private static Alphabet wordDictionary;
	private static Alphabet filteredWordDictionary;
	private static PennTreeReader treeReader;
	private static Tree<String> syntaxTree;
	private static List<String> wordList;
	private static URL url;
	private static Dictionary wnDir;
	private static RAMDictionary dict;
	private static Iterator<String> wordListItr;
	private static VSMWordDictionaryBean dictionaryBean;
	private static final Logger LOGGER;

	static {
		LOGGER = VSMLogger.setup(VSMWordDic.class.getName());
		stopWords = new Stopwords();
		treeNormalizer = new PTBTreeNormaliser(true);
		wordDictionary = new Alphabet();
		wordDictionary.turnOnCounts();
		wordDictionary.allowGrowth();
		filteredWordDictionary = new Alphabet();
		filteredWordDictionary.turnOnCounts();
		filteredWordDictionary.allowGrowth();
		dictionaryBean = new VSMWordDictionaryBean();
		try {
			url = new URL("file", null,
					"/group/project/vsm-nfs/WordNet-3.0/dict");
		} catch (MalformedURLException e) {
			LOGGER.severe("Not able to form the given URL for word net");
			e.printStackTrace();
		}
	}

	public static void main(String... args) {

		parsedTreeCorpus = VSMUtil.getTreeCorpus();
		System.out.println("++Parsed Tree Corpus++" + parsedTreeCorpus.size());
		wnDir = new Dictionary(url);

		dict = new RAMDictionary(wnDir, ILoadPolicy.NO_LOAD);
		LOGGER.info("Done Loading WordNet Dictionary");
		openAndLoadDict();

		System.out.println("***Extracting the word dictionary****");
		int iterationMonitor = 0;
		for (String file : parsedTreeCorpus) {
			iterationMonitor++;
			System.out.println(iterationMonitor);
			try {
				treeReader = VSMUtil.getTreeReader(file);
			} catch (Exception e) {
				LOGGER.severe("Exception thrown while getting the tree reader for file+++"
						+ file);
				e.printStackTrace();
			}

			while (treeReader.hasNext()) {

				getSyntaxTree();

				if (syntaxTree != null) {

					syntaxTree = treeNormalizer.process(syntaxTree);

					wordList = syntaxTree.getTerminalYield();

					wordList = VSMUtil.lowercase(wordList);

					wordList = VSMUtil.normalize(wordList);

					wordListItr = wordList.iterator();

					cleanWordList();

					VSMUtil.addToDictionary(wordList, wordDictionary);

				}
			}
		}

		wordDictionary.stopGrowth();
		filteredWordDictionary = VSMUtil.filterWordDictionary(wordDictionary,
				filteredWordDictionary);
		filteredWordDictionary.lookupIndex("<OOV>");
		filteredWordDictionary.stopGrowth();

		System.out
				.println("Dictionary size:**" + filteredWordDictionary.size());

		dictionaryBean = new VSMWordDictionaryBean();

		dictionaryBean.setWordList(filteredWordDictionary);
		serializeFeatureDictionary(dictionaryBean);
		System.out.println("***Done Serialization****");

		System.out.println("****Writing the dictionary out to a file***");
		VSMUtil.writeWordDictionary(filteredWordDictionary);
		System.out.println("***Written the dictionary***");

	}

	private static void cleanWordList() {
		while (wordListItr.hasNext()) {
			String word = wordListItr.next();
			IIndexWord idxWord = null;
			if (!(word.toLowerCase().trim().length() == 0)) {
				for (POS pos : POS.values()) {
					try {
						idxWord = dict.getIndexWord(word, pos);
					} catch (IllegalArgumentException e) {
						LOGGER.severe("Illegal Argument exception while getting the index of a word from wordnet+++ "
								+ word);
					}
				}
				if (!StringUtils.isAlphanumeric(word) || idxWord == null
						|| Stopwords.isStopword(word)) {

					wordListItr.remove();
				}

			} else {
				LOGGER.severe("The word length is zero! ++++ " + word);
			}
		}

	}

	private static void checkAgainstWordNet() {
		while (wordListItr.hasNext()) {
			String word = wordListItr.next();
			IIndexWord idxWord = null;
			for (POS pos : POS.values()) {
				idxWord = dict.getIndexWord(word, pos);
			}

			if (idxWord == null) {
				wordListItr.remove();
				LOGGER.info("The removed word is: " + word);
			}
		}
	}

	private static void removeStopWords() {
		while (wordListItr.hasNext()) {
			String word = wordListItr.next();
			if (Stopwords.isStopword(word)) {
				wordListItr.remove();
			}
		}
	}

	private static void openAndLoadDict() {
		try {
			dict.open();
			dict.load(true);
		} catch (IOException e) {
			LOGGER.severe("Exception while opening the word net dictionary");
			e.printStackTrace();
		} catch (InterruptedException e) {
			LOGGER.severe("Error while loading the dictionary in the RAM");
			e.printStackTrace();
		}

	}

	private static void getSyntaxTree() {
		try {
			syntaxTree = treeReader.next();
		} catch (RuntimeException e) {
			System.out.println("exception" + e + " ::tree  " + syntaxTree);
		}

	}

	private static void serializeFeatureDictionary(
			VSMWordDictionaryBean dictionaryBean) {

		File file = new File("/group/project/vsm-nfs/wordDictionary");

		if (!file.exists()) {
			file.mkdirs();
		}
		String filename = file.getAbsolutePath() + "/worddictionary.ser";
		/*
		 * Delete befoe serializing
		 */
		File ditionaryFile = new File(filename);
		if (ditionaryFile.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(dictionaryBean);
			System.out.println("**serialized at** " + filename);

		} catch (IOException ex) {
			System.out.println("***An Exception Occured While serializing***  "
					+ ex);
		} finally {

			try {
				out.flush();
				fos.flush();
				out.close();
				fos.close();
			} catch (IOException e) {
				System.out
						.println("**Closing the stream can cause exception too**"
								+ e);
			}

		}

	}
}