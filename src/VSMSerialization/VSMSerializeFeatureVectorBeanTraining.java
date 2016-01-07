package VSMSerialization;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import VSMConstants.VSMContant;

public class VSMSerializeFeatureVectorBeanTraining {

	private static int treeCount;
	private static int count;
	private static int fileNum;
	private static int index;

	/*
	 * No args constructor
	 */
	public VSMSerializeFeatureVectorBeanTraining() {
		treeCount = 0;
		// count = 0;
	}

	/*
	 * Constructor with arguments, fetch the count map and send it to this
	 * constructor, so that the count can start making files from where we left
	 * off in the previous iteration
	 */

	public VSMSerializeFeatureVectorBeanTraining(int treeCount) {
		VSMSerializeFeatureVectorBeanTraining.treeCount = treeCount;
	}

	public void serializeVectorBean(VSMFeatureVectorBean vectorBean,
			int treeCount, int nodeCount) {

		VSMSerializeFeatureVectorBeanTraining.treeCount = treeCount;

		/*
		 * 
		 */

		System.out.println("***Forming the file***");
		File file = new File(VSMContant.SICK_TRIAL_BINARY_SENT_VECS_FOLDER
				+ treeCount + "/" + vectorBean.getLabel());
		if (!file.exists()) {
			file.mkdirs();
		}

		/*
		 * The .ser file name. The file in which the feature vectors are
		 * serialized
		 */
		String filename = file.getAbsolutePath() + "/" + vectorBean.getLabel()
				+ "_" + nodeCount + ".ser";
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";

		/*
		 * Serializing the object
		 */
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(filename, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void serializeVectorBeanSem(VSMWordFeatureVectorBean vectorBean,
			int treeCount, int nodeCount) {
		count++;
		VSMSerializeFeatureVectorBeanTraining.treeCount = treeCount;

		/*
		 * 
		 */

		System.out.println("***Forming the file***");
		File file = new File(
				"/afs/inf.ed.ac.uk/group/project/vsm/BLIPPsentencevectors/S_"
						+ treeCount + "/" + vectorBean.getLabel());
		if (!file.exists()) {
			file.mkdirs();
		}

		/*
		 * The .ser file name. The file in which the feature vectors are
		 * serialized
		 */
		String filename = file.getAbsolutePath() + "/" + vectorBean.getLabel()
				+ "_" + nodeCount + "sem.ser";
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";

		/*
		 * Serializing the object
		 */
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(filename, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void serializeVectorBeanBLLIP(VSMFeatureVectorBean vectorBean,
			int treeCount, int nodeCount) {

		VSMSerializeFeatureVectorBeanTraining.treeCount = treeCount;

		/*
		 * 
		 */

		/*
		 * Updating the file count
		 */
		fileNum = fileNum + 1;

		File file = null;

		if (fileNum <= 5000) {

			file = new File(VSMContant.BINARY_SENTENCE_VECS_BLLIP + "/folder_"
					+ index + "/S_" + fileNum + "/" + vectorBean.getLabel()
					+ "/" + vectorBean.getLabel() + "_" + nodeCount + ".ser");
		} else {

			index++;
			fileNum = 0;
			fileNum = fileNum + 1;
			file = new File(VSMContant.BINARY_SENTENCE_VECS_BLLIP + "/folder_"
					+ index + "/S_" + fileNum + "/" + vectorBean.getLabel()
					+ "/" + vectorBean.getLabel() + "_" + nodeCount + ".ser");
		}

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			System.out.println("***Forming the file***");
		} else {
			System.err.println("****The file Already Exists*****"
					+ file.getName());
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(file.getAbsoluteFile(), false);
			System.err.println(file.canWrite());
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void serializeEmbeddedVectorBeanWords(
			VSMFeatureVectorBeanEmbedded vectorBean, String nonTerminal,
			String word, int wordCount, int nodeCount) {

		// VSMSerializeFeatureVectorBeanTraining.treeCount = treeCount;

		// ArrayList<String> words = new ArrayList<String>();

		boolean flag = false;

		String alreadyExistingWordPath = null;

		File[] files = new File(VSMContant.WORD_FEATURE_VECS_EMBEDDED)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						// TODO Auto-generated method stub
						return !file.isHidden();
					}
				});
		/*
		 * If there are files and the array is not an empty one, form the list
		 * of words that already have a directory, this should be a very fast
		 * loop, no worries here
		 */
		if (files != null && files.length > 0) {
			main: for (File file : files) {
				if (file.isDirectory()) {
					File[] wordDirecs = file.listFiles();
					if (wordDirecs != null) {
						for (File fileWord : wordDirecs) {
							if (word.equalsIgnoreCase(fileWord.getName())) {
								flag = true;
								alreadyExistingWordPath = fileWord
										.getAbsolutePath();
								break main;
							}
						}
					}
				}
			}
		}

		/*
		 * 
		 */

		/*
		 * Updating the file count
		 */
		// fileNum = fileNum + 1;

		File file = null;

		/*
		 * Forming the appropriate directory structure
		 */

		if (wordCount < 30000) {
			// index = 1;
			// file = new File(VSMContant.BINARY_SENTENCE_VECS_BLLIP +
			// "/folder_"
			// + index + "/S_" + fileNum + "/" + vectorBean.getLabel());
			if (flag == false) {
				file = new File(VSMContant.WORD_FEATURE_VECS_EMBEDDED
						+ "/folder_" + index + "/" + word + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + ".ser");
			} else {
				System.out.println("****The word already exists***"
						+ alreadyExistingWordPath);
				file = new File(alreadyExistingWordPath + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + ".ser");
			}
		} else {

			// fileNum = 0;
			// fileNum = fileNum + 1;
			if (flag == false) {
				index++;
				file = new File(VSMContant.WORD_FEATURE_VECS_EMBEDDED
						+ "/folder_" + index + "/" + word + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + ".ser");
			} else {
				System.out.println("****The word already exists***"
						+ alreadyExistingWordPath);
				file = new File(alreadyExistingWordPath + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + ".ser");
			}
		}
		// /BLLIPBinarySentenceVecs/folder_1/S_12642/JJ

		// File file = new File(
		// "/afs/inf.ed.ac.uk/group/project/vsm.restored/BLLIPSentenceVecs/S_"
		// + treeCount + "/" + vectorBean.getLabel());
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			System.out.println("***Forming the file***");
		} else {
			System.err.println("****The file Already Exists*****"
					+ file.getName());
		}

		/*
		 * The .ser file name. The file in which the feature vectors are
		 * serialized
		 */
		// String filename = file.getAbsolutePath() + "/" +
		// vectorBean.getLabel()
		// + "_" + nodeCount + ".ser";
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";

		/*
		 * Serializing the object
		 */
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(file.getAbsoluteFile(), false);
			System.err.println(file.canWrite());
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 * @param vectorBean
	 * @param path
	 */
	public void serializeVectorBeanSentenceRand(
			VSMRandomSentenceBean vectorBean, String path) {

		/*
		 * 
		 */

		System.out.println("***Forming the file***");
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
		}

		/*
		 * The .ser file name. The file in which the feature vectors are
		 * serialized
		 */
		String filename = file.getAbsolutePath();
		// String filename =
		// "/Users/sameerkhurana10/Documents/testserialization/"
		// + phiBean.getLabel() + "_1.ser";

		/*
		 * Serializing the object
		 */
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(filename, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);
			out.close();
			fos.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	/*
	 * Method that returns the count map, used to retrieve the count map at the
	 * end of the operation on one particular file, so that we can serialize the
	 * count map for the next operation
	 */
	public static int getTreeCount() {

		return treeCount;

	}

}
