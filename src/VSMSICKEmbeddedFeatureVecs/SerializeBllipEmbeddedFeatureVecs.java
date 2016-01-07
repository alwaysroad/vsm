package VSMSICKEmbeddedFeatureVecs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBean;

public class SerializeBllipEmbeddedFeatureVecs {

	private int count;
	private int fileNum;
	private int index;
	private static int treeCount;

	public void serializeVectorBeanBLLIP(EmbeddedVectorBean vectorBean,
			int senTenceCount, String name, int nodeCount) {

		/*
		 * 
		 */
		fileNum = senTenceCount;
		/*
		 * Updating the file count
		 */
		fileNum = fileNum + 1;

		File file = null;

		if (fileNum <= 5) {

			file = new File(VSMContant.EMBEDDED_SENTENCE_VECS_BLLIP
					+ "/folder_" + index + "/" + name + "/"
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel() + "_"
					+ nodeCount + ".ser");
		} else {

			index++;
			fileNum = 0;
			fileNum = fileNum + 1;
			file = new File(VSMContant.EMBEDDED_SENTENCE_VECS_BLLIP
					+ "/folder_" + index + "/S_" + fileNum + "/"
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel() + "_"
					+ nodeCount + ".ser");
		}

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			System.out.println("***Forming the file***");
		} else {
			System.err.println("****The file Already Exists*****"
					+ file.getName());
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(file.getAbsoluteFile(), false);
			System.err.println(file.canWrite());
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
