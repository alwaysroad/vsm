package VSMTests;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import no.uib.cipr.matrix.DenseVector;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import VSMConstants.VSMContant;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceEmbeddingBean;
import VSMUtilityClasses.VSMUtil;

public class TestReadSerializeSentenceVecBOWTrial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 914973760707369149L;

	public static void main(String... args) throws IOException {

		System.out.println("***newlijolnlny compiled**");

		PrintWriter writer = new PrintWriter(VSMContant.SIMILARITY_SCORE_DIFF,
				"UTF-8");

		// PrintWriter out = new PrintWriter(
		// "/afs/inf.ed.ac.uk/group/project/vsm/sentencesim/similaritydiff.txt");

		double[] modelScores = new double[500];

		// double[] newsim = new double[4500];

		String sentenceVecDirec = VSMContant.SENTENCE_EMBEDDINGS_BOW_TRIAL;

		/*
		 * Getting all the sentence vectors
		 */
		File[] vectorFiles = new File(sentenceVecDirec)
				.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return !file.isHidden();
					}
				});

		sortByNumber(vectorFiles);

		Collection<Double> goldSTandard = VSMUtil.getGoldStandardTrial()
				.values();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		List list = new ArrayList(goldSTandard);

		double[] goldScores = new double[list.size()];

		for (int i = 0; i < list.size(); i++)
			goldScores[i] = (double) list.get(i);
		double[][] sentences = new double[(vectorFiles.length / 2)][100];
		System.out.println(sentences[0][50]);
		double[][] evaluation = new double[(vectorFiles.length / 2)][2];
		int count = 0;
		for (int i = 0; i < vectorFiles.length; i += 2) {

			// System.out.println(i);

			/*
			 * Sentence pairs
			 */
			File sentenceVecFile1 = vectorFiles[i];
			File sentenceVecFile2 = vectorFiles[i + 1];

			System.out.println("1::" + sentenceVecFile1.getName() + " 2::"
					+ sentenceVecFile2.getName());

			// System.out.println("***Finding similarity between**"
			// + sentenceVecFile1.getName() + " and "
			// + sentenceVecFile2.getName());

			// DenseVector sentence1 = VSMReadSerialObject
			// .readSerializedSentenceVec(
			// sentenceVecFile1.getAbsolutePath())
			// .getSentenceVector();

			DenseVector sentence1 = VSMReadSerialObject
					.readSentenceEmbeddingAvgBean(sentenceVecFile1
							.getAbsolutePath());
			// System.out.println(sentence1);
			// double norm1 = VSMUtil.norm2(sentence1.getData());

			// DenseVector sentence2 = VSMReadSerialObject
			// .readSerializedSentenceVec(
			// sentenceVecFile2.getAbsolutePath())
			// .getSentenceVector();

			DenseVector sentence2 = VSMReadSerialObject
					.readSentenceEmbeddingAvgBean(sentenceVecFile2
							.getAbsolutePath());
			// System.out.println(sentence2);
			// double norm2 = VSMUtil.norm2(sentence2.getData());

			/*
			 * Calculating similarity
			 */
			// double dot = Math.abs(sentence1.dot(sentence2));
			// double divisor = norm1 * norm2;
			// System.out.println((dot / divisor) * 5);

			double[] data1 = sentence1.getData();
			for (int k = 0; k < sentence1.size(); k++) {
				sentences[count][k] = data1[k];
			}

			double[] data2 = sentence2.getData();
			int m = 0;
			for (int l = sentence1.size(); l < 100; l++) {
				// System.out.println(sentences[0][l]);
				// System.out.println(l);
				sentences[count][l] = data2[m];
				m++;
			}
			// sentences[1] = sentence2.getData();

			double similarityScore = VSMUtil.cosineSimilarity(
					sentence1.getData(), sentence2.getData());

			if (similarityScore < 1.0) {
				modelScores[count] = 1.0;
			} else {
				modelScores[count] = similarityScore;
			}

			evaluation[count][0] = modelScores[count];
			evaluation[count][1] = goldScores[count];
			count++;

		}

		VSMUtil.createMatFileSentenceEmbedBOWTrial(sentences, evaluation, count);

		for (double score : modelScores) {
			// System.out.println(score);
			break;
		}

		PearsonsCorrelation corr = new PearsonsCorrelation();

		// newsim = Arrays.copyOf(similarityScores, similarityScores.length -
		// 1);
		int k = 1;
		for (int j = 0; j < modelScores.length; j++) {
			writer.println((j + 1) + " " + (goldScores[j] - modelScores[j])
					+ "***gold***" + goldScores[j] + "****model****"
					+ modelScores[j]);

			k += 2;
		}

		writer.flush();
		writer.close();

		System.out.println(corr.correlation(goldScores, modelScores));

		/*
		 * Get the gold standard
		 */
		// HashMap<Integer, Double> goldStandard = VSMUtil.getGoldStandard();
		// // System.out.println(goldStandard);
		//
		// for (int i : modelSimilarityScore.keySet()) {
		// if (goldStandard.keySet().contains(i)) {
		// double modelScore = modelSimilarityScore.get(i);
		// System.out.println(i);
		// System.out.println("Model score!!!!" + modelScore);
		// // System.out.println("****Gold Standard!!!***" + goldStandard);
		// double goldScore = goldStandard.get(i);
		//
		// double diff = (goldScore - modelScore);
		//
		// /*
		// * Writing the difference in a file, to check later
		// */
		// out.println("Sentece Pair: " + Integer.toString(i)
		// + "(gold-model): " + Double.toString(diff));
		//
		// }
		// }

		// out.close();

	}

	public static void sortByNumber(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				int n1 = extractNumer(o1.getName());
				int n2 = extractNumer(o2.getName());
				return n1 - n2;
			}

			private int extractNumer(String name) {

				int i = 0;

				try {
					int s = name.indexOf('_') + 1;
					int e = name.lastIndexOf('.');
					String number = name.substring(s, e);
					i = Integer.parseInt(number);
				} catch (Exception e) {
					i = 0;
				}
				return i;

			}
		});
	}
}
