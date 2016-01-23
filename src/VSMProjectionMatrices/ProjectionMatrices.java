package VSMProjectionMatrices;

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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.j_paine.formatter.CJFormat;

import weka.filters.unsupervised.attribute.Normalize;
import jeigen.DenseMatrix;
import jeigen.SparseMatrixLil;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import Jama.Matrix;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMUtilityClasses.VSMUtil;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLSparse;

import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import VSMSentenceEmbeddingFull.SVDTemplates1;
import edu.upenn.cis.swell.SpectralRepresentations.ContextPCARepresentation;

public class ProjectionMatrices {

	private static Logger LOGGER;

	private SparseMatrixLil PsiSyn;

	private SparseMatrixLil PhiSyn;

	private SparseMatrixLil PsiSem;

	private SparseMatrixLil PhiSem;

	private static String nonTerminal;

	private SparseMatrixLil PsiTPsiSyn;

	private SparseMatrixLil PsiTPhiSyn;

	private SparseMatrixLil PhiTPhiSyn;

	private SparseMatrixLil PhiTPsiSYn;

	private static int d;

	private static int dprime;

	private static SVDTemplates1 svdTC;

	private Matrix ZSynUnormalized;

	private Matrix YSynUnormalized;

	private Matrix ZSyn;

	private Matrix YSyn;

	private double[] s;

	private Matrix ZSem;

	private Matrix YSem;

	private Matrix ZSemUnormalized;

	private Matrix YSemUnormalized;

	private SparseMatrixLil PsiTPsiSem;

	private SparseMatrixLil PsiTPhiSem;

	private SparseMatrixLil PhiTPhiSem;

	private SparseMatrixLil PhiTPsiSem;

	private static String userPref;

	private static int hiddenStates;

	private static ArrayList<MLArray> list;

	private static int dimensions;

	static {

		svdTC = new SVDTemplates1(null);
		list = new ArrayList<MLArray>();
	}

	public static void main(String... args) {

		ProjectionMatrices obj = new ProjectionMatrices();

		nonTerminal = VSMUtil.getNonTerminal(args);

		userPref = VSMUtil.getUserPreference(args);

		LOGGER = VSMLogger.setup(ProjectionMatrices.class.getName() + "." + nonTerminal + "." + userPref);

		hiddenStates = VSMUtil.getHiddenStates(args);

		SparseMatrixLil[] sparseMatrices = getSparseMatrices();

		formSparseMatrices(sparseMatrices, obj, userPref);

		if (userPref.equalsIgnoreCase("Syn")) {

			setDAndDprime(obj);
			formSyntacticProjections(obj);
			serializeProjectionsSyn(obj);
			createProjectionsTextFileSyn();

		} else if (userPref.equalsIgnoreCase("Sem")) {

			formSemanticProjections(obj);
			serializeProjectionsSem(obj);
			createProjectionsTextFileSem();

		}

		freeUpMemory(obj);

	}

	private static void createProjectionsTextFileSyn() {

		LOGGER.info("Finally Creating a MAT file for holding all the projections to be analyzed later");

		Matrix[] projectionMatrices = deserializeProjectionsSyn();

		if (projectionMatrices != null) {

			createTextFileForZSyn(projectionMatrices);
			createTextFileForYSyn(projectionMatrices);
		}

		LOGGER.info("Done with the mat file generation");

	}

	private static void createTextFileForYSyn(Matrix[] projectionMatrices) {

		VSMUtil.writeProjectionMatrix(projectionMatrices, nonTerminal, "YSyn", 1);

	}

	private static void createTextFileForZSyn(Matrix[] projectionMatrices) {

		VSMUtil.writeProjectionMatrix(projectionMatrices, nonTerminal, "ZSyn", 0);

	}

