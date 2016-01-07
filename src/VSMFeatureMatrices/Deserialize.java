package VSMFeatureMatrices;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import Jama.Matrix;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;

public class Deserialize {

	public static void main(String... args) throws ClassNotFoundException,
			IOException {

		int d = 0;
		int dprime = 0;

		String dictionaryPath = "/afs/inf.ed.ac.uk/group/project/vsm/featuredictionary/dictionary.ser";
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

		/**
		 * The method to serialize the inside and outside matrices
		 * 
		 * @param opt
		 * @return
		 * @throws ClassNotFoundException
		 */

		Object[] matrixObj = new Object[3];

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm/serializedprojections/NN";
		File fileDir = new File(fileDirPath);
		String fileName = fileDir.getAbsolutePath() + "/projectionInside.ser";
		String fileName1 = fileDir.getAbsolutePath()
				+ "/projectionOutside.ser";

		Matrix Y = null, Z = null;

		try {

			ObjectInput y = new ObjectInputStream(new FileInputStream(fileName));
			ObjectInput z = new ObjectInputStream(
					new FileInputStream(fileName1));

			Y = (Matrix) y.readObject();
			Z = (Matrix) z.readObject();

			System.out
					.println("=======De-serialized the CCA Variant Run=======");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		matrixObj[0] = (Object) Y;
		matrixObj[1] = (Object) Z;
		matrixObj[2] = null;
		System.out.println(matrixObj);
		System.out.println(matrixObj[0]);

		VSMUtil.writeEigenDictInside(matrixObj, "NN", d);
		VSMUtil.writeEigenDictOutside(matrixObj, "NN", dprime);

	}
}
