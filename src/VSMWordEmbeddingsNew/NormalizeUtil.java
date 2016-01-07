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
import java.util.LinkedHashMap;

import VSMUtilityClasses.VSMUtil;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;

public class NormalizeUtil {

	private static LinkedHashMap<String, DenseVector> embeddingsMap;
	private static String embeddingsMapPathRoot;
	private static String embeddingsMapFileName;

	static {
		embeddingsMap = new LinkedHashMap<>();
		embeddingsMapPathRoot = "/group/project/vsm-nfs/phraseEmbeddings_new";

	}

	public static void main(String... args) {
		getUserInput(args);
		deserializeEmbeddingsMap();
		normalize();
		serializeMap();
		writeToFile();
	}

	private static void serializeMap() {
		System.out.println("++SERIALIZING BACK THE MAP++");
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(
					embeddingsMapPathRoot + "/" + embeddingsMapFileName
							+ "N.ser"));
			out.writeObject(embeddingsMap);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		System.out.println("+++DONE+++");
	}

	private static void normalize() {
		System.out.println("++NORMALIZING MAp++");
		for (String word : embeddingsMap.keySet()) {
			DenseVector vec = embeddingsMap.get(word);
			System.out.println("++++Vector Size+++" + vec.size());
			vec = (DenseVector) VSMUtil.normalizeVec(vec);
			embeddingsMap.put(word, vec);
		}
		System.out.println("++DONE++");

	}

	private static void writeToFile() {
		System.out.println("WRITING EMBEDDINGS TO A FILE");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new File(embeddingsMapPathRoot + "/"
					+ embeddingsMapFileName + "N.txt"));
			for (String word : embeddingsMap.keySet()) {
				writer.write(word);
				writer.write(" ");

				DenseVector vec = embeddingsMap.get(word);
				for (VectorEntry e : vec) {
					writer.write(((Double) e.get()).toString());
					writer.write(" ");
				}
				writer.write("\n");
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}

		System.out.println("+++DONE WRITING+++");
	}

	@SuppressWarnings("unchecked")
	private static void deserializeEmbeddingsMap() {
		System.out.println("+++DESERIALIZE EMBEDDINGS MAP++++");
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(
					embeddingsMapPathRoot + "/" + embeddingsMapFileName
							+ ".ser"));
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

		System.out.println("DONE " + embeddingsMap.size());

	}

	private static void getUserInput(String[] args) {

		if (args.length > 0) {
			embeddingsMapFileName = args[0];
		} else {
			System.out
					.println("PLEASE GIVE THE EMBEDDINGS MAP THAT NEEDS TO BE NORMALIZED");
			System.exit(-1);
		}

	}
}
