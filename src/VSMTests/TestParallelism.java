package VSMTests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import jeigen.SparseMatrixLil;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import edu.upenn.cis.swell.MathUtils.SVDTemplates;
import edu.upenn.cis.swell.SpectralRepresentations.ContextPCARepresentation;
import Jama.Matrix;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialObject;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;

/**
 * Test parallel implementation of the VSMProjectionMatrices Extraction and then
 * make all of the code parallel to run faster I guess
 * 
 * @author sameerkhurana10
 *
 */

public class TestParallelism {

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

	public static void main(String... args) throws InterruptedException,
			ExecutionException, IOException, ClassNotFoundException {

		System.out.println("Hi: I am AUX");

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

		String dictionaryPath = "/afs/inf.ed.ac.uk/group/project/vsm/featuredictionary/dt/dictionary.ser";
		// String dictionaryPath =
		// "/Users/sameerkhurana10/Documents/featuredictionary/dictionary.ser";

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

		// String direct =
		// "/Users/sameerkhurana10/Documents/serialization/AUX/AUX_1/";
		String direct = "/afs/inf.ed.ac.uk/group/project/vsm/sparsefeaturevectorssyntactic/DT/";
		File[] serFileDirectories = new File(direct).listFiles();

		/*
		 * The method returns all the serialized file paths
		 */
		ArrayList<String> serFiles = VSMUtil.getFilePaths(serFileDirectories);
		ArrayList<VSMFeatureVectorBean> vectorBeans = new ArrayList<VSMFeatureVectorBean>();

		/*
		 * So we have all the vectorBeans now, which can be used to extract the
		 * phi and psi
		 */
		System.out.println("****Number of files****" + serFiles.size());
		for (String serFile : serFiles) {
			vectorBeans.add(VSMReadSerialObject.readFeatureVectorBean(serFile));
		}

		int threads = Runtime.getRuntime().availableProcessors();
		System.out.println(threads);

		ExecutorService service = Executors.newFixedThreadPool(threads);

		/*
		 * TODO change this to batches of String, batches of 250 because 250*40
		 * = 100000
		 */
		ArrayList<VSMFeatureVectorBean[]> batches = new ArrayList<VSMFeatureVectorBean[]>();
		VSMFeatureVectorBean[] miniBatches = new VSMFeatureVectorBean[500];

		System.out.println("***Forming Mini Batches****");
		int j = 1;
		for (int i = 1; i <= vectorBeans.size(); i++) {
			miniBatches[j - 1] = vectorBeans.get(i - 1);
			if (j == 500) {
				batches.add(miniBatches);
				miniBatches = new VSMFeatureVectorBean[500];
				j = 0;
			}
			j++;
		}
		System.out.println("***Mini Batches Formed*** " + batches.size());

		// HashMap<Integer, String> hashMap = new LinkedHashMap<Integer,
		// String>();
		// Collections.synchronizedMap(hashMap);

		// ArrayList<SparseMatrixLil> batchMatricesPsi = new
		// ArrayList<SparseMatrixLil>();
		// ArrayList<SparseMatrixLil> batchMatricesPhi = new
		// ArrayList<SparseMatrixLil>();
		//
		//
		// Collections.synchronizedList(batchMatricesPhi);
		// Collections.synchronizedList(batchMatricesPsi);

		List<Future<HashMap<SparseMatrixLil, SparseMatrixLil>>> futures = new ArrayList<Future<HashMap<SparseMatrixLil, SparseMatrixLil>>>();
		int count = 0;
		System.out.println("***Running Multiple Threads***");

		final long startTime = System.currentTimeMillis();

		for (VSMFeatureVectorBean[] miniBatch : batches) {
			count++;
			/*
			 * 
			 */
			System.out.println("***Mini Batch***" + count);

			final InnerClassVars innnerVars = new InnerClassVars(miniBatch);

			Callable<HashMap<SparseMatrixLil, SparseMatrixLil>> callable = new Callable<HashMap<SparseMatrixLil, SparseMatrixLil>>() {

				final VSMFeatureVectorBean[] batchToBeProcessed;
				final HashMap<SparseMatrixLil, SparseMatrixLil> batchMatrices = new LinkedHashMap<SparseMatrixLil, SparseMatrixLil>();

				{
					batchToBeProcessed = innnerVars.getMinibatch();
				}

				public HashMap<SparseMatrixLil, SparseMatrixLil> call()
						throws Exception {

					/*
					 * 
					 */
					System.out.println(Thread.currentThread().getName());

					int count = 0;
					/*
					 * M \times d
					 */
					SparseMatrixLil Phi = new SparseMatrixLil(
							batchToBeProcessed.length, d);
					/*
					 * M \times dprimes
					 */
					SparseMatrixLil Psi = new SparseMatrixLil(
							batchToBeProcessed.length, dprime);

					int iteration = 0;

					for (VSMFeatureVectorBean vectorBean : batchToBeProcessed) {

						iteration++;

						System.out.println("Inside thread" + iteration);

						/*
						 * Get the inside and outside feature vectors phi and
						 * psi
						 */
						if (vectorBean != null) {
							SparseVector phi = vectorBean.getPhi();
							SparseVector psi = vectorBean.getPsi();
							vectorBean = null;
							/*
							 * Inside sparse matrix formation for the particular
							 * node. TODO need to test this getIndex first, that
							 * whether it returns +1 index or the ones we wanted
							 * to store
							 */
							int[] indicesPhi = phi.getIndex();
							double[] valuesPhi = phi.getData();
							/*
							 * Don't need the phi anymore in this iteration
							 */
							phi = null;
							/*
							 * Putting the inside feature vector into the inside
							 * feature matrix
							 */
							for (int i = 0; i < indicesPhi.length; i++) {

								Phi.append(count, indicesPhi[i], valuesPhi[i]);

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
								Psi.append(count, indicesPsi[j], valuesPsi[j]);
							}
							indicesPsi = null;
							valuesPsi = null;
							System.gc();
							count++;

						} else {
							System.out
									.println("The vector bean was null for the file: "
											+ vectorBean);
						}

					}

					// batchMatricesPhi.add(Phi);
					// //
					// batchMatricesPsi.add(Psi);

					batchMatrices.put(Phi, Psi);
					//
					return batchMatrices;

				}
			};

			System.out.println("***Done with iterations***");

			/*
			 * Getting all the batch feature matrices
			 */
			futures.add(service.submit(callable));

			// so, we get an output for each input, now we can do something with
			// the output
			// String message = service.submit(callable).get();
			// for (int k : output) {
			// hashMap.put(k, Integer.toString(count));
			// }
			// System.out.println("***Output***" + output);
		}

		final long endTime = System.currentTimeMillis();

		System.out.println("Total execution time: " + (endTime - startTime));

		// System.out.println(hashMap);

		service.shutdown();

		/*
		 * One map per batch in the list. So we have as many hash maps as we
		 * have batches
		 */
		ArrayList<HashMap<SparseMatrixLil, SparseMatrixLil>> outputs = new ArrayList<HashMap<SparseMatrixLil, SparseMatrixLil>>();
		for (Future<HashMap<SparseMatrixLil, SparseMatrixLil>> batchMatric : futures) {
			outputs.add(batchMatric.get()); // System.out.println(batchMatric.get());
		}

		Set<SparseMatrixLil> matricesIn = new LinkedHashSet<SparseMatrixLil>();
		Set<SparseMatrixLil> matricesOut = new LinkedHashSet<SparseMatrixLil>();
		for (HashMap<SparseMatrixLil, SparseMatrixLil> map : outputs) {

			/*
			 * Inside feature matrix minibatch
			 */
			SparseMatrixLil PhiBatch = (SparseMatrixLil) map.keySet().toArray()[0];
			SparseMatrixLil PsiBatch = map.get(PhiBatch);
			matricesIn.add(PhiBatch);
			matricesOut.add(PsiBatch);

		}

		/*
		 * Concatinating the matrices
		 */

		Iterator<SparseMatrixLil> matrixInItr = matricesIn.iterator();
		Iterator<SparseMatrixLil> matrixOutItr = matricesOut.iterator();

		int i = 0;
		// SparseMatrixLil out1 = new SparseMatrixLil(250, dprime);
		SparseMatrixLil Phi = new SparseMatrixLil(vectorBeans.size(), d);
		SparseMatrixLil Psi = new SparseMatrixLil(vectorBeans.size(), dprime);
		while (matrixInItr.hasNext()) {

			i++;
			if (i == 1) {
				Phi = matrixInItr.next();
			}

			else {
				Phi = Phi.concatDown(matrixInItr.next());
			}

		}

		int k = 0;
		while (matrixOutItr.hasNext()) {

			k++;
			if (k == 1) {
				Psi = matrixOutItr.next();
			}

			else {
				Psi = Psi.concatDown(matrixOutItr.next());
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
		 * Done with the Psi and Phi and freeing up some space
		 */
		Psi = null;
		Phi = null;
		System.gc();
		/*
		 * Getting the svd template object that has utility methods to do
		 * preprocessing before performing CCA
		 */
		SVDTemplates svdTC = new SVDTemplates(null);

		/*
		 * Function to compute the CCA, passing the covariance matrices to the
		 * function
		 */
		computeCCA2(
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PsiTPhi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PhiTPsi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PhiTPhi),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(PsiTPsi),
				svdTC, null, 0, 50, "AUX");

		/*
		 * Writing the projection matrices out in a file to see what is in there
		 */
		matrices = VSMUtil.deserializeCCAVariantsRun("AUX");
		VSMUtil.writeEigenDictInside(matrices, "AUX", d);
		VSMUtil.writeEigenDictOutside(matrices, "AUX", dprime);
		matrices = null;
		PsiTPhi = null;
		PhiTPhi = null;
		PsiTPsi = null;
		PhiTPsi = null;
		System.gc();

		// PsiConcatenated = out1;
		// System.out.println(Phi);
		// System.out.println(Psi);

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
			FlexCompRowMatrix xtx, SVDTemplates svdTC,
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
}
