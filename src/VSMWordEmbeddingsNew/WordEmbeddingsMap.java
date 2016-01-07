package VSMWordEmbeddingsNew;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import Jama.Matrix;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLDouble;

import no.uib.cipr.matrix.DenseVector;

public class WordEmbeddingsMap {

	private static LinkedHashMap<String, DenseVector> embeddingsMap;

	private static String embeddingsMapPath;

	private static String serializeMapPath;

	private static double[][] U;

	static {

		serializeMapPath = "/amd/nfs/lammasu/disk/ptn199/vsm-nfs/WordEmbeddingsMap";
		embeddingsMapPath = "/amd/nfs/lammasu/disk/ptn199/vsm-nfs/WordEmbeddingsMap/wordEmbeddingsMapLower.ser";
	}

	public static void main(String... args) {
		getEmbeddingsMatrix(args);
		getEmbeddingsMap();
		modifyEmbeddingsMap();
		serializeEmbeddingsMap(args);
		writeToTextFile(args);
	}

	private static void writeToTextFile(String[] args) {
		System.out.println("WRITING EMBEDDINGS TO A FILE");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(serializeMapPath + "/" + args[1] + ".txt");
			for (String word : embeddingsMap.keySet()) {
				double[] vec = embeddingsMap.get(word).getData();
				writer.write(word);
				writer.write(" ");
				for (double d : vec) {
					writer.write(((Double) d).toString());
					writer.write(" ");
				}

				writer.write("\n");
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {
			writer.close();
		}

		System.out.println("++DONE++");

	}

	private static void serializeEmbeddingsMap(String[] args) {
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(serializeMapPath
					+ "/" + args[1] + ".ser"));
			out.writeObject(embeddingsMap);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private static void modifyEmbeddingsMap() {
		System.out.println("++Forming the Embeddings Map again++");
		int count = 0;
		for (String word : embeddingsMap.keySet()) {
			DenseVector vec = new DenseVector(U[count]);
			embeddingsMap.put(word, vec);
		}

		System.out.println("++DONE++");

	}

	@SuppressWarnings("unchecked")
	private static void getEmbeddingsMap() {
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(embeddingsMapPath));
			embeddingsMap = (LinkedHashMap<String, DenseVector>) in
					.readObject();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

	}

	private static void getEmbeddingsMatrix(String[] args) {
		MatFileReader fileReader = null;
		File file = null;
		try {
			if (args.length > 0) {
				file = new File(
						"/amd/nfs/lammasu/disk/ptn199/vsm-nfs/WordEmbeddingsMap/"
								+ args[0] + ".mat");
			} else {
				System.out
						.println("PLEASE GIVE THE MAT FILE NAME THAT HOLDS THE EMBEDDINGS");
				System.exit(-1);
			}
			fileReader = new MatFileReader(file);

		} catch (IOException e) {
			e.printStackTrace();
		}

		U = ((MLDouble) fileReader.getMLArray("U")).getArray();

	}

}
