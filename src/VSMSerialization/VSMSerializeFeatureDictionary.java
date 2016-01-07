package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMConstants.VSMContant;
import VSMUtilityClasses.Alphabet;

public class VSMSerializeFeatureDictionary {

	public void serializeFeatureDictionary(VSMDictionaryBean dictionaryBean,
			String nonTerminal) {

		File file = new File(VSMContant.SYNTACTIC_FEATURE_DICTIONARY_FOLDER
				+ nonTerminal + "/");
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdirs();
		} else {
			// System.out.println("already exists");
		}
		String filename = file.getAbsolutePath() + "/dictionary.ser";
		/*
		 * Delete befoe serializing
		 */
		File ditionaryFile = new File(filename);
		if (ditionaryFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(dictionaryBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
