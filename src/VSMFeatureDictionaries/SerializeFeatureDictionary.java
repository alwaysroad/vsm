package VSMFeatureDictionaries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import VSMConstants.VSMContant;
import VSMSerialization.VSMDictionaryBean;

public class SerializeFeatureDictionary {

	public void serializeFeatureDictionary(VSMDictionaryBean dictionaryBean,
			String nonTerminal) {

		File file = new File(VSMContant.SYNTACTIC_FEATURE_DICTIONARY_FOLDER
				+ nonTerminal + "/");
		if (!file.exists()) {
			file.mkdirs();
		}
		String filename = file.getAbsolutePath() + "/dictionary.ser";

		File ditionaryFile = new File(filename);
		if (ditionaryFile.exists()) {
			file.delete();
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);

			out = new ObjectOutputStream(fos);

			out.writeObject(dictionaryBean);

			out.close();

			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
