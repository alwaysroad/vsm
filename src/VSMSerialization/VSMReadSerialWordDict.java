package VSMSerialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class VSMReadSerialWordDict {
	public static VSMWordDictionaryBean readSerializedDictionary(String fileName) {

		VSMWordDictionaryBean dictionaryBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			dictionaryBean = (VSMWordDictionaryBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return dictionaryBean;
	}
}
