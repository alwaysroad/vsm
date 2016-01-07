package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMConstants.VSMContant;

public class VSMSerializeFeatureVectorBeanEmbed {

	private static LinkedHashMap<String, Integer> countMap;
	private static int count;
	private static int fileIdx;

	public static void serializeVectorBean(
			VSMFeatureVectorBeanEmbedded vectorBean, String sentence,
			String serName) {

		File file = null;

		file = new File(VSMContant.SICK_EMBEDDED_SENT_VECS + "/" + sentence
				+ "/" + vectorBean.getLabel());

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			String filename = file.getAbsolutePath() + "/" + serName;

			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {

				fos = new FileOutputStream(filename, false);
				out = new ObjectOutputStream(fos);
				out.writeObject(vectorBean);
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		}

	}

	public static void serializeVectorBeanSyntacticEmbedding(
			VSMFeatureVectorBeanEmbedded vectorBean, String sentence,
			String serName) {

		/*
		 * Taking 300k samples of each node and no more
		 */

		// File file = new
		// File("/Users/sameerkhurana10/Documents/serialization/"
		// + phiBean.getLabel());
		/*
		 * Location where sparse feature vectors are storeds
		 */
		File file = null;

		file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm.restored/SICKSyntacticOnlyEmbeddedFeaturevecs3/"
						+ sentence + "/" + vectorBean.getLabel());

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/" + serName;
			// String filename =
			// "/Users/sameerkhurana10/Documents/testserialization/"
			// + phiBean.getLabel() + "_1.ser";

			/*
			 * Serializing the object
			 */
			File checkFile = new File(filename);
			/*
			 * If the file does not exist then only make one if it already done
			 * then don't do anything
			 */
			if (!checkFile.exists()) {
				FileOutputStream fos = null;
				ObjectOutputStream out = null;

				try {

					fos = new FileOutputStream(filename, false);
					out = new ObjectOutputStream(fos);
					out.writeObject(vectorBean);
					out.close();
					fos.close();

				} catch (IOException ex) {

					System.err.println("***File name too large***");
				}
			} else {
				System.out.println("**The file already exists"
						+ file.getAbsolutePath());
			}
		}

	}

	public static void serializeVectorBeanSemantic(
			VSMFeatureVectorBeanEmbedded vectorBean, String sentence,
			String serName) {

		/*
		 * Taking 300k samples of each node and no more
		 */

		// File file = new
		// File("/Users/sameerkhurana10/Documents/serialization/"
		// + phiBean.getLabel());
		/*
		 * Location where sparse feature vectors are storeds
		 */
		File file = null;

		file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm/featurevecembeddedsem/"
						+ sentence + "/" + vectorBean.getLabel());

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/" + serName;
			// String filename =
			// "/Users/sameerkhurana10/Documents/testserialization/"
			// + phiBean.getLabel() + "_1.ser";

			/*
			 * Serializing the object
			 */
			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {

				fos = new FileOutputStream(filename, false);
				out = new ObjectOutputStream(fos);
				out.writeObject(vectorBean);
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		}

	}

	/*
	 * Method that returns the count map, used to retrieve the count map at the
	 * end of the operation on one particular file, so that we can serialize the
	 * count map for the next operation
	 */
	public static LinkedHashMap<String, Integer> getCountMap() {

		return countMap;

	}

}