	private static void createProjectionsTextFileSem() {

		LOGGER.info("Finally Creating a MAT file for holding all the projections to be analyzed later");

		Matrix[] projectionMatrices = deserializeProjectionsSem();

		if (projectionMatrices != null) {

			createTextFileForZSem(projectionMatrices);
			createTextFileForYSem(projectionMatrices);
		}

		LOGGER.info("Done with the mat file generation");

	}

	private static void createTextFileForYSem(Matrix[] projectionMatrices) {

		VSMUtil.writeProjectionMatrix(projectionMatrices, nonTerminal, "YSem", 1);

	}

	private static void createTextFileForZSem(Matrix[] projectionMatrices) {

		VSMUtil.writeProjectionMatrix(projectionMatrices, nonTerminal, "ZSem", 0);

	}

	private static Matrix[] deserializeProjectionsSyn() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/" + nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssyn.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(new FileInputStream(fileName));

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE, "Exception while reading the projections.ser file: " + ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, "Exception while reading the projection matrices object" + e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***" + nonTerminal);
			return null;
		}

	}

	private static Matrix[] deserializeProjectionsSem() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/" + nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssem.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(new FileInputStream(fileName));

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE, "Exception while reading the projections.ser file: " + ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, "Exception while reading the projection matrices object" + e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***" + nonTerminal);
			return null;
		}

	}

	private static void serializeProjectionsSyn(ProjectionMatrices obj) {
		LOGGER.info("Serializing the projections");
		serializeCCAVariantsRunSyn(nonTerminal, obj);

	}

	private static void serializeProjectionsSem(ProjectionMatrices obj) {
		LOGGER.info("Serializing the projections");
		serializeCCAVariantsRunSem(nonTerminal, obj);

	}

	private static void formSemanticProjections(ProjectionMatrices obj) {

		LOGGER.info("Forming the semantic projection matrices");
		int x = obj.PsiSem.cols;

		obj.PsiTPsiSem = new SparseMatrixLil(x, x);
		obj.PsiTPhiSem = new SparseMatrixLil(x, x);
		obj.PhiTPhiSem = new SparseMatrixLil(x, x);
		obj.PhiTPsiSem = new SparseMatrixLil(x, x);
		formSemanticCovarianceMatrices(obj);

		removeBinarySemanticMatrices(obj);

		// transformCovarianceMatrices("Sem", obj);

		computeCCASem(MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PsiTPhiSem),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PhiTPsiSem),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PhiTPhiSem),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PsiTPsiSem), svdTC, null, 0, hiddenStates,
				nonTerminal, obj);

		LOGGER.info("+++++Leaving the formSemanticProjections method++++++");

	}

	private static void removeBinarySemanticMatrices(ProjectionMatrices obj) {
		obj.PsiSem = null;
		obj.PhiSem = null;

	}

	private static void formSemanticCovarianceMatrices(ProjectionMatrices obj) {

		LOGGER.info("First the unnormalized Covariances");
		obj.PsiTPsiSem = obj.PsiSem.t().mmul(obj.PsiSem);
		obj.PsiTPhiSem = obj.PsiSem.t().mmul(obj.PhiSem);
		obj.PhiTPhiSem = obj.PhiSem.t().mmul(obj.PhiSem);
		obj.PhiTPsiSem = obj.PhiSem.t().mmul(obj.PsiSem);

		createCovariancesUMATFileSem(obj);

	}

	private static void createCovariancesUMATFileSem(ProjectionMatrices obj) {

		LOGGER.info("Storing the unnormalized covariances in a mat file");
		File file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal + "usem.mat");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		addSparseMatrix(obj.PsiTPsiSem, "PsiTPsiSem");
		addSparseMatrix(obj.PsiTPhiSem, "PsiTPhiSem");
		addSparseMatrix(obj.PhiTPhiSem, "PhiTPhiSem");
		addSparseMatrix(obj.PhiTPsiSem, "PhiTPsiSem");

		createMatFileSem();

	}

	private static void computeCCASem(FlexCompRowMatrix xty, FlexCompRowMatrix ytx, FlexCompRowMatrix yty,
			FlexCompRowMatrix xtx, SVDTemplates1 svdTC, ContextPCARepresentation _cpcaR2, int twoStageFlag,
			int hiddenStates, String directoryName, ProjectionMatrices obj) {

		LOGGER.info("+++Entering CCA Compute Function+++");
		DenseDoubleMatrix2D ZCOLT, YCOLT;

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

		System.out.println("****Memory Consuming Step Done, Loaded two huge matrices in Memory****");

		LOGGER.info("Total memory (bytes) used currently by JVM: " + Runtime.getRuntime().totalMemory());

		FlexCompRowMatrix auxMat1 = new FlexCompRowMatrix(xtx.numRows(), xty.numColumns());

		FlexCompRowMatrix auxMat2 = new FlexCompRowMatrix(yty.numRows(), ytx.numColumns());

		FlexCompRowMatrix auxMat3 = new FlexCompRowMatrix(auxMat1.numRows(), auxMat1.numColumns());

		FlexCompRowMatrix auxMat4 = new FlexCompRowMatrix(auxMat2.numRows(), auxMat2.numColumns());

		int dim1 = ytx.numRows();

		int dim2 = xty.numRows();

		LOGGER.info("+++Initialized auxiliary matrices+++");

		auxMat1 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(computeSparseInverseSqRoot(xtx), xty);

		auxMat3 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat1, computeSparseInverseSqRoot(yty));

		LOGGER.info("+++Computed 1 inverse+++");

		auxMat2 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN((svdTC.computeSparseInverseSqRoot(yty)), ytx);

		auxMat4 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat2, svdTC.computeSparseInverseSqRoot(xtx));

		LOGGER.info("+++Computed Inverses+++");

		LOGGER.info("+++Entering SVD computation+++");

		obj.ZSemUnormalized = svdTC.computeSVD_Tropp(MatrixFormatConversion.createSparseMatrixCOLT(auxMat3),
				getOmegaMatrix(auxMat3.numColumns(), hiddenStates), dim1, hiddenStates, false);

		obj.s = svdTC.getSingularVals();

		VSMUtil.writeSingularValues(obj.s, nonTerminal, "ZSem", dimensions);

		ZCOLT = new DenseDoubleMatrix2D(xtx.numRows(), dimensions);

		MatrixFormatConversion.createSparseMatrixCOLT((svdTC.computeSparseInverseSqRoot(xtx)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(obj.ZSemUnormalized), ZCOLT);

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

		obj.ZSem = MatrixFormatConversion.createDenseMatrixJAMA(ZCOLT);

		LOGGER.info("++++SIZE OF ZSEM+++++ " + obj.ZSem.getRowDimension() + " " + obj.ZSem.getColumnDimension());

		obj.YSemUnormalized = svdTC.computeSVD_Tropp(MatrixFormatConversion.createSparseMatrixCOLT(auxMat4),
				getOmegaMatrix(auxMat4.numColumns(), hiddenStates), dim2, hiddenStates, false);

		obj.s = svdTC.getSingularVals();

		VSMUtil.writeSingularValues(obj.s, nonTerminal, "YSem", dimensions);

		YCOLT = new DenseDoubleMatrix2D(yty.numRows(), dimensions);

		MatrixFormatConversion.createSparseMatrixCOLT((svdTC.computeSparseInverseSqRoot(yty)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(obj.YSemUnormalized), YCOLT);

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

		obj.YSem = MatrixFormatConversion.createDenseMatrixJAMA(YCOLT);

		LOGGER.info("++++++SIZE OF YSEM++++" + obj.YSem.getRowDimension() + " " + obj.YSem.getColumnDimension());

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

	}

	private static void formSyntacticProjections(ProjectionMatrices obj) {

		LOGGER.info("Forning Syntactic Projection Matrices");
		obj.PsiTPsiSyn = new SparseMatrixLil(dprime, dprime);
		obj.PsiTPhiSyn = new SparseMatrixLil(dprime, d);
		obj.PhiTPhiSyn = new SparseMatrixLil(d, d);
		obj.PhiTPsiSYn = new SparseMatrixLil(d, dprime);

		formSyntacticCovarianceMatrices(obj);

		removeSyntacticBinaryMatrices(obj);

		// transformCovarianceMatrices("Syn", obj);

		LOGGER.info("NOW CCA To get the projections");
		computeCCA(MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PsiTPhiSyn),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PhiTPsiSYn),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PhiTPhiSyn),
				MatrixFormatConversion.createSparseMatrixMTJFromJeigen(obj.PsiTPsiSyn), svdTC, null, 0, hiddenStates,
				nonTerminal, obj);

	}

	private static void removeSyntacticBinaryMatrices(ProjectionMatrices obj) {
		obj.PsiSyn = null;
		obj.PhiSyn = null;
		System.gc();

	}

	private static void computeCCA(FlexCompRowMatrix xty, FlexCompRowMatrix ytx, FlexCompRowMatrix yty,
			FlexCompRowMatrix xtx, SVDTemplates1 svdTC, ContextPCARepresentation _cpcaR2, int twoStageFlag,
			int hiddenStates, String directoryName, ProjectionMatrices obj) {

		LOGGER.info("+++Entering CCA Compute Function+++");
		DenseDoubleMatrix2D ZCOLT, YCOLT;

		LOGGER.info("Total memory (bytes) used currently by JVM: " + Runtime.getRuntime().totalMemory());

		FlexCompRowMatrix auxMat1 = new FlexCompRowMatrix(xtx.numRows(), xty.numColumns());

		FlexCompRowMatrix auxMat2 = new FlexCompRowMatrix(yty.numRows(), ytx.numColumns());

		FlexCompRowMatrix auxMat3 = new FlexCompRowMatrix(auxMat1.numRows(), auxMat1.numColumns());

		FlexCompRowMatrix auxMat4 = new FlexCompRowMatrix(auxMat2.numRows(), auxMat2.numColumns());

		int dim1 = ytx.numRows();

		int dim2 = xty.numRows();

		LOGGER.info("+++Initialized auxiliary matrices+++");

		auxMat1 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(computeSparseInverseSqRoot(xtx), xty);

		auxMat3 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat1, computeSparseInverseSqRoot(yty));

		LOGGER.info("+++Computed 1 inverse+++");

		auxMat2 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN((svdTC.computeSparseInverseSqRoot(yty)), ytx);

		auxMat4 = MatrixFormatConversion.multLargeSparseMatricesJEIGEN(auxMat2, svdTC.computeSparseInverseSqRoot(xtx));

		LOGGER.info("+++Computed Inverses+++");

		LOGGER.info("+++Entering SVD computation+++");

		obj.ZSynUnormalized = svdTC.computeSVD_Tropp(MatrixFormatConversion.createSparseMatrixCOLT(auxMat3),
				getOmegaMatrix(auxMat3.numColumns(), hiddenStates), dim1, hiddenStates, false);

		obj.s = svdTC.getSingularVals();

		VSMUtil.writeSingularValues(obj.s, nonTerminal, "ZSyn", dimensions);

		ZCOLT = new DenseDoubleMatrix2D(xtx.numRows(), dimensions);
		MatrixFormatConversion.createSparseMatrixCOLT((svdTC.computeSparseInverseSqRoot(xtx)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(obj.ZSynUnormalized), ZCOLT);

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

		obj.ZSyn = MatrixFormatConversion.createDenseMatrixJAMA(ZCOLT);

		LOGGER.info("+++++++SIZE OF ZSYN+++++++++" + obj.ZSyn.getRowDimension() + " " + obj.ZSyn.getColumnDimension());

		obj.YSynUnormalized = svdTC.computeSVD_Tropp(MatrixFormatConversion.createSparseMatrixCOLT(auxMat4),
				getOmegaMatrix(auxMat4.numColumns(), hiddenStates), dim2, hiddenStates, false);

		YCOLT = new DenseDoubleMatrix2D(yty.numRows(), dimensions);
		MatrixFormatConversion.createSparseMatrixCOLT((svdTC.computeSparseInverseSqRoot(yty)))
				.zMult(MatrixFormatConversion.createDenseMatrixCOLT(obj.YSynUnormalized), YCOLT);

		obj.s = svdTC.getSingularVals();
		VSMUtil.writeSingularValues(obj.s, nonTerminal, "YSyn", dimensions);

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

		obj.YSyn = MatrixFormatConversion.createDenseMatrixJAMA(YCOLT);

		LOGGER.info(
				"+++++++++SIZE OF YSYN+++++++++" + obj.YSyn.getRowDimension() + " " + obj.YSyn.getColumnDimension());

		LOGGER.info("Total memory (bytes) currently used: " + Runtime.getRuntime().totalMemory());

	}

	private static void freeUpMemory(ProjectionMatrices obj) {

		obj.YSyn = null;
		obj.ZSyn = null;
		obj.YSem = null;
		obj.ZSem = null;
		System.gc();

	}

	public static void serializeCCAVariantsRunSyn(String directoryName, ProjectionMatrices obj) {

		Matrix[] projections = new Matrix[2];

		projections[0] = VSMUtil.normalize(obj.ZSyn);
		projections[1] = VSMUtil.normalize(obj.YSyn);

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/" + directoryName;

		File fileDir = new File(fileDirPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = fileDir.getAbsolutePath() + "/projectionssyn.ser";

		try {
			@SuppressWarnings("resource")
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(fileName, false));
			output.writeObject(projections);

			LOGGER.info("=======Serialized the CCA Variant Run=======");
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, "Exception While Serializing the Projections");
			LOGGER.info(ioe.getMessage());
		}

		LOGGER.info("Done with Syntactic serialization");

	}

	public static void serializeCCAVariantsRunSem(String directoryName, ProjectionMatrices obj) {

		Matrix[] projections = new Matrix[2];

		// divide each row vector by norm2
		projections[0] = VSMUtil.normalize(obj.ZSem);
		projections[1] = VSMUtil.normalize(obj.YSem);

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/" + directoryName;

		File fileDir = new File(fileDirPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		String fileName = fileDir.getAbsolutePath() + "/projectionssem.ser";

		try {
			@SuppressWarnings("resource")
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(fileName, false));
			output.writeObject(projections);

			LOGGER.info("=======Serialized the CCA Variant Run=======");
		} catch (IOException ioe) {
			LOGGER.log(Level.SEVERE, "Exception While Serializing the Projections");
			LOGGER.info(ioe.getMessage());
		}

		LOGGER.info("Done with Syntactic serialization");

	}

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
		LOGGER.info("==Created Omega Matrix==");
		return Omega;
	}

	public static FlexCompRowMatrix computeSparseInverseSqRoot(FlexCompRowMatrix X) {

		FlexCompRowMatrix diagInvEntries = new FlexCompRowMatrix(X.numRows(), X.numColumns());

		LOGGER.info("++Beginning Sparse Inverse Sq. Root++");

		for (MatrixEntry e : X) {
			if (e.row() == e.column() && e.get() != 0) {
				diagInvEntries.set(e.row(), e.column(), 1 / Math.sqrt(e.get()));

			}
			if (e.row() == e.column() && e.get() == 0) {
				diagInvEntries.set(e.row(), e.column(), 10000); // Some large
																// value

			}

		}

		LOGGER.info("++Finished Sparse Inverse Sq. Root++");

		return diagInvEntries;

	}

	private static void transformCovarianceMatrices(String check, ProjectionMatrices obj) {

		LOGGER.info("Normalizing the Covariance Matrices, the square root transform");

		if (check.equalsIgnoreCase("Syn")) {

			obj.PsiTPsiSyn = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PsiTPsiSyn)));
			obj.PsiTPhiSyn = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PsiTPhiSyn)));
			obj.PhiTPhiSyn = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PhiTPhiSyn)));
			obj.PhiTPsiSYn = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PhiTPsiSYn)));

			createMatFileCovriancesNSyn(obj);

		} else if (check.equalsIgnoreCase("Sem")) {

			obj.PsiTPsiSem = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PsiTPsiSem)));
			obj.PsiTPhiSem = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PsiTPhiSem)));
			obj.PhiTPhiSem = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PhiTPhiSem)));
			obj.PhiTPsiSem = VSMUtil
					.createJeigenMatrix(transform(VSMUtil.createSparseMatrixMTJFromJeigen(obj.PhiTPsiSem)));

			createMatFileCovriancesNSem(obj);

		}

		LOGGER.info("Done with the transform and the matfile geenration of course");

	}

	private static void createMatFileCovriancesNSem(ProjectionMatrices obj) {

		File file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal + "nsem.mat");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		addSparseMatrix(obj.PsiTPsiSem, "PsiTPsiSemN");
		addSparseMatrix(obj.PsiTPhiSem, "PsiTPhiSemN");
		addSparseMatrix(obj.PhiTPhiSem, "PhiTPhiSemN");
		addSparseMatrix(obj.PhiTPsiSem, "PhiTPsiSemN");

	}

	private static void createMatFileSem() {

		try {
			new MatFileWriter("/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal
					+ "sem.mat", list);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception while writing to the Mat File" + e);
			e.printStackTrace();
		}

		LOGGER.info("Done with  Semantic Covariances MAT file Normalized and Unormalized");

	}

	private static void createMatFileCovriancesNSyn(ProjectionMatrices obj) {

		File file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal + "nsyn.mat");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		addSparseMatrix(obj.PsiTPsiSyn, "PsiTPsiSynN");
		addSparseMatrix(obj.PsiTPhiSyn, "PsiTPhiSynN");
		addSparseMatrix(obj.PhiTPhiSyn, "PhiTPhiSynN");
		addSparseMatrix(obj.PhiTPsiSYn, "PhiTPsiSYnN");

	}

	private static FlexCompRowMatrix transform(FlexCompRowMatrix a) {

		Iterator<MatrixEntry> aIt = a.iterator();
		double ent = 0;

		while (aIt.hasNext()) {
			MatrixEntry ment = aIt.next();
			ent = ment.get();
			// ent = Math.log(ent);
			ent = Math.sqrt(ent);
			a.set(ment.row(), ment.column(), ent);
		}

		return a;

	}

	private static void formSyntacticCovarianceMatrices(ProjectionMatrices obj) {

		LOGGER.info("Forning Unormalized Syntactic Covariances");
		obj.PsiTPsiSyn = obj.PsiSyn.t().mmul(obj.PsiSyn);
		obj.PsiTPhiSyn = obj.PsiSyn.t().mmul(obj.PhiSyn);
		obj.PhiTPhiSyn = obj.PhiSyn.t().mmul(obj.PhiSyn);
		obj.PhiTPsiSYn = obj.PhiSyn.t().mmul(obj.PsiSyn);

		createCovariancesUMATFileSyn(obj);

	}

	private static void createCovariancesUMATFileSyn(ProjectionMatrices obj) {

		LOGGER.info("Now Forming a MAT file containing the Unnormalized Covariances");

		File file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal + "usyn.mat");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		addSparseMatrix(obj.PsiTPsiSyn, "PsiTPsiSyn");
		addSparseMatrix(obj.PsiTPhiSyn, "PsiTPhiSyn");
		addSparseMatrix(obj.PhiTPhiSyn, "PhiTPhiSyn");
		addSparseMatrix(obj.PhiTPsiSYn, "PhiTPsiSYn");

		createMatFileSyn();

	}

	private static void createMatFileSyn() {
		try {
			new MatFileWriter("/afs/inf.ed.ac.uk/group/project/vsm-afs/covariances/" + nonTerminal + "/" + nonTerminal
					+ "syn.mat", list);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception while writing to the Mat File" + e);
			e.printStackTrace();
		}

		LOGGER.info("Done with  Syntactic Covariances MAT file Normalized and Unormalized");

	}

	private static void addSparseMatrix(SparseMatrixLil sparseMatrixLil, String name) {

		FlexCompRowMatrix sparseMatrix = VSMUtil.createSparseMatrixMTJFromJeigen(sparseMatrixLil);
		int[] dims = new int[2];
		dims[0] = sparseMatrix.numRows();
		dims[1] = sparseMatrix.numColumns();
		MLSparse sparse = new MLSparse(name, dims, 0, sparseMatrixLil.getColIdxs().length * 2);
		for (MatrixEntry e : sparseMatrix) {
			sparse.setReal(e.get(), e.row(), e.column());
		}

		list.add(sparse);

	}

	private static void setDAndDprime(ProjectionMatrices obj) {

		d = obj.PhiSyn.cols;
		dprime = obj.PsiSyn.cols;

		LOGGER.info("Got the Syntactic Projection Matrix Dimensions: " + d + " and " + dprime);

	}

	private static void formSparseMatrices(SparseMatrixLil[] sparseMatrices, ProjectionMatrices obj, String userPref) {

		if (userPref.equalsIgnoreCase("Syn")) {

			obj.PsiSyn = sparseMatrices[0];
			obj.PhiSyn = sparseMatrices[1];

		} else if (userPref.equalsIgnoreCase("Sem")) {

			obj.PsiSem = sparseMatrices[2];
			obj.PhiSem = sparseMatrices[3];

		}

		LOGGER.info("Done with it, getting out the method formSparseMatricesFromJAMA");

	}

	private static SparseMatrixLil getSparseMatrix(DenseMatrix psiSynD) {

		LOGGER.info("Forming Sparse Matrix");
		SparseMatrixLil x = new SparseMatrixLil(psiSynD.rows, psiSynD.cols);

		for (int i = 0; i < x.rows; i++) {
			for (int j = 0; j < x.cols; j++) {
				x.append(i, j, psiSynD.get(i, j));
			}
		}
		return x;
	}

	@SuppressWarnings("resource")
	private static SparseMatrixLil[] getSparseMatrices() {

		SparseMatrixLil[] sparseMatrices = new SparseMatrixLil[4];

		ObjectInput matrices = null;

		try {
			matrices = new ObjectInputStream(new FileInputStream(
					new File(VSMContant.SPARSE_MATRICES + nonTerminal + "/" + nonTerminal + ".ser")));

		} catch (IOException e) {
			LOGGER.severe("Excpeiton While opening a stream to the sparse matrices object file: " + e);
		}

		try {

			LOGGER.info("Reading the sparse matrices object form the file");
			sparseMatrices = (SparseMatrixLil[]) matrices.readObject();

		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Exception While reading the Sparse Matrices Array Object from the location: "
					+ VSMContant.SPARSE_MATRICES + nonTerminal + "/" + nonTerminal + ".ser\n" + "Exception: " + e);
			e.printStackTrace();

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Exception While reading the Sparse Matrices Array Object from the location: "
					+ VSMContant.SPARSE_MATRICES + nonTerminal + "/" + nonTerminal + ".ser\n" + "Exception: " + e);
			e.printStackTrace();
		}

		LOGGER.info("Done with it, returning from the method getJamaMatrices");

		return sparseMatrices;
	}

	public static int getDimensions() {
		return dimensions;
	}

	public static void setDimensions(int dimensions) {
		ProjectionMatrices.dimensions = dimensions;
	}

}
