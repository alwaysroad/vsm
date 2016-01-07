package VSMBinaryFeatureVectors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMConstants.VSMContant;

public class SerializeFeatureVectorBean {

	private static LinkedHashMap<String, Integer> countMap;
	private static int count;
	private static int fileIdx;

	/*
	 * No args constructor
	 */
	public SerializeFeatureVectorBean() {
		countMap = new LinkedHashMap<String, Integer>();
		count = 0;
	}

	/*
	 * Constructor with arguments, fetch the count map and send it to this
	 * constructor, so that the count can start making files from where we left
	 * off in the previous iteration
	 */

	public SerializeFeatureVectorBean(LinkedHashMap<String, Integer> countMap) {
		SerializeFeatureVectorBean.countMap = countMap;
		count = 0;
	}

	public boolean serializeVectorBean(FeatureVectorBean vectorBean) {

		boolean flag = false;

		Set<String> labels = countMap.keySet();
		/*
		 * If the map already contains the label then get the count variable
		 */
		if (labels.contains(vectorBean.getLabel())) {
			count = countMap.get(vectorBean.getLabel());
			count += 1;
		} else {

			count = 1;
		}
		/*
		 * The put method replaces the value of the existing key
		 */
		countMap.put(vectorBean.getLabel(), count);

		File file = null;
		if (count <= 30000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_1");
		} else if (count <= 60000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_2");
		} else if (count <= 90000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_3");
		} else if (count <= 120000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_4");
		} else if (count <= 150000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_5");
		} else if (count <= 180000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_6");
		} else if (count <= 210000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_7");
		} else if (count <= 240000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_8");
		} else if (count <= 270000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_9");
		} else if (count <= 300000) {

			file = new File(VSMContant.BINARY_FEATURE_VECTORS
					+ vectorBean.getLabel() + "/" + vectorBean.getLabel()
					+ "_10");
		} else {

			System.out.println("**Done***");
			flag = true;

		}

		if (file != null) {
			if (!file.exists()) {
				file.mkdirs();
				fileIdx = 1;
			}

			/*
			 * The .ser file name
			 */
			String filename = file.getAbsolutePath() + "/"
					+ vectorBean.getLabel() + "_" + fileIdx + ".ser";

			FileOutputStream fos = null;
			ObjectOutputStream out = null;

			try {

				fos = new FileOutputStream(filename, false);
				out = new ObjectOutputStream(fos);
				out.writeObject(vectorBean);
				fileIdx++;
				out.close();
				fos.close();

			} catch (IOException ex) {

				System.err.println("***File name too large***");
			}
		} else {
			System.out.println("****Done no more serialization***");
		}

		return flag;

	}
}