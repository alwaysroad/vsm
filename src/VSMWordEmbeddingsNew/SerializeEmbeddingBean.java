package VSMWordEmbeddingsNew;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMUtilityClasses.Alphabet;

public class SerializeEmbeddingBean {
	public static void serializeEmbeddedFeatureVecs(Vector phiSynEmbedded,
			Vector phiSemEmbedded, Vector psiSynEmbedded,
			Vector psiSemEmbedded, int wordCount, String word,
			String nonTerminal, Alphabet featureCount) {

		EmbeddedVectorBean vectorBeanEmbedded = new EmbeddedVectorBean();

		vectorBeanEmbedded.setPhiSynEmbedded((DenseVector) phiSynEmbedded);
		vectorBeanEmbedded.setPhiSemEmbedded((DenseVector) phiSemEmbedded);

		vectorBeanEmbedded.setPsiSynEmbedded((DenseVector) psiSynEmbedded);
		vectorBeanEmbedded.setPsiSemEmbedded((DenseVector) psiSemEmbedded);

		vectorBeanEmbedded.setLabel(word);

		phiSynEmbedded = null;
		phiSemEmbedded = null;
		psiSemEmbedded = null;
		psiSynEmbedded = null;

		serializeEmbeddedVectorBeanWords(vectorBeanEmbedded, nonTerminal, word,
				wordCount, featureCount.countMap.get(word));

		System.out.println("Serialized the feature vector***");

	}

	private static int index;

	public static void serializeEmbeddedVectorBeanWords(
			EmbeddedVectorBean vectorBean, String nonTerminal, String word,
			int wordCount, int nodeCount) {

		System.out.println("++I AM NEW++");
		if (nodeCount == 10000) {
			return;
		}

		boolean flag = false;

		String alreadyExistingWordPath = null;

		File[] files = new File(VSMContant.WORD_FEATURE_VECS_EMBEDDED)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {

						return !file.isHidden();
					}
				});

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

		File file = null;

		if (wordCount <= 2000) {

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

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			System.out.println("***Forming the file***");
		} else {
			System.err.println("****The file Already Exists*****"
					+ file.getName());
			file.delete();
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {

			fos = new FileOutputStream(file.getAbsoluteFile(), false);
			// System.err.println(file.canWrite());
			out = new ObjectOutputStream(fos);
			out.writeObject(vectorBean);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fos.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
