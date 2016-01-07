package VSMPhraseEmbeddings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import VSMConstants.VSMContant;
import VSMUtilityClasses.Alphabet;

public class SerializeEmbeddingBean {
	public static void serializeEmbeddedFeatureVecs(Vector phiSynEmbedded,
			Vector phiSemEmbedded, Vector psiSynEmbedded,
			Vector psiSemEmbedded, int wordCount, String phrase,
			String nonTerminal, Alphabet featureCount, String corpus) {

		EmbeddedVectorBean vectorBeanEmbedded = new EmbeddedVectorBean();

		vectorBeanEmbedded.setPhiSynEmbedded((DenseVector) phiSynEmbedded);
		vectorBeanEmbedded.setPhiSemEmbedded((DenseVector) phiSemEmbedded);

		vectorBeanEmbedded.setPsiSynEmbedded((DenseVector) psiSynEmbedded);
		vectorBeanEmbedded.setPsiSemEmbedded((DenseVector) psiSemEmbedded);

		vectorBeanEmbedded.setLabel(phrase);

		phiSynEmbedded = null;
		phiSemEmbedded = null;
		psiSemEmbedded = null;
		psiSynEmbedded = null;

		serializeEmbeddedVectorBeanPhrase(vectorBeanEmbedded, nonTerminal,
				phrase, wordCount, featureCount.countMap.get(phrase), corpus);

		System.out.println("Serialized the feature vector***");

	}

	private static int index;

	public static void serializeEmbeddedVectorBeanPhrase(
			EmbeddedVectorBean vectorBean, String nonTerminal, String phrase,
			int wordCount, int nodeCount, String corpus) {

		System.out.println("++I AM NEW++");
		if (nodeCount == 10000) {
			return;
		}
		boolean flag = false;

		String alreadyExistingPhrasePath = null;

		File[] files = new File(VSMContant.PHRASE_FEATURE_VECS_EMBEDDED)
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
							if (phrase.equalsIgnoreCase(fileWord.getName())) {
								flag = true;
								alreadyExistingPhrasePath = fileWord
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

		if (wordCount <= 3000) {

			if (flag == false) {
				file = new File(VSMContant.PHRASE_FEATURE_VECS_EMBEDDED
						+ "/folder_" + index + "/" + phrase + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + "-" + corpus
						+ ".ser");
			} else {
				System.out.println("****The Phrase already exists***"
						+ alreadyExistingPhrasePath);
				file = new File(alreadyExistingPhrasePath + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + "-" + corpus
						+ ".ser");
			}
		} else {

			if (flag == false) {
				index++;
				file = new File(VSMContant.PHRASE_FEATURE_VECS_EMBEDDED
						+ "/folder_" + index + "/" + phrase + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + "-" + corpus
						+ ".ser");
			} else {
				System.out.println("****The Phrase already exists***"
						+ alreadyExistingPhrasePath);
				file = new File(alreadyExistingPhrasePath + "/" + nonTerminal
						+ "/" + nonTerminal + "_" + nodeCount + "-" + corpus
						+ ".ser");
			}
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

}
