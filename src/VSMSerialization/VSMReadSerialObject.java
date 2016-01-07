package VSMSerialization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import VSMBinaryFeatureVectors.FeatureVectorBean;
import no.uib.cipr.matrix.DenseVector;

public class VSMReadSerialObject {
	public static VSMFeatureVectorBean readFeatureVectorBean(String fileName) {

		VSMFeatureVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMFeatureVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	public static FeatureVectorBean readBinaryFeaturVec(String fileName) {

		FeatureVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (FeatureVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * Read vector bean embedded
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMFeatureVectorBeanEmbedded readFeatureVectorBeanEmbedded(
			String fileName) {

		VSMFeatureVectorBeanEmbedded vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMFeatureVectorBeanEmbedded) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * Read vector bean embedded
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMSentenceVecBean readSerializedSentenceVec(String fileName) {

		VSMSentenceVecBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMSentenceVecBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * Read vector bean embedded
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMRandomSentenceBean readSerializedRandSentenceVec(
			String fileName) {

		VSMRandomSentenceBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMRandomSentenceBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * TODO add this method to the repository. First update the repo from the
	 * dice machine
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMWordFeatureVectorBean readSemanticFeatureVecBean(
			String fileName) {

		VSMWordFeatureVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMWordFeatureVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * TODO add this method to the repository. First update the repo from the
	 * dice machine
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMWordEmbeddingSem readWordEmbeddingSemBean(String fileName) {

		VSMWordEmbeddingSem vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (VSMWordEmbeddingSem) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
	}

	/**
	 * TODO add this method to the repository. First update the repo from the
	 * dice machine
	 * 
	 * @param fileName
	 * @return
	 */
	public static VSMWordEmbeddingMapBean readWordEmbeddingMapBean(
			String fileName) {

		VSMWordEmbeddingMapBean mapBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			mapBean = (VSMWordEmbeddingMapBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return mapBean;
	}

	/**
	 * TODO add this method to the repository. First update the repo from the
	 * dice machine
	 * 
	 * @param fileName
	 * @return
	 */
	public static DenseVector readSentenceEmbeddingAvgBean(String fileName) {

		DenseVector vecBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vecBean = (DenseVector) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vecBean;
	}
	// public static VSMEmbeddingsMapBean readEmbeddingsMapBean(
	// String fileName) {
	//
	// VSMEmbeddingsMapBean embeddingsMapBean = null;
	// FileInputStream fis = null;
	// ObjectInputStream in = null;
	//
	// try {
	// fis = new FileInputStream(fileName);
	// in = new ObjectInputStream(fis);
	// embeddingsMapBean = (VSMEmbeddingsMapBean) in.readObject();
	// in.close();
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// } catch (ClassNotFoundException cnfe) {
	// cnfe.printStackTrace();
	// }
	//
	// return embeddingsMapBean;
	// }
}
