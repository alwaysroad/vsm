package VSMSICKEmbeddedFeatureVecs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;

import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;

public class SerializeEmbeddedBean {

	public static void serializeVectorBean(EmbeddedVectorBean vectorBean,
			String sentence, String serName) {

		File file = null;

		file = new File(VSMContant.SICK_EMBEDDED_CHUNK_VECS + "/" + sentence
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
}
