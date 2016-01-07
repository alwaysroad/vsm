package VSMSemanticBinaryFeatureVectors;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import jeigen.SparseMatrixLil;
import Jama.Matrix;
import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMSerialization.VSMCountMap;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialCountMap;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMSerializeCountMap;
import VSMSerialization.VSMSerializeFeatureVectorBean;
import VSMSerialization.VSMSerializeFeatureVectorBeanWord;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import edu.upenn.cis.swell.MathUtils.SVDTemplates1;
import edu.upenn.cis.swell.SpectralRepresentations.ContextPCARepresentation;

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

public class VSMFeatureVectorsWordCC {

	static Matrix phiL;
	static Matrix phiR;
	Matrix phiLT;
	Matrix phiRT;
	static Matrix phiLCSU;
	static Matrix phiRCSU;
	Matrix phiL_1stage;
	Matrix phiR_1stage;
	static double[] s;
	private static DenseDoubleMatrix2D dictMatrixCOLT = null;
	private static int d;
	private static int dprime;

	public static void main(String... args) throws Exception {

		/*
		 * The inside and outisde projection matrices I suppose
		 */
		Object[] matrices = new Object[2];

		/*
		 * Data structure to hold all the sparse vectors
		 */
		// ArrayList<SparseVector> phiList = new ArrayList<SparseVector>();
		// ArrayList<SparseVector> psiList = new ArrayList<SparseVector>();

		/*
		 * Used to normalize the trees
		 */

		PTBTreeNormaliser treeNormalizer = new PTBTreeNormaliser(true);

		/*
		 * Getting the feature dictionary path, i.e. the serialized file path.
		 * This dictionary will be used to form the feature vectors.
		 */
		String featureDictionary = null;

		/*
		 * This variable tells the code about the directory path where parse
		 * trees are stored from which feature vectors need to be extracted
		 * corresponding to all the nodes
		 */
		String parsedTreeCorpus = null;

		/*
		 * The feature dictionary that needs to be used while extracting
		 * features
		 */
		featureDictionary = "/disk/scratch/s1444025/worddictionary/worddictionary.ser";
		/*
		 * The directory that holds the parse trees that are iterated over to
		 * extract the feature vector corresponding to the nodes
		 */
		parsedTreeCorpus = "/afs/inf.ed.ac.uk/group/project/vsm.restored/trees";
		/*
		 * Necessary to get the appropriate directory structure
		 */
		// countMapLoc =
		// "/afs/inf.ed.ac.uk/group/project/vsm/countmapnodesamples/countMap.ser";

		/*
		 * Getting the serialised dictionary bean object that contains the
		 * inside and outside feature dictionaries which are used to form the
		 * feature vectors
		 */
		VSMWordDictionaryBean dictionaryBean = VSMReadSerialWordDict
				.readSerializedDictionary(featureDictionary);

		/*
		 * Getting the inside and outside feature dictionaries, that are used
		 * for forming the feature vectors
		 */
		System.out.println("***Getting word dictionary*****");
		Alphabet wordDictionary = dictionaryBean.getWordDictionary();
		System.out.println(wordDictionary);
		// System.out.println(wordDictionary.size());

		dprime = wordDictionary.size();
		d = wordDictionary.size();

		SparseMatrixLil PsiTPsi = new SparseMatrixLil(dprime, dprime);
		SparseMatrixLil PsiTPhi = new SparseMatrixLil(dprime, d);
		SparseMatrixLil PhiTPhi = new SparseMatrixLil(d, d);
		SparseMatrixLil PhiTPsi = new SparseMatrixLil(d, dprime);

		/*
		 * The parsed tree corpus from where the feature vectors need to be
		 * extracted corresponding to all the nodes
		 */
		File[] files = new File(parsedTreeCorpus).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.isHidden();
			}
		});

		ArrayList<String> filePaths = VSMUtil.getFilePaths(files);

		/*
		 * The obect that is used to serialize the feature vector bean. The
		 * feature vector bean storing the inside and outside feature vectors
		 * corresponding to a particular node in a tree. Each feature vector
		 * bean holds the feature vectors for one particular node
		 */
		VSMSerializeFeatureVectorBeanWord serializeBean = null;

		/*
		 * If we already have a serialized count map object then we would want
		 * to start from where we left
		 */
		// File fileCountMap = new File(countMapLoc);

		serializeBean = new VSMSerializeFeatureVectorBeanWord();
		// } else {
		// VSMCountMap countMapObj = VSMReadSerialCountMap
		// .readCountMapObj(countMapLoc);
		// System.out.println("inside the count map***");
		// serializeBean = new VSMSerializeFeatureVectorBeanWord(
		// countMapObj.getCountMap());
		// }

		/*
		 * Getting the data structure to store all the feature vectors in it, We
		 * are taking 200000 samples for a particular non-terminal
		 */
		SparseMatrixLil Phi = new SparseMatrixLil(300000, d);
		SparseMatrixLil Psi = new SparseMatrixLil(300000, dprime);

		int count = 0;

		mainloop: for (String filePath : filePaths) {

			/*
			 * Getting an iterator over the trees in the file
			 */
			PennTreeReader treeReader = VSMUtil.getTreeReader(filePath);

			/*
			 * Iterating over all the trees
			 */
			while (treeReader.hasNext()) {

				/*
				 * The syntax tree
				 */
				Tree<String> syntaxTree = null;

				/*
				 * Unmatched parentheses exception. Does this mean that the
				 * BLLIP corpus sometimes does not have correct parse trees?
				 * Strange
				 */
				try {
					syntaxTree = treeReader.next();
				} catch (RuntimeException e) {
					System.out.println("exception" + e + " ::tree  "
							+ syntaxTree);
				}

				/*
				 * Do stuff only if the syntax tree is a valid one
				 */
				if (syntaxTree != null) {

					/*
					 * Process the syntax tree to remove the top bracket
					 */
					syntaxTree = treeNormalizer.process(syntaxTree);

					/*
					 * Iterator over the nodes of the tree
					 */
					Iterator<Tree<String>> nodeTrees = syntaxTree.iterator();

					/*
					 * Sparse Inside and outside feature vectors declared
					 */
					no.uib.cipr.matrix.sparse.SparseVector psi = null;
					no.uib.cipr.matrix.sparse.SparseVector phi = null;
					Tree<String> insideTree = null;

					/*
					 * Iterating over all the nodes in a particular syntax tree
					 */
					while (nodeTrees.hasNext()) {

						/*
						 * This is the inside tree for which we want to form a
						 * feature vector and store it in the map
						 */
						insideTree = nodeTrees.next();

						/*
						 * Only do stuff if inside tree is not a leaf
						 */
						if (!insideTree.isLeaf()
								&& insideTree.getLabel().equalsIgnoreCase("CC")) {

							/*
							 * Setting the object's properties that are stored
							 * in the .ser file
							 */
							VSMWordFeatureVectorBean vectorBean = new VSMWordFeatureVectorBean();

							System.out
									.println("****Extracting inside and outside feature vectors for node****  "
											+ insideTree.getLabel());

							/*
							 * Getting the inside and outside feature vectors
							 * corresponding to the partcular node
							 */

							psi = new VSMOutsideFeatureVectorWords()
									.getOutsideFeatureVectorPsi(syntaxTree,
											insideTree, wordDictionary,
											vectorBean);
							// psiList.add(psi);

							phi = new VSMInsideFeatureVectorWords()
									.getInsideFeatureVectorPhi(insideTree,
											wordDictionary, vectorBean);
							// phiList.add(phi);

							System.out.println("got the sparse vectors*** ");

							/*
							 * Inside sparse matrix formation for the particular
							 * node.
							 */

							/*
							 * Do the below operation only if both psi and phi
							 * are not null for the given node sample and also
							 * if either psi pr phi are different than before
							 * for this spample, if both are same then no need
							 * to unecessarily fill up Psi and Phi
							 */

							if (phi != null && psi != null) {

								System.out.println(count);

								System.out
										.println("****Filling in the matrices***");

								int[] indicesPhi = phi.getIndex();
								double[] valuesPhi = phi.getData();
								/*
								 * Don't need the phi anymore in this iteration
								 */
								phi = null;
								/*
								 * Putting the inside feature vector into the
								 * inside feature matrix
								 */
								for (int i = 0; i < indicesPhi.length; i++) {
									Phi.append(count, indicesPhi[i],
											valuesPhi[i]);
								}
								indicesPhi = null;
								valuesPhi = null;

								/*
								 * Outside sparse matrix formation for the
								 * particular node
								 */
								int[] indicesPsi = psi.getIndex();
								double[] valuesPsi = psi.getData();
								psi = null;

								/*
								 * Putting the outside feature vector into the
								 * outside feature matrix
								 */
								for (int j = 0; j < indicesPsi.length; j++) {
									Psi.append(count, indicesPsi[j],
											valuesPsi[j]);
								}
								indicesPsi = null;
								valuesPsi = null;
								System.gc();

								/*
								 * Storing the feature vectors in a bean which
								 * will be serialized for future use
								 */
								vectorBean.setPhi(phi);
								vectorBean.setPsi(psi);
								vectorBean.setInsideTree(insideTree);
								vectorBean.setLabel(insideTree.getLabel());
								vectorBean.setSyntaxTree(syntaxTree);

								/*
								 * Serialize the feature vector bean
								 * corresponding to the particular node. The
								 * feature vector bean contains the sparse
								 * inside and outside feature vectors
								 */
								serializeBean
										.serializeWordVectorBean(vectorBean);
								System.out
										.println("***Serialized the feature vector***");

								count++;

								/*
								 * Break when we have 200000 samples
								 */
								if (count == (Psi.rows - 1)) {
									break mainloop;
								}

							}
						}

					}
				}
			}
		}

		/*
		 * Call the CCA function here
		 */

		System.out.println("*****Done with matrices formation****");

		/*
		 * Just calculating the co-vavriance, assuming that the data is centered
		 * and normalized
		 */

		System.out.println("***Calculating Covariances****");
		PsiTPsi = Psi.t().mmul(Psi); // d' \times d'
		PsiTPhi = Psi.t().mmul(Phi);// d' \times d
		PhiTPhi = Phi.t().mmul(Phi);// d \times d
		PhiTPsi = Phi.t().mmul(Psi);// d \times d'
		System.out.println("****Done with it***");

		/*
		 * Log and square root transform
		 */
		PsiTPsi = VSMUtil.createJeigenMatrix(transform(VSMUtil
				.createSparseMatrixMTJFromJeigen(PsiTPsi)));
		PsiTPhi = VSMUtil.createJeigenMatrix(transform(VSMUtil
				.createSparseMatrixMTJFromJeigen(PsiTPhi)));
		PhiTPhi = VSMUtil.createJeigenMatrix(transform(VSMUtil
				.createSparseMatrixMTJFromJeigen(PhiTPhi)));
		PhiTPsi = VSMUtil.createJeigenMatrix(transform(VSMUtil
				.createSparseMatrixMTJFromJeigen(PhiTPsi)));

		/*
		 * Writing the co-variance matrices in a text file to see what's going
		 * on
		 */
		System.out
				.println("****Writing the Covarinace Matrices to the file***");
		VSMUtil.writeCovarMatrixSem(PsiTPsi, "CC");
		VSMUtil.writeCovarMatrixSem(PsiTPhi, "CC");
		VSMUtil.writeCovarMatrixSem(PhiTPhi, "CC");
		VSMUtil.writeCovarMatrixSem(PhiTPsi, "CC");
		System.out.println("***Done***");

		/*
		 * Done with the Psi and Phi and freeing up some space
		 */
		Psi = null;
		Phi = null;
		System.gc();
		/*
		 * Getting the the similarity scoressvd template object that has utility
		 * methods to do preprocessing before performing CCA
		 */
		SVDTemplates1 svdTC = new SVDTemplates1(null);

		/*
		 * Function to compute the CCA, passing the covariance matrices to the
		 * function
		 */
		computeCCA2(
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PsiTPhi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PhiTPsi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PhiTPhi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PsiTPsi),
				svdTC, null, 0, 50, "CC");

		/*
		 * Writing the projection matrices out in a file to see what is in there
		 */
		matrices = VSMUtil.deserializeCCAVariantsRunSem("CC");
		VSMUtil.writeEigenDictInsideSemantic(matrices, "CC", d);
		VSMUtil.writeEigenDictOutsideSem(matrices, "CC", dprime);
		matrices = null;
		PsiTPhi = null;
		PhiTPhi = null;
		PsiTPsi = null;
		PhiTPsi = null;
		System.gc();

		/*
		 * We would also like to serialize the count map. The count map is the
		 * data structure that helps us store the .ser files in proper
		 * directories with proper names. So, if in future we want to extract
		 * feature vectors corresponding to more parse trees, we will start from
		 * where we left in the directory structure and file name
		 */

		/*
		 * Getting the updated count map
		 */
		// countMap = VSMSerializeFeatureVectorBean.getCountMap();
		// /*
		// * The object that will be serialized
		// */
		// VSMCountMap countMapObject = new VSMCountMap();
		// countMapObject.setCountMap(countMap);
		//
		// /*
		// * Serialize count map
		// */
		// VSMSerializeCountMap.serializeCountMap(countMapObject);
		// System.out.println("*****count map serialized****");
	}

	/**
	 * Computing CCA
	 * 
	 * @param xty
	 *            - x = \Psi and y= \Phi
	 * @param ytx
	 * @param yty
	 * @param xtx
	 * @param svdTC
	 * @param _cpcaR2
	 * @param twoStageFlag
	 * @return
	 */
	private static void computeCCA2(FlexCompRowMatrix xty,
			FlexCompRowMatrix ytx, FlexCompRowMatrix yty,
			FlexCompRowMatrix xtx, SVDTemplates1 svdTC,
			ContextPCARepresentation _cpcaR2, int twoStageFlag,
			int hiddenStates, String directoryName) {

		System.out.println("+++Entering CCA Compute Function+++");
		DenseDoubleMatrix2D phiLCOLT, phiRCOLT;

		// remember x is Psi, i.e. the outside feature matrix and hence the
		// dimensionality here is dprime \times k
		System.out
				.println("***Creating the dense matrix, Memory Consuming Step****");

		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes) currently used: "
				+ Runtime.getRuntime().totalMemory());

		phiLCOLT = new DenseDoubleMatrix2D(xtx.numRows(), hiddenStates);
		/*
		 * The below matrix dimensionality is d \times k
		 */
		phiRCOLT = new DenseDoubleMatrix2D(yty.numRows(), hiddenStates);

		System.out
				.println("****Memory Consuming Step Done, Loaded two huge matrices in Memory****");

		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes) used currently by JVM: "
				+ Runtime.getRuntime().totalMemory());
		/*
		 * dprime \times d
		 */
		FlexCompRowMatrix auxMat1 = new FlexCompRowMatrix(xtx.numRows(),
				xty.numColumns());
		/*
		 * d \times dprime
		 */
		FlexCompRowMatrix auxMat2 = new FlexCompRowMatrix(yty.numRows(),
				ytx.numColumns());
		/*
		 * dprime \times d
		 */
		FlexCompRowMatrix auxMat3 = new FlexCompRowMatrix(auxMat1.numRows(),
				auxMat1.numColumns());
		/*
		 * d \times dprime
		 */
		FlexCompRowMatrix auxMat4 = new FlexCompRowMatrix(auxMat2.numRows(),
				auxMat2.numColumns());

		// d in our case, the dimensionality of the inside feature matrix
		int dim1 = ytx.numRows();
		// dprime in our case, the dimensionality of the outside feature matrix
		int dim2 = xty.numRows();

		System.out.println("+++Initialized auxiliary matrices+++");

		/*
		 * Calculating C_{xx}^{-1|2} C_{xy}
		 */
		auxMat1 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(
				computeSparseInverseSqRoot(xtx), xty);

		/*
		 * Multiplying auxMat1 with C_{yy}^{-1|2}
		 */
		auxMat3 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat1,
				computeSparseInverseSqRoot(yty));

		System.out.println("+++Computed 1 inverse+++");

		// (svdTC.computeSparseInverse(yty)).zMult(ytx, auxMat2);

		/*
		 * C_{yy}^{-1|2}.C_{yx}
		 */
		auxMat2 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(
				(svdTC.computeSparseInverseSqRoot(yty)), ytx);

		/*
		 * Multiplying auxMat2 with C_{xx}^{-1|2}
		 */
		auxMat4 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat2,
				svdTC.computeSparseInverseSqRoot(xtx));

		System.out.println("+++Computed Inverses+++");

		// auxMat1.zMult(auxMat2,auxMat3);

		System.out.println("+++Entering SVD computation+++");

		/*
		 * Unnormalized Z projection matrix i.e. the Outside Projection Matrix,
		 * but Unnormalized
		 */
		phiLCSU = svdTC.computeSVD_Tropp(
				MatrixFormatConversion.createSparseMatrixCOLT(auxMat3),
				getOmegaMatrix(auxMat3.numColumns(), hiddenStates), dim1);

		s = svdTC.getSingularVals();

		/*
		 * Write singular values to a file, just to see what's going on in here
		 */
		VSMUtil.writeSingularValuesSem(s, "CC");

		// phiL=phiLCSU;

		MatrixFormatConversion.createSparseMatrixCOLT(
				(svdTC.computeSparseInverseSqRoot(xtx)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(phiLCSU),
						phiLCOLT);

		/*
		 * This is the actual Outside projection TODO, check whether this is
		 * actually the Outside projection. We get this by performing SVD on
		 * C_{xx}^{-1|2}.C_{XY}.C{YY}^{-1|2}, where x is the outside feature
		 * matrix (\Psi) and y is the inside feature matrix (\Phi) dprime \times
		 * k
		 */

		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes) currently used: "
				+ Runtime.getRuntime().totalMemory());

		phiL = MatrixFormatConversion.createDenseMatrixJAMA(phiLCOLT);

		/*
		 * Unormalized Y projection matrix
		 */
		phiRCSU = svdTC.computeSVD_Tropp(
				MatrixFormatConversion.createSparseMatrixCOLT(auxMat4),
				getOmegaMatrix(auxMat4.numColumns(), hiddenStates), dim2);

		MatrixFormatConversion.createSparseMatrixCOLT(
				(svdTC.computeSparseInverseSqRoot(yty)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(phiRCSU),
						phiRCOLT);

		/*
		 * THe inside projection matrix for the node
		 */
		// 700000 \times 200

		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes) currently used: "
				+ Runtime.getRuntime().totalMemory());

		phiR = MatrixFormatConversion.createDenseMatrixJAMA(phiRCOLT);

		/*
		 * Serialize PhiR and PhiL
		 */
		System.out.println("***Serializing***");
		serializeCCAVariantsRun(directoryName);

		System.out.println("Freeing up the memory");
		phiLCOLT = null;
		phiRCOLT = null;
		phiL = null;
		phiLCSU = null;
		phiRCSU = null;
		phiR = null;

		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes) currently used: "
				+ Runtime.getRuntime().totalMemory());
	}

	/**
	 * For randomized SVD
	 * 
	 * @param rows
	 * @return
	 */
	public static DenseDoubleMatrix2D getOmegaMatrix(int rows, int hiddenState) {// Refer
																					// Tropp's
		// notation
		Random r = new Random();
		DenseDoubleMatrix2D Omega;

		Omega = new DenseDoubleMatrix2D(rows, hiddenState + 20);// Oversampled
		// the rank k
		for (int i = 0; i < (rows); i++) {
			for (int j = 0; j < hiddenState + 20; j++)
				Omega.set(i, j, r.nextGaussian());
		}
		System.out.println("==Created Omega Matrix==");
		return Omega;
	}

	/**
	 * Important method used in compute CCA
	 * 
	 * @param X
	 * @return
	 */
	public static FlexCompRowMatrix computeSparseInverseSqRoot(
			FlexCompRowMatrix X) {

		FlexCompRowMatrix diagInvEntries = new FlexCompRowMatrix(X.numRows(),
				X.numColumns());

		System.out.println("++Beginning Sparse Inverse Sq. Root++");

		for (MatrixEntry e : X) {
			if (e.row() == e.column() && e.get() != 0) {
				diagInvEntries.set(e.row(), e.column(), 1 / Math.sqrt(e.get()));

			}
			if (e.row() == e.column() && e.get() == 0) {
				diagInvEntries.set(e.row(), e.column(), 10000); // Some large
																// value

			}

		}

		System.out.println("++Finished Sparse Inverse Sq. Root++");

		return diagInvEntries;

	}

	public static void serializeCCAVariantsRun(String directoryName) {

		// String fileDirPath =
		// "/Users/sameerkhurana10/Documents/serializedprojections/"
		// + directoryName;

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm.restored/semanticprojectionserobjects/"
				+ directoryName;

		File fileDir = new File(fileDirPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = fileDir.getAbsolutePath() + "/projectionInside.ser";
		String fileName1 = fileDir.getAbsolutePath() + "/projectionOutside.ser";

		try {
			ObjectOutput inside = new ObjectOutputStream(new FileOutputStream(
					fileName));
			ObjectOutput outside = new ObjectOutputStream(new FileOutputStream(
					fileName1));

			/*
			 * Inside serialization
			 */
			outside.writeObject(phiL);
			outside.flush();
			outside.close();

			/*
			 * Outside serialization
			 */
			inside.writeObject(phiR);
			inside.flush();
			inside.close();

			System.out.println("=======Serialized the CCA Variant Run=======");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}

	}

	public static FlexCompRowMatrix transform(FlexCompRowMatrix a) {

		Iterator<MatrixEntry> aIt = a.iterator(); // iterating over the elements
													// in the matrix
		double ent = 0;

		while (aIt.hasNext()) {
			MatrixEntry ment = aIt.next();
			ent = ment.get();
			if (true)
				ent = Math.log(ent); // log transform, a good thing to do I
										// guess
			if (true)
				ent = Math.sqrt(ent); // this is also a valid thing to do

			a.set(ment.row(), ment.column(), ent); // Performing tranforms on
													// the matrix
		}

		return a;

	}

}
