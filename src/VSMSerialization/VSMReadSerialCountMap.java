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

public class VSMReadSerialCountMap {

	public static VSMCountMap readCountMapObj(String fileName) {

		VSMCountMap countMapObj = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			/*
			 * getting the countMap object
			 */
			countMapObj = (VSMCountMap) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return countMapObj;
	}
}
