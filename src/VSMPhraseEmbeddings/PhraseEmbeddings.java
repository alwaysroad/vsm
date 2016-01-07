package VSMPhraseEmbeddings;

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

import org.apache.commons.lang3.StringUtils;

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

public class PhraseEmbeddings {

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

	private static ArrayList<File> parsedCorpus;

	private static ArrayList<Alphabet> outsideFeatureDictionary;

	private static ArrayList<Alphabet> insideFeatureDictionary;

	private static Alphabet wordDictionary;

	private static int phraseCount;

	private static String phrase;

	private static String corpus;

	static {

		LOGGER = VSMLogger.setup(PhraseEmbeddings.class.getName());
		featureCount = new Alphabet();
		featureCount.allowGrowth();
		featureCount.turnOnCounts();
		treeNormalizer = new PTBTreeNormaliser(true);
		binaryFeatureVectors = new Object[4];
		parsedCorpus = new ArrayList<File>();

	}

	public static void main(String... args) throws Exception {

		System.out.println("+++nnnr!!!+++");
		nonTerminal = VSMUtil.getNonTerminal(args);
		corpus = getCorpus(args);
		PennTreeReader treeReader = VSMUtil.getTreeReader(corpus);
		System.out.println(corpus);

		LOGGER.info("Formed the Log File Successfully for Non Terminal**"
				+ nonTerminal);

		System.out.println("***Getting Feature Dictionaries***");
		featureDictionary = VSMContant.FEATURE_DICTIONARY
				+ nonTerminal.toLowerCase() + "/dictionary.ser";

		wordDictionaryPath = VSMContant.WORD_DICT;

		// for (File file : VSMUtil.getTreeCorpus()) {
		// File[] file1 = file.listFiles(new FileFilter() {
		// @Override
		// public boolean accept(File pathname) {
		// // TODO Auto-generated method stub
		// return !pathname.isHidden();
		// }
		// });
		//
		// parsedCorpus.addAll(Arrays.asList(file1));
		// }

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

		// for (File treeFile : parsedCorpus) {

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

					if (insideTree.isPhrasal()
							&& insideTree.getLabel().equalsIgnoreCase(
									nonTerminal.toUpperCase())) {
						wordList = insideTree.getTerminalYield();
						// just picking small phrases, that would occur more
						// number of times, less sparse
						if (wordList.size() != 2) {
							continue inner;
						}

						wordList = VSMUtil.getProcessedWordList(insideTree);
						List<String> unNormalizedWordList = wordList;
						wordList = VSMUtil.normalize(wordList);

						System.out.println("+++wordList++" + wordList);

						phrase = StringUtils.join(
								unNormalizedWordList.toArray(), "_");

						System.out.println("++++" + phrase);

					} else {
						continue inner;
					}

					// checking for unique words
					if (!featureCount.contains(phrase)) {
						phraseCount++;
					}

					featureCount.lookupIndex(phrase);

					binaryFeatureVectors = VSMUtil.getBinaryFeatureVectors(
							insideTree, syntaxTree, constituentsMap,
							outsideFeatureDictionary, insideFeatureDictionary,
							wordDictionary);

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
							psiSemEmbedded, phraseCount, phrase, nonTerminal,
							featureCount, args[1]);
					resetWordCount();

				}
			}
		}
	}

	// System.out.println("****Done with" + nonTerminal + "*****");
	//
	// }

	private static String getCorpus(String[] args) {
		if (args.length > 1) {
			corpus = "/group/project/vsm-afs/treeCorpus/" + args[1] + ".txt";
			System.out.println("+++CORPUS+++" + corpus);
			return corpus;
		} else {
			System.out
					.println("++PLEASE ENTER THE CORPUS YOU WANT TO EXTRACT DATA FROM++");
			System.exit(0);
		}
		return null;
	}

	private static void resetWordCount() {
		if (phraseCount == 3001) {
			phraseCount = 0;
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
