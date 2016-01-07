package VSMTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;

import org.junit.Test;
import org.netlib.util.doubleW;

import VSMSICKEmbeddedFeatureVecs.EmbeddedFeatureVectors;
import VSMSICKEmbeddedFeatureVecs.EmbeddedVectorBean;
import VSMSentenceEmbeddingFull.SentenceVectorBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.VSMUtil;
import VSMWordEmbeddingsNew.WordEmbeddingBean;

//import edu.upenn.cis.swell.MainMethods.VSMEmbeddingsMapBean;

public class TestReadSerializedObject {
	// @Test
	// public void testReadSerializedObject() {
	// String fileName =
	// "/afs/inf.ed.ac.uk/group/project/vsm/serializewordembeddingmap/embeddings.ser";
	//
	// // VSMMatrixBean matrixBean = VSMReadFeatureMatrix
	// // .readFeatureMatrix(fileName);
	// // System.out.println(matrixBean);
	// // System.out.println(matrixBean.getPhi());
	// VSMEmbeddingsMapBean embeddingsMap = VSMReadSerialObject
	// .readEmbeddingsMapBean(fileName);
	//
	// HashMap<String, double[]> map = embeddingsMap.getEmbeddings();
	// double[] vector = map.get("courageously");
	// System.out.println(vector);
	//
	// int count = 0;
	// for (double val : vector) {
	// count++;
	// System.out.println(val);
	//
	// }
	//
	// System.out.println(count);
	// System.out.println(map.keySet().size());
	//
	// // System.out.println(vectorBean.getPhi());
	// // double[] phi = phiBean.getPhi();
	// // double[] psi = phiBean.getPsi();
	// // DenseVector phi = vectorBean.getSentenceVector();
	// // System.out.println(phi.length);
	// // System.out.println(phi);
	// // for (double d : psi) {
	// // System.out.println(d);
	// // }
	// // System.out.println(psi.length);
	// //
	// // Iterator<VectorEntry> itr1 = phi.iterator();
	// // while (itr1.hasNext()) {
	// // VectorEntry e = itr1.next();
	// // System.out.println(e.index());
	// // }
	// //
	// // for (double d : phi.getData()) {
	// // System.out.println(d);
	// // }
	//
	// }

	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String... args) throws FileNotFoundException {
		String file = "/Users/sameerkhurana10/training_corpus/sentenceEmbeddings.ser";
		LinkedList<double[]> sentenceEmbeddings = new LinkedList<double[]>();
		LinkedList<double[]> featureList = new LinkedList<double[]>();
		LinkedList<Double> cosineSimFeatures = new LinkedList<Double>();

		PrintWriter writer = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			sentenceEmbeddings = (LinkedList<double[]>) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		try {
			writer = new PrintWriter(
					new File(
							"/Users/sameerkhurana10/training_corpus/sentenceEmbeddings.txt"));
			for (double[] arr : sentenceEmbeddings) {
				DenseVector vec = new DenseVector(arr);
				vec = (DenseVector) VSMUtil.normalizeVec(vec);

				for (double d : vec.getData()) {
					writer.write(((Double) d).toString());
					writer.write(" ");
				}

				writer.write("\n");
			}
		} finally {
			writer.close();
		}

		for (int i = 0; i < sentenceEmbeddings.size(); i += 2) {

			double[] sent1 = sentenceEmbeddings.get(i);
			double[] sent2 = sentenceEmbeddings.get(i + 1);

			DenseVector sent1vec = new DenseVector(sent1);
			DenseVector sent2vec = new DenseVector(sent2);

			double similarityScore = VSMUtil.cosineSimilarity(
					sent1vec.getData(), sent2vec.getData());

			cosineSimFeatures.add(similarityScore);

			// DenseVector diff = (DenseVector) sent1vec.add(sent2vec);
			// the sentence difference feature
			// featureList.add(diff.getData());
		}

		System.out.println(featureList.size());
		PrintWriter writer1 = null;

		try {
			writer1 = new PrintWriter(
					new File(
							"/Users/sameerkhurana10/training_corpus/cosineSimFeatures.txt"));

			for (double d : cosineSimFeatures) {
				writer1.write(((Double) d).toString());
				writer1.write("\n");
			}
		} finally {
			writer1.close();
		}
	}
}
