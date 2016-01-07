package VSMTrainingBinarySentenceVectors;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import VSMSerialization.VSMReadSerialMatrix;
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

/**
 * The class generates the feature vectors given an input inside and outside
 * feature dictionary for each non-terminal in a corpus of parse trees. The
 * inside and outside feature vectors are stored serialized so that we do not
 * have to create the vectors again and can use them whenever we want by
 * deserialising the object. We store sparse feature vectors. These feature
 * vectors will be used to learn the linear transforms corresponding to each
 * non-terminal in Matlab. The feature vectors are binary and not scaled for
 * now. Read about the scaling in NAACL2013 and then implement scaling if
 * required
 * 
 * @author sameerkhurana10
 *
 */

public class SentenceFeatureVectors {

	private static Logger LOGGER;
	private static String nonTerminal;
	private static String featureDictionary;
	private static String wordDictionaryPath;
	private static ArrayList<String> parsedCorpus;
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
	private static VSMSerializeFeatureVectorBeanTraining serializeBean;

	static {
		treeNormalizer = new PTBTreeNormaliser(true);
		serializeBean = new VSMSerializeFeatureVectorBeanTraining();
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

		LOGGER.info("Got the syntactic and semantic feature dictionaries, with word dictionary dimensions: "
				+ wordDictionary.size());

		treeReader = VSMUtil.getTreeReader(VSMContant.SICK_TRAINING_TREES);

		LOGGER.info("GOT Training Trees File Iterator: " + treeReader);

		int treeCount = 0;

		while (treeReader.hasNext()) {

			getSynaxTree();

			if (syntaxTree != null) {

				treeCount += 1;

				syntaxTree = treeNormalizer.process(syntaxTree);

				constituentsMap = syntaxTree.getConstituents();

				Iterator<Tree<String>> nodeTrees = syntaxTree.iterator();

				while (nodeTrees.hasNext()) {

					insideTree = nodeTrees.next();

					if (!insideTree.isLeaf()
							&& insideTree.getLabel().equalsIgnoreCase(
									nonTerminal)) {

						createSparseVectors();

						serializeVectorBean(treeCount);

						System.out.println("Serialized the feature vector***");

					}

				}
			}
		}

		LOGGER.info("Done Creating the Sparse Vectors For the Non Terminal: "
				+ nonTerminal);

	}

	private static void serializeVectorBean(int treeCount) {
		nodeCount++;
		serializeBean.serializeVectorBean(vectorBean, treeCount, nodeCount);

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
}
