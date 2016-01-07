package VSMPhraseEmbeddings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import no.uib.cipr.matrix.DenseVector;

public class PhraseMap {

	private static String embeddingsMapFilesPath;

	private static HashMap<String, DenseVector> totalEmbeddingsMap;

	private static HashMap<String, DenseVector> localEmbeddingsMap;

	private static File[] embeddingsFiles;

	static {
		embeddingsMapFilesPath = "/group/project/vsm-nfs/PhraseEmbeddingsNPNew";
		totalEmbeddingsMap = new LinkedHashMap<String, DenseVector>();
	}

	public static void main(String... args) {
		embeddingsFiles = getEmbeedingsFiles();
		for (File file : embeddingsFiles) {

			localEmbeddingsMap = readObject(file);

			System.out.println("PUTTING PHRASES INTO THE TOTAL MAP++ "
					+ localEmbeddingsMap.keySet().size());

			for (String phrase : localEmbeddingsMap.keySet()) {
				DenseVector vec = localEmbeddingsMap.get(phrase);
				totalEmbeddingsMap.put(phrase, vec);
			}
		}

		serializeMap();
	}

	private static void serializeMap() {
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(
					new FileOutputStream(
							new File(
									"/group/project/vsm-nfs/phraseEmbeddings_new/embeddings.ser")));
			out.writeObject(totalEmbeddingsMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static HashMap<String, DenseVector> readObject(File file) {
		ObjectInput in = null;
		HashMap<String, DenseVector> map = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			map = (HashMap<String, DenseVector>) in.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	private static File[] getEmbeedingsFiles() {
		File[] files = new File(embeddingsMapFilesPath)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						// TODO Auto-generated method stub
						return !pathname.isHidden();
					}
				});
		return files;
	}
}
