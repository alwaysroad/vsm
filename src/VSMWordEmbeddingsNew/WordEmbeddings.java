package VSMWordEmbeddingsNew;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;
import Jama.Matrix;
import VSMConstants.VSMContant;
import VSMFeatureDictionaries.ReadSerializedDictionary;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMWordDictionaryBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import fig.basic.SysInfoUtils;

public class WordEmbeddings {

	private final static Logger LOGGER;

	private static Alphabet featureCount;

	private static DenseMatrix YTSyn;

	private static DenseMatrix YTSem;

	private static DenseMatrix ZTSyn;

	private static DenseMatrix ZTSem;

	private static PTBTreeNormaliser treeNormalizer;

	private static SparseVector psiSyn;

	private static SparseVector phiSyn;

	private static SparseVector psiSem;

	private static SparseVector phiSem;

	private static Tree<String> insideTree;

	private static Tree<String> syntaxTree;

	private static Map<Tree<String>, Constituent<String>> constituentsMap;

	private static List<String> wordList;

	private static Iterator<Tree<String>> treeIterator;

	private static Object[] binaryFeatureVectors;

	private static Vector psiSynEmbedded = null;

	private static Vector phiSynEmbedded = null;

	private static Vector psiSemEmbedded = null;

	private static Vector phiSemEmbedded = null;

	private static String nonTerminal;

	private static String featureDictionary;

	private static String wordDictionaryPath;

	private static VSMDictionaryBean dictionaryBean;

	private static VSMWordDictionaryBean wordDictBean;

	private static ArrayList<String> parsedCorpus;

	private static ArrayList<Alphabet> outsideFeatureDictionary;

	private static ArrayList<Alphabet> insideFeatureDictionary;

	private static Alphabet wordDictionary;

	private static int wordCount;

	static {

		LOGGER = VSMLogger.setup(WordEmbeddings.class.getName());
		featureCount = new Alphabet();
		featureCount.allowGrowth();
		featureCount.turnOnCounts();
		treeNormalizer = new PTBTreeNormaliser(true);
		binaryFeatureVectors = new Object[4];

	}

	public static void main(String... args) throws Exception {

		System.out.println("+++nnn");
		nonTerminal = VSMUtil.getNonTerminal(args);

		LOGGER.info("Formed the Log File Successfully for Non Terminal**"
				+ nonTerminal);

		System.out.println("***Getting Feature Dictionaries***");
		featureDictionary = VSMContant.FEATURE_DICTIONARY
				+ nonTerminal.toLowerCase() + "/dictionary.ser";

		wordDictionaryPath = VSMContant.WORD_DICT;

		// parsedCorpus = VSMUtil.getFilePaths(VSMUtil.getTreeCorpus());

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

		System.out.println("***Got the Tree Corpus***");

		LOGGER.info("Getting The Transposed Projection Matrices");

		System.out
				.println("***Getting the Transposed Projection Matrices YTSyn, ZTSyn, YTSem, ZTSem**");

		formTransposedProjectionMatrices();

		// for (String treeFile : parsedCorpus) {
		PennTreeReader treeReader = null;
		if (args.length > 1) {
			treeReader = VSMUtil
					.getTreeReaderBz("/group/project/vsm-afs/treeCorpus/"
							+ args[1]);
		} else {
			System.out
					.println("PLEASE GIVE THE NAME OF THE Bz FILE TO BE READ");
			System.exit(0);
		}

		while (treeReader.hasNext()) {

			try {
				syntaxTree = treeReader.next();
			} catch (RuntimeException e) {
				LOGGER.log(Level.SEVERE,
						"Runtime Exception Excpetion while reading a tree in the tree file: "
								+ e);
			}

			if (syntaxTree != null) {

				syntaxTree = treeNormalizer.process(syntaxTree);

				constituentsMap = syntaxTree.getConstituents();

				treeIterator = VSMUtil.getTreeIterator(syntaxTree);

				inner: while (treeIterator.hasNext()) {

					insideTree = treeIterator.next();

					boolean check = false;
					String word = null;

					if (insideTree.isPreTerminal()
							&& insideTree.getLabel().equalsIgnoreCase(
									nonTerminal.toUpperCase())) {

						wordList = VSMUtil.getProcessedWordList(insideTree);
						word = wordList.get(0);
						check = VSMUtil.inspectWord(word);

					} else {
						continue inner;
					}

					if (check) {

						// checking for unique words
						if (!featureCount.contains(word)) {
							wordCount++;
						}

						featureCount.lookupIndex(word);

						binaryFeatureVectors = VSMUtil.getBinaryFeatureVectors(
								insideTree, syntaxTree, constituentsMap,
								outsideFeatureDictionary,
								insideFeatureDictionary, wordDictionary);

						psiSyn = (SparseVector) binaryFeatureVectors[0];
						phiSyn = (SparseVector) binaryFeatureVectors[1];

						psiSem = (SparseVector) binaryFeatureVectors[2];
						phiSem = (SparseVector) binaryFeatureVectors[3];

						if (phiSyn != null) {

							phiSynEmbedded = VSMUtil.getPhiSynEmbedded(phiSyn,
									YTSyn);
						}

						if (phiSem != null) {

							phiSemEmbedded = VSMUtil.getPhiSemEmbedded(phiSem,
									YTSem);
						}

						if (psiSyn != null) {
							psiSynEmbedded = VSMUtil.getPsiSynEmbedded(psiSyn,
									ZTSyn);

						}

						if (psiSem != null) {
							psiSemEmbedded = VSMUtil.getPsiSemEmbedded(psiSem,
									ZTSem);
						}

						SerializeEmbeddingBean.serializeEmbeddedFeatureVecs(
								phiSynEmbedded, phiSemEmbedded, psiSynEmbedded,
								psiSemEmbedded, wordCount, word, nonTerminal,
								featureCount);
						resetWordCount();

					}

				}
			}
		}
		System.out.println("****Done with" + nonTerminal + "*****");

	}

