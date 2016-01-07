package VSMFeatureDictionaries;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

import VSMSerialization.VSMDictionaryBean;

public class ReadSerializedDictionary {

	public static VSMDictionaryBean readSerializedDictionary(String fileName,
			Logger logger) {

		VSMDictionaryBean dictionaryBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			dictionaryBean = (VSMDictionaryBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			if (logger != null) {
				logger.info("The dictionary does not already exists for the non terminal");
			}
			return null;

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return dictionaryBean;
	}
}
