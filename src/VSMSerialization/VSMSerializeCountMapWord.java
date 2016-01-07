package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class VSMSerializeCountMapWord {

	public static void serializeCountMap(VSMCountMap countMapObject)
			throws IOException {
		/*
		 * Creating the directory
		 */
		// File file = new File(
		// "/Users/sameerkhurana10/Documents/countmap/countMap.ser");

		File file = new File(
				"/Users/sameerkhurana10/Documents/countmapwordvecs/countMap.ser");

		if (!file.exists()) {
			file.createNewFile();
		} else {
			System.out.println("already exists");
			/*
			 * First delete the old file
			 */
			file.delete();
			/*
			 * The create a new empty file
			 */
			file.createNewFile();
		}
		String filename = file.getAbsolutePath();

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			System.out.println(fos);
			out = new ObjectOutputStream(fos);
			System.out.println(out);
			out.writeObject(countMapObject);
			System.out.println("serialized");
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
