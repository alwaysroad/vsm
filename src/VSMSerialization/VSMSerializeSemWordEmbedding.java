package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

public class VSMSerializeSemWordEmbedding {

	public static void serializeVectorBean(
			VSMWordEmbeddingSem wordEmbeddingSem, String token, String label,
			int tokenCount) {

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

		file = new File("/afs/inf.ed.ac.uk/group/project/vsm/wordembeddingser/"
				+ token + "/" + label);

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/wordEmbeddingSem_"
					+ tokenCount + ".ser";

			/*
			 * Serialising the object
			 */
			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {

				fos = new FileOutputStream(filename, false);
				out = new ObjectOutputStream(fos);
				out.writeObject(wordEmbeddingSem);
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		}

	}

	public static void serializeVectorBeanSyn(
			VSMWordEmbeddingSyn wordEmbeddingSyn, String token) {

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

		file = new File("/afs/inf.ed.ac.uk/group/project/vsm/wordembeddingser/"
				+ token);

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/wordEmbeddingSyn.ser";

			/*
			 * Serialising the object
			 */
			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {

				fos = new FileOutputStream(filename, false);
				out = new ObjectOutputStream(fos);
				out.writeObject(wordEmbeddingSyn);
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		}

	}

}