	// }

	private static void resetWordCount() {
		if (wordCount == 2001) {
			wordCount = 0;
		}

	}

	private static void formTransposedProjectionMatrices() {

		Matrix[] semProjections = deserializeProjectionsSem();

		LOGGER.info("++Got the Semantic Projections++" + semProjections.length);

		Matrix ZSem = semProjections[0];
		ZTSem = new DenseMatrix(ZSem.getColumnDimension(),
				ZSem.getRowDimension());

		System.out.println(ZSem);
		Matrix YSem = semProjections[1];
		YTSem = new DenseMatrix(YSem.getColumnDimension(),
				YSem.getRowDimension());

		tranpose("Sem", VSMUtil.createDenseMatrixMTJ(ZSem),
				VSMUtil.createDenseMatrixMTJ(YSem));

		// Clearing up some space
		ZSem = null;
		YSem = null;
		semProjections = null;
		System.gc();

		Matrix[] synProjections = deserializeProjectionsSyn();

		LOGGER.info("++++Got the Syntactic Projections++++"
				+ synProjections.length);

		Matrix ZSyn = synProjections[0];
		ZTSyn = new DenseMatrix(ZSyn.getColumnDimension(),
				ZSyn.getRowDimension());

		Matrix YSyn = synProjections[1];
		YTSyn = new DenseMatrix(YSyn.getColumnDimension(),
				YSyn.getRowDimension());

		tranpose("Syn", VSMUtil.createDenseMatrixMTJ(ZSyn),
				VSMUtil.createDenseMatrixMTJ(YSyn));

		// Clearing up some space
		synProjections = null;
		ZSyn = null;
		YSyn = null;
		System.gc();
	}

	private static void tranpose(String pref, DenseMatrix ZMTJ, DenseMatrix YMTJ) {
		if (pref.equalsIgnoreCase("Sem")) {
			System.out.println(ZMTJ.numRows());
			ZMTJ.transpose(ZTSem);
			YMTJ.transpose(YTSem);
		} else if (pref.equalsIgnoreCase("Syn")) {
			ZMTJ.transpose(ZTSyn);
			YMTJ.transpose(YTSyn);
		}

	}

	private static Matrix[] deserializeProjectionsSyn() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/"
				+ nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssyn.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(
						new FileInputStream(fileName));

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out
						.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projections.ser file: "
								+ ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projection matrices object"
								+ e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***"
							+ nonTerminal);
			return null;
		}

	}

	private static Matrix[] deserializeProjectionsSem() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/"
				+ nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssem.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(
						new FileInputStream(fileName));

				System.out.println(fileName);

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out
						.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projections.ser file: "
								+ ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projection matrices object"
								+ e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***"
							+ nonTerminal);
			return null;
		}

	}
}
