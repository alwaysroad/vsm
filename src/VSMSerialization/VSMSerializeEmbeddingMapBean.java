package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

public class VSMSerializeEmbeddingMapBean {

	public static void serializeVectorBean(VSMWordEmbeddingMapBean embeddingsBean) {

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
				"/afs/inf.ed.ac.uk/group/project/vsm.restored/ccaembeddingser");

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();

			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/wordEmbedding.ser";
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
				out.writeObject(embeddingsBean);
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		}

	}

}
