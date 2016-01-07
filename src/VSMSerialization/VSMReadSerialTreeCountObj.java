package VSMSerialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * The class is used to read the the serialized object from the file and return
 * the count map object
 * 
 * @author sameerkhurana10
 *
 */

public class VSMReadSerialTreeCountObj {

	public static VSMTreeCount redTreeCountObj(String fileName) {

		VSMTreeCount treeCountObj = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			/*
			 * getting the countMap object
			 */
			treeCountObj = (VSMTreeCount) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return treeCountObj;
	}
}
