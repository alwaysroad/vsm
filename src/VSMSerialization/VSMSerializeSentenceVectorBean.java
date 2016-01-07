package VSMSerialization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMConstants.VSMContant;

public class VSMSerializeSentenceVectorBean {

	public static void sentenceVectorBean(VSMSentenceVecBean vecBean,
			String sentence) {

		File file = new File(VSMContant.SICK_SENTENCE_EMBED_SYNTACTIC);
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdir();
		} else {
			// System.out.println("already exists");
		}
		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";
		/*
		 * Delete before serialising
		 */
		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void sentenceVectorBeanSynSem(VSMSentenceVecBean vecBean,
			String sentence) {

		File file = new File(VSMContant.SICK_SENTENCE_EMBED_SYNTACTIC_SEM);
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdir();
		} else {
			// System.out.println("already exists");
		}

		System.out.println("**inside serialization**************" + file.getName());

		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";
		/*
		 * Delete before serialising
		 */
		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void sentenceVectorBeanPhrases(VSMSentenceVecBean vecBean,
			String sentence) {

		File file = new File(VSMContant.SICK_SENTENCE_EMBED_PHRASAL_BOW);
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdir();
		} else {
			// System.out.println("already exists");
		}
		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";
		/*
		 * Delete before serialising
		 */
		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void sentenceVectorBeanPhrasesInside(
			VSMSentenceVecBean vecBean, String sentence) {

		File file = new File(VSMContant.SICK_SENTENCE_EMBED_PHRASAL_BOW1);
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdir();
		} else {
			// System.out.println("already exists");
		}
		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";
		/*
		 * Delete before serialising
		 */
		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public static void sentenceVectorBeanSyntactic(VSMSentenceVecBean vecBean,
			String sentence) {

		File file = new File(VSMContant.SICK_SENTENCE_EMBED_SYNTACTIC);
		if (!file.exists()) {
			// System.out.println("hi");
			file.mkdir();
		} else {
			// System.out.println("already exists");
		}
		String filename = file.getAbsolutePath() + "/" + sentence + ".ser";
		/*
		 * Delete before serialising
		 */
		File sentenceVecFile = new File(filename);
		if (sentenceVecFile.exists()) {
			file.delete();
		}
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			// System.out.println(fos);
			out = new ObjectOutputStream(fos);
			// System.out.println(out);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);
			out.close();
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}
