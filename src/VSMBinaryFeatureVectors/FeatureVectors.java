package VSMBinaryFeatureVectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeigen.SparseMatrixLil;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import VSMConstants.VSMContant;
import VSMFeatureDictionaries.ReadSerializedDictionary;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLSparse;

import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class FeatureVectors {

	private static final PTBTreeNormaliser treeNormalizer;

	private static String featureDictionary;

	private static String nonTerminal;

	private static ArrayList<String> parsedCorpus;

	private static VSMDictionaryBean dictionaryBean;

	private static Logger LOGGER;

	private static ArrayList<Alphabet> outsideFeatureDictionary;

	private static ArrayList<Alphabet> insideFeatureDictionary;

	private static PennTreeReader treeReader;

	private static Tree<String> syntaxTree;

	private static Map<Tree<String>, Constituent<String>> constituentsMap;

	private static SparseVector psiSyn;

	private static SparseVector phiSyn;

	private static FeatureVectorBean vectorBean;

	private static SparseVector phiSem;

	private static SparseVector psiSem;

	private static Alphabet wordDictionary;

	private static VSMWordDictionaryBean wordDictBean;

	private static String wordDictionaryPath;

	private static SerializeFeatureVectorBean serializeBean;

	private static VSMWordFeatureVectorBean wordFeatureVecBean;

	private static SparseMatrixLil PhiSyn;

	private static SparseMatrixLil PsiSyn;

	private static SparseMatrixLil PhiSem;

	private static SparseMatrixLil PsiSem;

	private static ArrayList<MLArray> list;

	private static int count;

	private static int dprime;

	private static int d;

	private static long startTime;

	private static long endTime;

	private static long duration;

	private SparseMatrixLil[] sparseBinaryMatrices;

	static {
		treeNormalizer = new PTBTreeNormaliser(true);
		list = new ArrayList<MLArray>();
	}

	{
		sparseBinaryMatrices = new SparseMatrixLil[4];
	}

	public static void main(String... args) throws Exception {
		System.out.println("New");

		nonTerminal = VSMUtil.getNonTerminal(args);

		LOGGER = VSMLogger.setup(FeatureVectors.class.getName() + "."
				+ nonTerminal);

		featureDictionary = VSMContant.FEATURE_DICTIONARY
				+ nonTerminal.toLowerCase() + "/dictionary.ser";

		wordDictionaryPath = VSMContant.WORD_DICT;

		parsedCorpus = VSMUtil.getTreeCorpus();

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

		dprime = getDprime();

		d = getD();

		LOGGER.info("+++Total Memory Currenly Used By JVM++++ "
				+ Runtime.getRuntime().totalMemory());

		PsiSyn = new SparseMatrixLil(300000, dprime);

		PhiSyn = new SparseMatrixLil(300000, d);

		PsiSem = new SparseMatrixLil(300000, wordDictionary.size());

		PhiSem = new SparseMatrixLil(300000, wordDictionary.size());

		LOGGER.info("+++Total memory After initializing the Matrices+++ "
				+ Runtime.getRuntime().totalMemory());

		serializeBean = new SerializeFeatureVectorBean();

		/*
		 * Timing the loop
		 */
		startTime = System.nanoTime();
		mainloop: for (String treesContainingFile : parsedCorpus) {

			treeReader = VSMUtil.getTreeReader(treesContainingFile);

			while (treeReader.hasNext()) {

				if (getSyntaxTree() != null) {

					constituentsMap = syntaxTree.getConstituents();

					Iterator<Tree<String>> treeIterator = syntaxTree.iterator();

					Tree<String> insideTree = null;

					while (treeIterator.hasNext()) {

						insideTree = treeIterator.next();

						if (!insideTree.isLeaf()
								&& insideTree.getLabel().equalsIgnoreCase(
										nonTerminal)) {

							System.out.println("Iteration: " + count);

							formSyntactiBinaryFeatureVectors(insideTree);

							formSemanticBinaryFeatureVectors(insideTree);

							formSparseSyntacticMatrices();

							formSparseSemanticMatrices();

							count++;

							serializeBean.serializeVectorBean(vectorBean);

							if (count == PsiSem.rows) {
								break mainloop;
							}

						}
					}

				}
			}
		}
		endTime = System.nanoTime();
		duration = (endTime - startTime);
		LOGGER.info("Time taken By the main loop: " + duration / 1000000
				+ " millisec");
		/*
		 * Checking Memory Usage after the objects have been filled with data
		 */
		LOGGER.info("++++Total Memory in use++++ "
				+ Runtime.getRuntime().totalMemory());

		/*
		 * Timing the method
		 */
		startTime = System.nanoTime();

		serializeMatrices();

		endTime = System.nanoTime();
		duration = (endTime - startTime);
		LOGGER.info("Time taken for serializing the Binary Feature Matrices: "
				+ duration / 1000000 + " millisec");

		/*
		 * Timing the method
		 */
		startTime = System.nanoTime();

		createMatricesMatFile();

		endTime = System.nanoTime();
		duration = (endTime - startTime);
		LOGGER.info("Time taken for Creating the MAT File: " + duration
				/ 1000000 + " millisec");
	}

	private static void createMatricesMatFile() {

		LOGGER.info("Inside The Functional createMatFile");
		SparseMatrixLil[] sparseMatrices = new SparseMatrixLil[4];
		ObjectInput matrices = null;
		try {
			matrices = new ObjectInputStream(new FileInputStream(new File(
					VSMContant.SPARSE_MATRICES + nonTerminal + "/"
							+ nonTerminal + ".ser")));

		} catch (IOException e) {
			LOGGER.severe("Error while reading the matrices object from the file");
		}

		try {

			sparseMatrices = (SparseMatrixLil[]) matrices.readObject();

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		createMatFile(sparseMatrices);

	}

	private static void createMatFile(SparseMatrixLil[] sparseMatrices) {

		File file = new File(VSMContant.SPARSE_MATRICES + nonTerminal + "/"
				+ nonTerminal + "sparse.mat");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		addSparseMatrix(sparseMatrices[0], "PsiSyn");
		addSparseMatrix(sparseMatrices[1], "PhiSyn");
		addSparseMatrix(sparseMatrices[2], "PsiSem");
		addSparseMatrix(sparseMatrices[3], "PhiSem");

		try {
			new MatFileWriter(VSMContant.SPARSE_MATRICES + nonTerminal + "/"
					+ nonTerminal + "sparse.mat", list);
			LOGGER.info("Written Mat File at" + VSMContant.SPARSE_MATRICES
					+ nonTerminal + "/" + nonTerminal + "sparse.mat");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception while writing to the Mat File"
					+ e);
			e.printStackTrace();
		}

	}

	private static void addSparseMatrix(SparseMatrixLil sparseMatrixLil,
			String name) {

		FlexCompRowMatrix sparseMatrix = VSMUtil
				.createSparseMatrixMTJFromJeigen(sparseMatrixLil);
		int[] dims = new int[2];
		dims[0] = sparseMatrix.numRows();
		dims[1] = sparseMatrix.numColumns();
		MLSparse sparse = new MLSparse(name, dims, 0,
				sparseMatrixLil.getColIdxs().length * 2);
		for (MatrixEntry e : sparseMatrix) {
			sparse.setReal(e.get(), e.row(), e.column());
		}

		list.add(sparse);

	}

	private static void serializeMatrices() {

		LOGGER.info("Serializing the 4 Sparse Matrices");
		FeatureVectors obj = new FeatureVectors();

		File file = new File(VSMContant.SPARSE_MATRICES + nonTerminal + "/"
				+ nonTerminal + ".ser");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		System.out.println("++++PhiSyn++++ " + PhiSyn);
		// System.out.println("+++++PhiSynDense++++" + PhiSyn.toDense());
		obj.sparseBinaryMatrices[0] = PsiSyn;
		obj.sparseBinaryMatrices[1] = PhiSyn;
		obj.sparseBinaryMatrices[2] = PsiSem;
		obj.sparseBinaryMatrices[3] = PhiSem;

		LOGGER.info("++++Total Memory in Use After creating JAMA Matrices+++ "
				+ Runtime.getRuntime().totalMemory());

		ObjectOutput matrices = null;
		try {
			matrices = new ObjectOutputStream(new FileOutputStream(file));
			matrices.writeObject(obj.sparseBinaryMatrices);
			matrices.close();
			obj.sparseBinaryMatrices = null;
		} catch (IOException e) {
			LOGGER.severe("Exception While storing the sparseMatrices object to a file");
			e.printStackTrace();
		}

	}

	private static void formSparseSemanticMatrices() {

		if (phiSem != null && psiSem != null) {

			int[] indicesPhi = phiSem.getIndex();
			double[] valuesPhi = phiSem.getData();

			for (int i = 0; i < indicesPhi.length; i++) {
				PhiSem.append(count, indicesPhi[i], valuesPhi[i]);
			}
			indicesPhi = null;
			valuesPhi = null;

			int[] indicesPsi = psiSem.getIndex();
			double[] valuesPsi = psiSem.getData();

			for (int j = 0; j < indicesPsi.length; j++) {
				PsiSem.append(count, indicesPsi[j], valuesPsi[j]);
			}
		}

	}

	private static void formSparseSyntacticMatrices() {

		if (phiSyn != null && psiSyn != null) {
			int[] indicesPhi = phiSyn.getIndex();
			double[] valuesPhi = phiSyn.getData();

			for (int i = 0; i < indicesPhi.length; i++) {
				PhiSyn.append(count, indicesPhi[i], valuesPhi[i]);
			}

			indicesPhi = null;
			valuesPhi = null;

			int[] indicesPsi = psiSyn.getIndex();
			double[] valuesPsi = psiSyn.getData();

			for (int j = 0; j < indicesPsi.length; j++) {
				PsiSyn.append(count, indicesPsi[j], valuesPsi[j]);
			}
		}

	}

	private static int getDprime() {
		int dprime = 0;

		for (Alphabet dicionary : outsideFeatureDictionary) {
			dprime = dprime + dicionary.size();
		}

		return dprime;
	}

	private static int getD() {

		int d = 0;

		for (Alphabet dictionary : insideFeatureDictionary) {
			d = d + dictionary.size();
		}

		return d;
	}

	private static void formSemanticBinaryFeatureVectors(Tree<String> insideTree) {

		wordFeatureVecBean = new VSMWordFeatureVectorBean();

		psiSem = new VSMOutsideFeatureVectorWords().getOutsideFeatureVectorPsi(
				syntaxTree, insideTree, wordDictionary, wordFeatureVecBean);

		// System.out.println(psiSem);

		phiSem = new VSMInsideFeatureVectorWords().getInsideFeatureVectorPhi(
				insideTree, wordDictionary, wordFeatureVecBean);

		// System.out.println(phiSem);

		// System.out.println(insideTree);

		vectorBean.setPhiSem(phiSem);
		vectorBean.setPsiSem(psiSem);

	}

	private static void formSyntactiBinaryFeatureVectors(Tree<String> insideTree) {

		VSMUtil.setConstituentLength(constituentsMap.get(insideTree));

		VSMUtil.getNumberOfOutsideWordsLeft(insideTree, constituentsMap,
				syntaxTree);

		VSMUtil.getNumberOfOutsideWordsRight(insideTree, constituentsMap,
				syntaxTree);

		Stack<Tree<String>> foottoroot = new Stack<Tree<String>>();

		foottoroot = VSMUtil.updateFoottorootPath(foottoroot, syntaxTree,
				insideTree, constituentsMap);

		if (!insideTree.isLeaf()
				&& insideTree.getLabel().equalsIgnoreCase(nonTerminal)) {

			vectorBean = new FeatureVectorBean();

			psiSyn = new OutsideFeatureVectorImpl().getOutsideFeatureVectorPsi(
					foottoroot, outsideFeatureDictionary, vectorBean);

			phiSyn = new InsideFeatureVectorImpl().getInsideFeatureVectorPhi(
					insideTree, insideFeatureDictionary, vectorBean);

			vectorBean.setPhi(phiSyn);
			vectorBean.setPsi(psiSyn);

			vectorBean.setInsideTree(insideTree);
			vectorBean.setLabel(insideTree.getLabel());
			vectorBean.setSyntaxTree(syntaxTree);
			vectorBean.setFootToRoot(foottoroot);

		}
	}

	private static Tree<String> getSyntaxTree() {
		try {
			syntaxTree = treeReader.next();
			if (syntaxTree != null) {
				syntaxTree = treeNormalizer.process(syntaxTree);
			} else {
				LOGGER.severe("The syntax tree was null");
			}
		} catch (RuntimeException e) {
			System.out.println("exception" + e + " ::tree  " + syntaxTree);
		}
		return syntaxTree;
	}
}
