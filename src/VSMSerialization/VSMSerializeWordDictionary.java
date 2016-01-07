package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMUtilityClasses.Alphabet;

public class VSMSerializeWordDictionary {

	public static void serializeFeatureDictionary(
			VSMWordDictionaryBean dictionaryBean) {

		File file = new File("/disk/scratch/s1444025");

		if (!file.exists()) {
			file.mkdirs();
		}
		String filename = file.getAbsolutePath() + "/worddictionary.ser";
		/*
		 * Delete befoe serializing
		 */
		File ditionaryFile = new File(filename);
		if (ditionaryFile.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(dictionaryBean);
			System.out.println("**serialized at** " + filename);

		} catch (IOException ex) {
			System.out.println("***An Exception Occured While serializing***  "
					+ ex);
		} finally {

			try {
				out.flush();
				fos.flush();
				out.close();
				fos.close();
			} catch (IOException e) {
				System.out
						.println("**Closing the stream can cause exception too**"
								+ e);
			}

		}

	}

}
