package VSMSentenceEmbeddingFull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import jeigen.DenseMatrix;
import Jama.Matrix;
import VSMUtilityClasses.VSMUtil;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

@SuppressWarnings("unchecked")
public class NormalizeMat {

	private static Matrix S;

	private static double[][] sentencesD;

	private static double[][] evaluation;

	private static double[] modelScores;

	private static Collection<Double> goldSTandard;

	private static List list;

	private static double[] goldScores;

	private static String normalizedMatFilePath;

	private static MatFileReader fileReader;

	static {
		goldSTandard = VSMUtil.getGoldStandard().values();
		list = new ArrayList(goldSTandard);
		goldScores = new double[list.size()];
		modelScores = new double[500];
	}

	public static void main(String... args) {

		if (!(args.length > 1)) {
			System.out.println("+++NOT ENOUGH ARGUMENTS+++");
			System.exit(-1);
		}
		setNormalizedFilePath(args);
		getUnormalizedMatFile(args);

		System.out.println("+++Reading the mat file++");
		S = new Matrix(
				((MLDouble) fileReader.getMLArray("U" + args[2])).getArray());
		System.out.println("++++Normalizing the mat file++++");
		S = normalize(S);

		getDataReadyForMat(S);

		createMatFileSentenceEmbed(sentencesD, evaluation);
	}

	private static void getUnormalizedMatFile(String[] args) {
		fileReader = null;
		File file = null;
		try {
			if (args.length > 0) {
				file = new File(
						"/amd/nfs/lammasu/disk/ptn199/vsm-nfs/WordEmbeddingsMap/"
								+ args[0] + ".mat");
			} else {
				System.out.println("PLEASE GIVE THE FILE NAME");
				System.exit(-1);
			}
			fileReader = new MatFileReader(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void setNormalizedFilePath(String[] args) {
		if (args.length > 1) {
			normalizedMatFilePath = args[1];
		} else {
			System.out.println("PLEASE GIVE THE NORMALIZED FILE PATH");
			System.exit(-1);
		}

	}

	private static Matrix normalize(Matrix x) {

		DenseMatrix temp = new DenseMatrix(x.getRowDimension(),
				x.getColumnDimension());
		DenseMatrix xJ = VSMUtil.createDenseMatrixJEIGEN(x);

		System.out.println("Normalizing the matrix+++++");
		for (int k = 0; k < xJ.rows; k++) {

			DenseMatrix rowVec = xJ.row(k);

			if (!(rowVec.nonZeroCols().rows == 0)) {
				double norm2 = VSMUtil.norm2(rowVec.getValues());
				rowVec = rowVec.div(norm2);
				// System.out.println(rowVec.cols);
				for (int l = 0; l < rowVec.cols; l++) {
					temp.set(k, l, rowVec.get(0, l));
				}

			}

		}

		return VSMUtil.createDenseMatrixJAMA(temp);

	}

	private static void getDataReadyForMat(Matrix S) {
		for (int i = 0; i < list.size(); i++)
			goldScores[i] = (double) list.get(i);

		sentencesD = new double[(S.getRowDimension() / 2)][(S
				.getColumnDimension()) * 2];
		evaluation = new double[(S.getRowDimension() / 2)][2];

		int count = 0;
		for (int i = 0; i < S.getRowDimension(); i += 2) {

			System.out.println("++++Getting sentences++++");

			double[] sentence1 = new double[S.getColumnDimension()];
			double[] sentence2 = new double[S.getColumnDimension()];

			for (int k = 0; k < S.getColumnDimension(); k++) {
				sentence1[k] = S.get(i, k);
				sentence2[k] = S.get(i + 1, k);
			}

			double[] data1 = sentence1;
			for (int k = 0; k < sentence1.length; k++) {
				sentencesD[count][k] = data1[k];
			}

			double[] data2 = sentence2;
			int m = 0;
			for (int l = sentence1.length; l < (S.getColumnDimension() * 2); l++) {

				sentencesD[count][l] = data2[m];
				m++;
			}
			double similarityScore = VSMUtil.cosineSimilarity(sentence1,
					sentence2);
			if (similarityScore < 1.0) {
				modelScores[count] = 1.0;
			} else {
				modelScores[count] = similarityScore;
			}

			evaluation[count][0] = modelScores[count];
			evaluation[count][1] = goldScores[count];
			count++;
		}

	}

	private static void createMatFileSentenceEmbed(double[][] sentencesD2,
			double[][] evaluation) {

		double[][] dictL = sentencesD2;
		double[][] dictR = evaluation;

		String matVarNameIn = "S";
		String matVarNameOut = "SCORE";
		MLDouble dictInside = new MLDouble(matVarNameIn, dictL);
		MLDouble dictOutside = new MLDouble(matVarNameOut, dictR);

		ArrayList list = new ArrayList();
		list.add(dictInside);
		list.add(dictOutside);

		try {
			new MatFileWriter(
					"/amd/nfs/lammasu/disk/ptn199/vsm-nfs/WordEmbeddingsMap/"
							+ normalizedMatFilePath + ".mat", list);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

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

	public static SentenceVectorBean readSentenceVec(String fileName) {

		SentenceVectorBean vecBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vecBean = (SentenceVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vecBean;
	}
}
