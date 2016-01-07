package VSMWordEmbeddingsNew;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import jeigen.DenseMatrix;
import Jama.Matrix;
import VSMUtilityClasses.VSMUtil;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

@SuppressWarnings("unchecked")
public class NormalizeMat {

	private static Matrix U;

	private static String normalizedMatFilePath;

	private static MatFileReader fileReader;

	static {

	}

	public static void main(String... args) {

		if (!(args.length > 1)) {
			System.out.println("+++NOT ENOUGH ARGUMENTS+++");
			System.exit(-1);
		}
		setNormalizedFilePath(args);
		getUnormalizedMatFile(args);

		System.out.println("+++Reading the mat file++");
		U = new Matrix(
				((MLDouble) fileReader.getMLArray("U" + args[2])).getArray());
		System.out.println("++++Normalizing the mat file++++");
		U = normalize(U);
		createMatFileSentenceEmbed(U.getArray());
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

				if (!Double.isNaN(norm2))
					rowVec = rowVec.div(norm2);
				else
					System.out.println("NORM IS NAN");

				for (int l = 0; l < rowVec.cols; l++) {
					temp.set(k, l, rowVec.get(0, l));
				}

			}

		}

		return VSMUtil.createDenseMatrixJAMA(temp);

	}

	private static void createMatFileSentenceEmbed(double[][] U) {

		String matVarNameIn = "U";

		MLDouble eigenDict = new MLDouble(matVarNameIn, U);

		ArrayList list = new ArrayList();
		list.add(eigenDict);

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

}
