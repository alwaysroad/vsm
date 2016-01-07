package VSMSentenceEmbeddingFull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMUtilityClasses.VSMUtil;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;
import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import VSMSentenceEmbeddingFull.SVDTemplates1;

public class MatrixSVD {

	private static File[] sentenceEmbeddingsFiles;
	private static ArrayList<String> sentenceEmbeddings;

	private static Matrix sentenceEmbeddingsMat;

	private static final Logger LOGGER;
	private static SentenceVectorBean sentenceEmbedBean;
	private static DenseVector sentenceVec;
	private static int count;
	private static SingularValueDecomposition svd;
	private static PrintWriter writer;
	private static SVDTemplates1 svdTC;

	private static File highDSentenceEmbeddings;

	private static int hiddenStates;
	private static String fileName;

	static {
		LOGGER = VSMLogger.setup(MatrixSVD.class.getName());
		sentenceEmbeddingsMat = new Matrix(1000, 36000);
		svdTC = new SVDTemplates1(null);
		highDSentenceEmbeddings = new File(
				"/group/project/vsm-afs/SICKSentenceEmbeddingsSVD/sentenceEmbed36kTrial.txt");
		if (!highDSentenceEmbeddings.exists()) {
			try {
				highDSentenceEmbeddings.createNewFile();
			} catch (IOException e) {
				LOGGER.severe("Could not form the sentence embeddings file");
				e.printStackTrace();
			}
		}
	}

	public static void main(String... args) {

		sentenceEmbeddingsFiles = VSMUtil
				.getFiles(VSMContant.SICK_TRIAL_EMBEDDINGS_SENT);

		getHiddenStates(args);

		getFileName(args);

		sortByNumber(sentenceEmbeddingsFiles);

		for (File sentenceEmbedding : sentenceEmbeddingsFiles) {
			System.out.println("Sentence number  "
					+ sentenceEmbedding.getName());
			sentenceEmbedBean = readSentenceEmbedding(sentenceEmbedding
					.getAbsolutePath());
			sentenceVec = sentenceEmbedBean.getSentenceVector();
			formMatrix();

		}

		writeMatrix();

		writeEmbeddings(performSVD());

	}

	private static void writeMatrix() {

		System.out.println("++writing now+++");
		double[][] arr = sentenceEmbeddingsMat.getArray();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(highDSentenceEmbeddings);
			for (int i = 0; i < sentenceEmbeddingsMat.getRowDimension(); i++) {
				for (int j = 0; j < sentenceEmbeddingsMat.getColumnDimension(); j++) {
					writer.write(((Double) arr[i][j]).toString());
					writer.write(" ");
				}

				writer.write("\n");
			}
		} catch (IOException e) {
			LOGGER.severe("Exception while writing to the file");
		} finally {
			writer.flush();
			writer.close();
		}
	}

	private static void getFileName(String[] args) {

		if (args.length > 1) {
			fileName = args[1];
		} else {
			System.out.println("PLEASE PROVIDE THE FILENAME");
			System.exit(-1);
		}

	}

	private static void getHiddenStates(String[] args) {

		if (args.length > 0) {
			hiddenStates = Integer.parseInt(args[0]);
		} else {
			System.out.println("PLEASE PROVIDE THE NUMBER OF HIDDEN STATES");
			System.exit(-1);
		}

	}

	private static void writeEmbeddings(Matrix matrix) {
		double[][] singularVectors = matrix.getArray();
		System.out.println("Writing the embeddings to a file");
		File file = null;
		try {
			file = new File("/group/project/vsm-afs/SICKSentenceEmbeddingsSVD/"
					+ fileName);
			file.createNewFile();
			writer = new PrintWriter(file);

			for (int i = 0; i < matrix.getRowDimension(); i++) {
				for (int j = 0; j < matrix.getColumnDimension(); j++) {
					writer.write(Double.toString(singularVectors[i][j]));
					writer.write(' ');
				}

				writer.write('\n');
			}

		} catch (FileNotFoundException e) {
			LOGGER.severe("Exception while writing the singular values to the file");
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.severe("Exception while creating a new file at the location: "
					+ file.getAbsolutePath());
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}

	private static Matrix performSVD() {

		FlexCompRowMatrix S = createSparseMatrixMTJFromJeigen(sentenceEmbeddingsMat);

		// svd = sentenceEmbeddingsMat.svd();
		System.out.println("Getting the left eigen vectors");
		Matrix U = svdTC.computeSVD_Tropp(
				MatrixFormatConversion.createSparseMatrixCOLT(S),
				getOmegaMatrix(S.numColumns(), hiddenStates), S.numColumns(),
				hiddenStates, false);
		// Matrix singularVals = svd.getS();
		return U;

	}

	private static void formMatrix() {
		for (VectorEntry e : sentenceVec) {
			sentenceEmbeddingsMat.set(count, e.index(), e.get());
		}
		System.out.println(count);
		count++;
	}

	public static SentenceVectorBean readSentenceEmbedding(String fileName) {

		SentenceVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (SentenceVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
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

	public static SparseDoubleMatrix2D createSparseMatrixCOLT(
			FlexCompRowMatrix xmtj) {

		System.out.println(" Number Rows: " + xmtj.numRows());
		System.out.println(" Number Cols: " + xmtj.numColumns());

		xmtj.compact();

		SparseDoubleMatrix2D x_omega = new SparseDoubleMatrix2D(xmtj.numRows(),
				xmtj.numColumns(), 0, 0.70, 0.75);

		for (MatrixEntry e : xmtj) {
			x_omega.set(e.row(), e.column(), e.get());
		}

		System.out.println("==Created Sparse Matrix==");
		return x_omega;
	}

	public static FlexCompRowMatrix createSparseMatrixMTJFromJeigen(Matrix xjeig) {
		FlexCompRowMatrix x = new FlexCompRowMatrix(xjeig.getRowDimension(),
				xjeig.getColumnDimension());

		for (int i = 0; i < x.numRows(); i++) {

			for (int j = 0; j < x.numColumns(); j++) {

				x.set(i, j, xjeig.get(i, j));

			}

		}

		return x;
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

}
