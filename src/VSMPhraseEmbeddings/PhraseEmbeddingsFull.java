package VSMPhraseEmbeddings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMPhraseEmbeddings.EmbeddedVectorBean;
import VSMUtilityClasses.VSMUtil;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class PhraseEmbeddingsFull {

	private static Logger LOGGER;

	private static File[] topLevelWordFolders;

	private static Vector phraseEmbedding;

	private static Vector[] embeddedFeatureVectors;

	private static File[] embeddedChunkVectors;

	private static File[] chunks;

	private static PhraseEmbeddingBean vecBean;

	private static HashMap<String, DenseVector> embeddingsMap;

	private static File[] phraseDirectories;

	private static Vector npoutSem;

	private static Vector npinSem;

	private static Vector npoutSyn;

	private static Vector npinSyn;

	private static String folderName;

	static {
		LOGGER = VSMLogger.setup(PhraseEmbeddingsFull.class.getName());
		embeddingsMap = new LinkedHashMap<>();
	}

	public static void main(String[] args) {

		System.out.println("NEW");

		folderName = getFolderName(args);
		System.out.println("++FOLDER NAME++" + folderName);

		topLevelWordFolders = getTopLevelWordFolders(VSMContant.PHRASE_VECS_EMBEDDED_CHUNKS);
		System.out.println(topLevelWordFolders);

		sortByNumber(topLevelWordFolders);

		int i = 0;
		for (File topLevelFolder : topLevelWordFolders) {
			if (topLevelFolder.getName().equalsIgnoreCase(folderName)) {

				getPhraseDirectories(topLevelFolder);
				System.out.println(phraseDirectories.length);

				for (File phraseDirectory : phraseDirectories) {

					phraseEmbedding = new DenseVector(2560);

					embeddedFeatureVectors = new DenseVector[4];

					setup();

					chunks = VSMUtil.getWordChunks(phraseDirectory, LOGGER);

					for (File chunk : chunks) {

						embeddedChunkVectors = VSMUtil.getEmbeddedChunkVecs(
								chunk, LOGGER);

						System.out.println("++++Number of Vectors+++ "
								+ embeddedChunkVectors.length);

						String chunkName = chunk.getName();

						if (embeddedChunkVectors != null
								&& chunkName.equalsIgnoreCase("NP")) {

							switch (chunkName) {

							case "NP":
								embedNP();
								break;

							default:
								break;

							}

						}
					}

					formPhraseEmbedding();

					serializePhraseEmbedding(phraseDirectory);
				}

			}
		}

		serializeEmbeddingsMap();

	}

	private static String getFolderName(String[] args) {
		if (args.length > 0) {
			return args[0];
		} else {
			System.out.println("PLEASE GIVE A FLOLDER NAME");
			System.exit(-1);
		}

		return null;
	}

	private static void embedNP() {

		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean.getPhiEmbedded() != null)
				npinSyn = npinSyn.add(vectorBean.getPhiEmbedded());

			if (vectorBean.getPsiEmbedded() != null)
				npoutSyn = npoutSyn.add(vectorBean.getPsiEmbedded());

			if (vectorBean.getPhiSem() != null)
				npinSem = npinSem.add(vectorBean.getPhiSem());

			if (vectorBean.getPsiSem() != null)
				npoutSem = npoutSem.add(vectorBean.getPsiSem());

		}

		npinSyn = npinSyn.scale((double) 1 / count);
		npoutSyn = npoutSyn.scale((double) 1 / count);

		npinSem = npinSem.scale((double) 1 / count);
		npoutSem = npoutSem.scale((double) 1 / count);

	}

	private static void serializeEmbeddingsMap() {
		ObjectOutput embeddings = null;
		try {
			embeddings = new ObjectOutputStream(new FileOutputStream(
					"/group/project/vsm-nfs/PhraseEmbeddingsNPNew/phraseEmbeddings+"
							+ folderName + ".ser"));
			embeddings.writeObject(embeddingsMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (embeddings != null) {
					embeddings.flush();
					embeddings.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void getPhraseDirectories(File topLevelFolder) {
		phraseDirectories = topLevelFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return !pathname.isHidden();
			}
		});

	}

	private static File[] getTopLevelWordFolders(String wordVecsEmbeddedChunks) {
		File[] topLevelFolders = new File(wordVecsEmbeddedChunks)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return !pathname.isHidden();
					}
				});
		return topLevelFolders;
	}

	private static void serializePhraseEmbedding(File phraseDirectory) {

		vecBean = new PhraseEmbeddingBean();

		vecBean.setPhraseEmbedding((DenseVector) phraseEmbedding);

		System.out.println("ENTERING THE PHRASE IN THE MAP"
				+ phraseDirectory.getName());
		// System.out.println(phraseEmbedding);
		embeddingsMap.put(phraseDirectory.getName(),
				(DenseVector) phraseEmbedding);

		// serializePhraseVector(vecBean, phraseDirectory);

	}

	private static void formPhraseEmbedding() {

		int i = 0;
		for (Vector vec : embeddedFeatureVectors) {
			i = i + vec.size();

		}
		System.out.println(i);
		int startIdx = 0;
		Iterator<VectorEntry> iterator = null;
		for (Vector vector : embeddedFeatureVectors) {

			iterator = vector.iterator();
			while (iterator.hasNext()) {
				VectorEntry e = iterator.next();
				int idx = e.index() + startIdx;
				double val = e.get();
				phraseEmbedding.add(idx, val);
			}

			startIdx = startIdx + vector.size();
		}

		// System.out.println(phraseEmbedding);

	}

	private static void setup() {

		npinSyn = new DenseVector(640);
		npoutSyn = new DenseVector(640);
		npinSem = new DenseVector(640);
		npoutSem = new DenseVector(640);

		embeddedFeatureVectors[0] = npinSyn;
		embeddedFeatureVectors[1] = npoutSyn;
		embeddedFeatureVectors[2] = npinSem;
		embeddedFeatureVectors[3] = npoutSem;

	}

	public static void serializePhraseVector(PhraseEmbeddingBean vecBean,
			File phraseDirectory) {

		File file = new File(phraseDirectory.getParent() + "_embed");

		if (!file.exists()) {
			file.mkdir();
		}

		String filename = file.getAbsolutePath() + "/"
				+ phraseDirectory.getName() + "/embedding.ser";

		File wordVecFile = new File(filename);
		if (wordVecFile.exists()) {
			System.out.println("+++++Word Embedding Already Exists");
		} else {
			wordVecFile.getParentFile().mkdir();
		}

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename, false);
			out = new ObjectOutputStream(fos);
			out.writeObject(vecBean);
			System.out.println("**serialized at** " + filename);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				out.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static EmbeddedVectorBean readFeatureVectorBeanEmbedded(
			String fileName) {

		EmbeddedVectorBean vectorBean = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			vectorBean = (EmbeddedVectorBean) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		return vectorBean;
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
