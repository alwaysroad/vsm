package VSMTrainingBinarySentenceVectors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMWordFeatureVectorBean;

public class Serialization {

	private int count;
	private int fileNum;
	private int index;
	private static int treeCount;

	public void serializeVectorBeanBLLIP(VSMFeatureVectorBean vectorBean,
			int treeCount, int nodeCount) {

		/*
		 * 
		 */

		/*
		 * Updating the file count
		 */
		fileNum = fileNum + 1;

		File file = null;

		if (fileNum <= 5000) {

			file = new File(VSMContant.BINARY_SENTENCE_VECS_BLLIP + "/folder_"
					+ index + "/S_" + treeCount + "/" + vectorBean.getLabel()
					+ "/" + vectorBean.getLabel() + "_" + nodeCount + ".ser");
		} else {

			index++;
			fileNum = 0;
			fileNum = fileNum + 1;
			file = new File(VSMContant.BINARY_SENTENCE_VECS_BLLIP + "/folder_"
					+ index + "/S_" + treeCount + "/" + vectorBean.getLabel()
					+ "/" + vectorBean.getLabel() + "_" + nodeCount + ".ser");
		}

		if (!file.exists()) {
			file.getParentFile().mkdirs();

		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(file.getAbsoluteFile(), false);

			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
