package VSMWordEmbeddingsNew;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import edu.upenn.cis.swell.MathUtils.MatrixFormatConversion;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import Jama.Matrix;
import VSMLogger.VSMLogger;
import VSMSentenceEmbeddingFull.SVDTemplates1;
import VSMUtilityClasses.VSMUtil;

public class WordMatrixForSVD {
	private static final String EMBEDDINGS_PATH;
	private static final Logger LOGGER;
	private static File[] embeddingFiles;
	private static Matrix wordMatrix;

	private static HashMap<String, DenseVector> embeddingsMap;
	private static int hiddenStates;
	private static VSMWordEmbeddingsNew.SVDTemplates1 svdTC;

	private static final String EMBEDDINGS_MAP_SERIALIZE_PATH;

	static {
		EMBEDDINGS_PATH = "/group/project/vsm-nfs/WordEmbeddings1/WordEmbeddings";
		LOGGER = VSMLogger.setup(WordMatrixForSVD.class.getName());
		embeddingsMap = new LinkedHashMap<String, DenseVector>();
		EMBEDDINGS_MAP_SERIALIZE_PATH = "/group/project/vsm-nfs/WordEmbeddingsMap1";
		svdTC = new VSMWordEmbeddingsNew.SVDTemplates1(null);

	}

	public static void main(String... args) {
		getHiddenStates(args);
		embeddingFiles = getFiles();
		combineAllFiles();
		formMatrix();
		wordMatrix = performSVD();
		modifyEmbeddingsMap();
		serializeEmbeddingsMap(args);
		writeEmbeddingsToFile(args);

	}

	private static void writeEmbeddingsToFile(String[] args) {
		System.out.println("WRITING EMBEDDINGS TO THE FILE");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(EMBEDDINGS_MAP_SERIALIZE_PATH + "/"
					+ args[1] + ".txt");

			for (String word : embeddingsMap.keySet()) {
				DenseVector vec = embeddingsMap.get(word);

				writer.write(word);
				writer.write(" ");
				for (VectorEntry e : vec) {
					writer.write(((Double) e.get()).toString());
					writer.write(" ");
				}

				writer.write("\n");

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}

		System.out.println("++DONE+++");

	}

	private static void test() {
		System.out.println("+++TESTING++++");
		if (embeddingsMap.containsKey("underpinnings")) {
			DenseVector vec = embeddingsMap.get("underpinnings");
			for (VectorEntry e : vec) {
				if (Double.isNaN(e.get())) {
					System.out.println("+++I AM NAN HERE+++");
				}
			}
		}

	}

	private static void modifyEmbeddingsMap() {
		System.out.println("MODIFYING EMBEDDINGS MAP WITH LOWER D VECTORS");
		System.out.println("+++CHECKING THE SIZE OF WORD MATRIX+++");
		if (wordMatrix.getColumnDimension() == 24000) {
			System.out.println("++NOT SERIALIZING THE RIGHT MATRIX+++");
			System.exit(-1);
		} else {
			System.out.println("++EVERYTHING SEEMS FINE++ "
					+ wordMatrix.getColumnDimension());
		}
		double[][] arr = wordMatrix.getArray();
		int count = 0;
		for (String word : embeddingsMap.keySet()) {
			double[] vec = arr[count];
			DenseVector vector = new DenseVector(vec);
			// normalizing the vectors here only
			vector = (DenseVector) VSMUtil.normalizeVec(vector);
			embeddingsMap.put(word, vector);
			count++;
		}

		System.out.println("++DONE++");
	}

	private static void getHiddenStates(String[] args) {
		if (args.length > 0) {
			hiddenStates = Integer.parseInt(args[0]);
		} else {

			System.exit(-1);
		}

	}

	private static Matrix performSVD() {

		FlexCompRowMatrix S = createSparseMatrixMTJFromJeigen(wordMatrix);

		// svd = sentenceEmbeddingsMat.svd();
		System.out.println("Getting the left eigen vectors");
		Matrix U = svdTC.computeSVD_Tropp(
				MatrixFormatConversion.createSparseMatrixCOLT(S),
				getOmegaMatrix(S.numColumns(), hiddenStates), S.numColumns(),
				hiddenStates, false);
		// Matrix singularVals = svd.getS();
		return U;

	}

	private static void serializeEmbeddingsMap(String[] args) {

		System.out.println("SERIALIZING THE EMBEDDINGS MAP");
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(
					EMBEDDINGS_MAP_SERIALIZE_PATH + "/" + args[1] + ".ser"));
			out.writeObject(embeddingsMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("DONE SERIALIZATION");
	}

	private static void formMatrix() {
		wordMatrix = new Matrix(embeddingsMap.keySet().size(), 24000);
		int count = 0;
		for (String word : embeddingsMap.keySet()) {
			DenseVector vec = embeddingsMap.get(word);
			for (VectorEntry e : vec) {
				wordMatrix.set(count, e.index(), e.get());
				if (Double.isNaN(e.get())) {
					System.out.println("oops STILL NAN");
				}
			}
			count++;
		}

	}

	private static void combineAllFiles() {
		System.out.println("+++COMBINING ALL THE EMBEDDINGS MAP TOGETHER+++");

		for (File embeddingFile : embeddingFiles) {
			HashMap<String, DenseVector> embeddingsMap = getEmbeddingsMap(embeddingFile);

			for (String word : embeddingsMap.keySet()) {
				DenseVector vec = embeddingsMap.get(word);

				Double[] doubleArray = ArrayUtils.toObject(vec.getData());
				List<Double> list = Arrays.asList(doubleArray);

				if (!(list.contains(Double.NaN))) {
					WordMatrixForSVD.embeddingsMap.put(word, vec);
				}
			}
		}

	}

	private static HashMap<String, DenseVector> getEmbeddingsMap(
			File embeddingFile) {
		System.out.println("embeddings file: " + embeddingFile.getName());
		ObjectInput in = null;
		LinkedHashMap<String, DenseVector> map = new LinkedHashMap<>();
		try {
			in = new ObjectInputStream(new FileInputStream(embeddingFile));
			map = (LinkedHashMap<String, DenseVector>) in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private static File[] getFiles() {
		File[] files = new File(EMBEDDINGS_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.isHidden();
			}
		});
		return files;
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
}
