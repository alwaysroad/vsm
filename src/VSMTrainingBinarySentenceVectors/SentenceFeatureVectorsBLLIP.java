package VSMTrainingBinarySentenceVectors;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMBinaryFeatureVectors.FeatureVectors;
import VSMConstants.VSMContant;
import VSMFeatureDictionaries.ReadSerializedDictionary;
import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMSerializeFeatureVectorBeanTraining;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class SentenceFeatureVectorsBLLIP {

	private static Logger LOGGER;
	private static String nonTerminal;
	private static String featureDictionary;
	private static String wordDictionaryPath;

	private static VSMDictionaryBean dictionaryBean;
	private static VSMWordDictionaryBean wordDictBean;
	private static ArrayList<Alphabet> outsideFeatureDictionary;
	private static ArrayList<Alphabet> insideFeatureDictionary;
	private static Alphabet wordDictionary;
	private static PennTreeReader treeReader;

	private static PTBTreeNormaliser treeNormalizer;
	private static Map<Tree<String>, Constituent<String>> constituentsMap;

	private static Tree<String> syntaxTree;
	private static SparseVector psiSyn;
	private static SparseVector phiSyn;
	private static SparseVector phiSem;
	private static SparseVector psiSem;
	private static Tree<String> insideTree;
	private static VSMFeatureVectorBean vectorBean;
	private static VSMWordFeatureVectorBean vectorBeanWord;
	private static int nodeCount;
	private static Stack<Tree<String>> foottoroot;
	private static File[] parsedTreeFiles;
	private static Serialization serialize;
	private static File[] treeFiles;

	static {
		treeNormalizer = new PTBTreeNormaliser(true);

		serialize = new Serialization();
	}

	public static void main(String... args) throws Exception {

		System.out.println("+++Compiled New++++");

		nonTerminal = VSMUtil.getNonTerminal(args);

		LOGGER = VSMLogger.setup(FeatureVectors.class.getName() + "."
				+ nonTerminal);

		featureDictionary = VSMContant.FEATURE_DICTIONARY
				+ nonTerminal.toLowerCase() + "/dictionary.ser";

		wordDictionaryPath = VSMContant.WORD_DICT;

		LOGGER.info("Reading the Feature Dictionary Object");
		dictionaryBean = ReadSerializedDictionary.readSerializedDictionary(
				featureDictionary, LOGGER);

		LOGGER.info("Reading the word dictionary object");
		wordDictBean = VSMReadSerialWordDict
				.readSerializedDictionary(wordDictionaryPath);

		outsideFeatureDictionary = dictionaryBean.getOutsideFeatureDictionary();

		insideFeatureDictionary = dictionaryBean.getInsideFeatureDictionary();

		wordDictionary = wordDictBean.getWordDictionary();

		System.out.println(wordDictionary.size());

		LOGGER.info("Got the syntactic and semantic feature dictionaries, with word dictionary dimensions: "
				+ wordDictionary.size());

		parsedTreeFiles = VSMUtil.getTreeCorpus();

		sortByNumber(parsedTreeFiles);

		System.out.println("++++Length++++ " + parsedTreeFiles.length);

		LOGGER.info("++Got the Parsed Tree Corpus, Size: "
				+ parsedTreeFiles.length);

		int treeCount = 0;

		main: for (File treeDirec : parsedTreeFiles) {

			getTreeFiles(treeDirec);

			for (File treesFile : treeFiles) {

				LOGGER.info("TREE FILE+++ " + treesFile.getName());

				treeReader = VSMUtil.getTreeReader(treesFile.getAbsolutePath());

				while (treeReader.hasNext()) {

					getSynaxTree();

					if (syntaxTree != null) {

						treeCount += 1;
						System.out.println(treeCount);

						syntaxTree = treeNormalizer.process(syntaxTree);

						constituentsMap = syntaxTree.getConstituents();

						Iterator<Tree<String>> nodeTrees = syntaxTree
								.iterator();

						while (nodeTrees.hasNext()) {

							insideTree = nodeTrees.next();

							if (!insideTree.isLeaf()
									&& insideTree.getLabel().equalsIgnoreCase(
											nonTerminal)) {

								createSparseVectors();

								serializeVectorBean(treeCount);

							}

						}
					}

					if (treeCount == 100000) {
						break main;
					}
				}

			}

			LOGGER.info("Done Creating the Sparse Vectors For the Non Terminal: "
					+ nonTerminal);
		}
	}

	private static void getTreeFiles(File treeDirec) {
		treeFiles = treeDirec.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {

				return !pathname.isHidden();
			}
		});

		sortByNumber(treeFiles);

	}

	private static void serializeVectorBean(int treeCount) {
		nodeCount++;
		serialize.serializeVectorBeanBLLIP(vectorBean, treeCount, nodeCount);

	}

	private static void createSparseVectors() {

		initializeSomeVariables();

		makeVectors();

		createVectorBeanToBeSerialized();

	}

	private static void makeVectors() {

		psiSyn = new VSMOutsideFeatureVector().getOutsideFeatureVectorPsi(
				foottoroot, outsideFeatureDictionary, vectorBean);
		phiSyn = new VSMInsideFeatureVector().getInsideFeatureVectorPhi(
				insideTree, insideFeatureDictionary, vectorBean);
		psiSem = new VSMOutsideFeatureVectorWords().getOutsideFeatureVectorPsi(
				syntaxTree, insideTree, wordDictionary, vectorBeanWord);
		phiSem = new VSMInsideFeatureVectorWords().getInsideFeatureVectorPhi(
				insideTree, wordDictionary, vectorBeanWord);

	}

	private static void initializeSomeVariables() {

		VSMUtil.setConstituentLength(constituentsMap.get(insideTree));
		VSMUtil.getNumberOfOutsideWordsLeft(insideTree, constituentsMap,
				syntaxTree);
		VSMUtil.getNumberOfOutsideWordsRight(insideTree, constituentsMap,
				syntaxTree);

		foottoroot = new Stack<Tree<String>>();
		foottoroot = VSMUtil.updateFoottorootPath(foottoroot, syntaxTree,
				insideTree, constituentsMap);

		vectorBean = new VSMFeatureVectorBean();
		vectorBeanWord = new VSMWordFeatureVectorBean();

	}

	private static void createVectorBeanToBeSerialized() {

		vectorBean.setPhi(phiSyn);
		vectorBean.setPsi(psiSyn);
		vectorBean.setPhiSem(phiSem);
		vectorBean.setPsiSem(psiSem);

		vectorBean.setInsideTree(insideTree);
		vectorBean.setLabel(insideTree.getLabel());
		vectorBean.setSyntaxTree(syntaxTree);
		vectorBean.setFootToRoot(foottoroot);

	}

	private static void getSynaxTree() {
		syntaxTree = null;
		try {
			syntaxTree = treeReader.next();
		} catch (RuntimeException e) {
			System.out.println("exception" + e + " ::tree  " + syntaxTree);
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
