package VSMSerialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

public class VSMReadSerialMatrix {
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
			logger.info("The dictionary does not already exists for the non terminal");
			return null;

		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return dictionaryBean;
	}
}
