package VSMSyntacticProjectionMatrices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import jeigen.SparseMatrixLil;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import Jama.Matrix;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialObject;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import edu.upenn.cis.swell.MathUtils.SVDTemplates1;
import edu.upenn.cis.swell.SpectralRepresentations.ContextPCARepresentation;

public class VSMFeatureMatrixIN {

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

	public static void main(String... args) throws ClassNotFoundException,
			IOException {

		System.out.println("Hi: I am IN");

		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (bytes): "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

		/*
		 * The inside and outisde projection matrices I suppose
		 */
		Object[] matrices = new Object[2];

		/*
		 * First deserialise the dictionary as well to get the dimensions d and
		 * dprime
		 */
		// String dictionaryPath =java -Xms10g -Xmx100g -cp
		// ".:/afs/inf.ed.ac.uk/user/s14/s1444025/Documents/workspace/spectralembeddings/lib/*"
		// "/Users/sameerkhurana10/Documents/featuredictionary1/dictionary.ser";

		String dictionaryPath = "/afs/inf.ed.ac.uk/group/project/vsm/featuredictionary/in/dictionary.ser";
		/*
		 * Getting the dictionary bean
		 */
		System.out.println("******Getting the feature dictionary******");
		VSMDictionaryBean dictionaryBean = VSMReadSerialMatrix
				.readSerializedDictionary(dictionaryPath);

		/*
		 * d and dprime
		 */
		ArrayList<Alphabet> insideFeatureDictionary = dictionaryBean
				.getInsideFeatureDictionary();
		ArrayList<Alphabet> outsideFeatureDictionary = dictionaryBean
				.getOutsideFeatureDictionary();

		System.out
				.println("***Got the inside and outside feature dictionary****");

		System.out
				.println("***Getting the inside and outside matrix dimensions****");

		/*
		 * This is d
		 */
		for (Alphabet dictionary : insideFeatureDictionary) {
			d = d + dictionary.size();
		}

		/*
		 * this is d prime
		 */
		for (Alphabet dictionary : outsideFeatureDictionary) {
			dprime = dprime + dictionary.size();
		}

		/*
		 * Don't need the dictionary anymore and hence removing the objects and
		 * running the garbage collector
		 */
		insideFeatureDictionary = null;
		outsideFeatureDictionary = null;
		dictionaryBean = null;
		System.gc();

		System.out
				.println("****Done and hence removed the dictionary object****");

		/*
		 * Declare the Matrices that will hold the feature vector
		 */
		// /*
		// * Declaring the matrices for CCA
		// */
		// FlexCompRowMatrix PhiTPsi = new FlexCompRowMatrix(d, dprime); // This
		// is
		// // d
		// // \times
		// // d'
		// FlexCompRowMatrix PsiTPhi = new FlexCompRowMatrix(dprime, d); // d'
		// // \times
		// // d
		// FlexCompRowMatrix PsiTPsi = new FlexCompRowMatrix(dprime, dprime); //
		// d'
		// // \times
		// // d'
		// FlexCompRowMatrix PhiTPhi = new FlexCompRowMatrix(d, d); // d \times
		// d

		SparseMatrixLil PsiTPsi = new SparseMatrixLil(dprime, dprime);
		SparseMatrixLil PsiTPhi = new SparseMatrixLil(dprime, d);
		SparseMatrixLil PhiTPhi = new SparseMatrixLil(d, d);
		SparseMatrixLil PhiTPsi = new SparseMatrixLil(d, dprime);

		/*
		 * Get the directory, where sparse feature vectors are
		 */
		// String direct = "/Users/sameerkhurana10/Documents/serialization/";
		String direct = "/afs/inf.ed.ac.uk/group/project/vsm/sparsefeaturevectorssyntactic/IN/";
		/*
		 * Getting all the node directories
		 */
		// File[] files = new File(direct).listFiles();

		/*
		 * Iterating over the node directories
		 */

		// for (File file : files) {

		/*
		 * Getting the directory name, used to form the name of the file in
		 * which the projection matrix is stored
		 */
		// String directoryName = file.getName();

		// String directoryName = "IN";

		/*
		 * Getting all the files that contain inside and outside feature vectors
		 */
		// File[] serFileDirectories = file.listFiles();
		File[] serFileDirectories = new File(direct).listFiles();

		/*
		 * The method returns all the serialized file paths
		 */
		ArrayList<String> serFiles = VSMUtil.getFilePaths(serFileDirectories);
		// File[] serFiles = new File(
		// "/Users/sameerkhurana10/Documents/serialization/NP")
		// .listFiles();
		// System.out.println(serFiles);

		/*
		 * Counting the number of samples pertaining to a node
		 */
		// int rows = 0;
		//
		// /*
		// * Getting the total number of samples for a particular node, this
		// would
		// * form the row of the inside and outside matrix Phi
		// */
		// if (!serFiles.isEmpty()) {
		// for (String file1 : serFiles) {
		// rows++;
		// }

		/*
		 * Learning projection matrices for only those nodes that have more than
		 * samples
		 */

		/*
		 * Phi and Psi, inside and outside feature vector matrices The data
		 * structure that holds the inside and outside feature vectors
		 */

		/*
		 * M \times d
		 */
		SparseMatrixLil Phi = new SparseMatrixLil(serFiles.size(), d);
		/*
		 * M \times dprimes
		 */
		SparseMatrixLil Psi = new SparseMatrixLil(serFiles.size(), dprime);

		/*
		 * Iterating over the serialized vector files, to get the inside and
		 * outside feature vectors pertaining to a node and then storing them in
		 * a feature matrix
		 */
		System.out
				.println("*****Forming the inside and outside Feature Matrices****");
		int count = 0;
		for (String serFilePath : serFiles) {
			System.out.println("**Iteration***" + count);
			/*
			 * Getting the path to the serialised object file
			 */
			String filePath = serFilePath;
			/*
			 * Reading the feature vector bean at that path to get the inside
			 * and outside feature vector
			 */
			VSMFeatureVectorBean vectorBean = VSMReadSerialObject
					.readFeatureVectorBean(filePath);
			/*
			 * Get the inside and outside feature vectors phi and psi
			 */
			if (vectorBean != null) {
				SparseVector phi = vectorBean.getPhi();
				SparseVector psi = vectorBean.getPsi();
				vectorBean = null;
				/*
				 * Inside sparse matrix formation for the particular node. TODO
				 * need to test this getIndex first, that whether it returns +1
				 * index or the ones we wanted to store
				 */
				int[] indicesPhi = phi.getIndex();
				double[] valuesPhi = phi.getData();
				/*
				 * Don't need the phi anymore in this iteration
				 */
				phi = null;
				/*
				 * Putting the inside feature vector into the inside feature
				 * matrix
				 */
				for (int i = 0; i < indicesPhi.length; i++) {
					Phi.append(count, indicesPhi[i], valuesPhi[i]);
				}
				indicesPhi = null;
				valuesPhi = null;

				/*
				 * Outside sparse matrix formation for the particular node
				 */
				int[] indicesPsi = psi.getIndex();
				double[] valuesPsi = psi.getData();
				psi = null;

				/*
				 * Putting the outside feature vector into the outside feature
				 * matrix
				 */
				for (int j = 0; j < indicesPsi.length; j++) {
					Psi.append(count, indicesPsi[j], valuesPsi[j]);
				}
				indicesPsi = null;
				valuesPsi = null;
				System.gc();

				/*
				 * The number of samples are stored in the count variable. Used
				 * when writing the projection matrices in a file
				 */
				count++;
				/*
				 * Only take the first 150000 feature vectors
				 */
				if (count == 150000) {
					break;
				}

			} else {
				System.out.println("The vector bean was null for the file: "
						+ filePath);
			}
		}

		System.out.println("*****Done with matrices formation****");
		serFiles = null;
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
		VSMUtil.writeCovarMatrix(PsiTPsi, "IN");
		VSMUtil.writeCovarMatrix(PsiTPhi, "IN");
		VSMUtil.writeCovarMatrix(PhiTPhi, "IN");
		VSMUtil.writeCovarMatrix(PhiTPsi, "IN");
		System.out.println("***Done***");

		/*
		 * Done with the Psi and Phi and freeing up some space
		 */
		Psi = null;
		Phi = null;
		System.gc();
		/*
		 * Getting the svd template object that has utility methods to do
		 * preprocessing before performing CCA
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
				svdTC, null, 0, 50, "IN");

		/*
		 * Writing the projection matrices out in a file to see what is in there
		 */
		matrices = VSMUtil.deserializeCCAVariantsRun("IN");
		VSMUtil.writeEigenDictInside(matrices, "IN", d);
		VSMUtil.writeEigenDictOutside(matrices, "IN", dprime);
		matrices = null;
		PsiTPhi = null;
		PhiTPhi = null;
		PsiTPsi = null;
		PhiTPsi = null;
		System.gc();

	}

	// }

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
		VSMUtil.writeSingularValues(s, "IN");

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

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm/syntacticprojectionserobjects/"
				+ directoryName;

		File fileDir = new File(fileDirPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = fileDir.getAbsolutePath() + "/projectionInside.ser";
		String fileName1 = fileDir.getAbsolutePath() + "/projectionOutside.ser";

		try {
			ObjectOutput inside = new ObjectOutputStream(new FileOutputStream(
					fileName, false));
			ObjectOutput outside = new ObjectOutputStream(new FileOutputStream(
					fileName1, false));

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
