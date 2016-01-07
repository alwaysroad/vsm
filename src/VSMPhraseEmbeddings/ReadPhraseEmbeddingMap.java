package VSMPhraseEmbeddings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import jeigen.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import Jama.Matrix;
import VSMLogger.VSMLogger;
import VSMUtilityClasses.VSMUtil;

public class ReadPhraseEmbeddingMap {

	private static final Logger LOGGER;
	private static final String phraseEmbeddingsURL;
	private static HashMap<String, DenseVector> embeddingsMap;

	private static final String phraseEmbeddingMatrixURL;

	static {
		LOGGER = VSMLogger.setup(ReadPhraseEmbeddingMap.class.getName());
		phraseEmbeddingsURL = "/group/project/vsm-nfs/PhraseEmbeddings/phraseEmbeddings.ser";
		phraseEmbeddingMatrixURL = "/group/project/vsm-nfs/PhraseEmbeddings/phraseEmbeddingsNorm.ser";
	}

	public static void main(String... args) {

		getUnnormalizedEmneddingsMap();
		normalizeEmbeddingsMap();
		serializeEmbeddingsMap();

	}

	private static void getUnnormalizedEmneddingsMap() {
		ObjectInput in = null;
		try {
			System.out.println("Reading the object");
			in = new ObjectInputStream(new FileInputStream(phraseEmbeddingsURL));
			embeddingsMap = (HashMap<String, DenseVector>) in.readObject();
		} catch (IOException e) {
			LOGGER.severe("IOException while reading the object file" + e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LOGGER.severe("Class not found exception while reading the class"
					+ e);
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private static void serializeEmbeddingsMap() {

		System.out.println("+++SERIALIZING THE EmbeddingsMap OBJECT++");
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(
					phraseEmbeddingMatrixURL));
			out.writeObject(embeddingsMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		System.out.println("++DONE SERIALIZATION++");

	}

	private static void normalizeEmbeddingsMap() {
		System.out.println("Normalizing");
		for (String phrase : embeddingsMap.keySet()) {
			DenseVector vec = embeddingsMap.get(phrase);
			normalizeVec(vec);
			// overriding the previous vector with the normalized vector
			embeddingsMap.put(phrase, vec);

		}

		System.out.println("NEW EMBEDDINGS MAP FORMED");
	}

	private static void normalizeVec(DenseVector vec) {

		double norm2 = VSMUtil.norm2(vec.getData());
		if (!(Double.isNaN(norm2))) {
			vec = vec.scale((double) 1 / (double) norm2);
		} else {
			System.out.println("++++Norm is NAN+++");
			LOGGER.severe("NORM is NAN for the vector");
		}
	}

}
