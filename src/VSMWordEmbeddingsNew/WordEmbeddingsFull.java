package VSMWordEmbeddingsNew;

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
import VSMWordEmbeddingsNew.EmbeddedVectorBean;
import VSMUtilityClasses.VSMUtil;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class WordEmbeddingsFull {

	private static Logger LOGGER;

	private static File[] topLevelWordFolders;

	private static Vector wordEmbedding;

	// private static int nonTerminals;

	private static Vector[] embeddedFeatureVectors;

	private static File[] embeddedChunkVectors;

	private static File[] chunks;

	private static WordEmbeddingBean vecBean;

	private static HashMap<String, DenseVector> embeddingsMap;

	private static Vector ccinSyn;
	private static Vector ccoutSyn;
	private static Vector ccinSem;
	private static Vector ccoutSem;

	private static Vector nninSyn;
	private static Vector nnoutSyn;
	private static Vector nninSem;
	private static Vector nnoutSem;

	private static Vector ininSyn;
	private static Vector inoutSyn;
	private static Vector ininSem;
	private static Vector inoutSem;

	private static Vector jjinSyn;
	private static Vector jjoutSyn;
	private static Vector jjinSem;
	private static Vector jjoutSem;

	private static Vector nnpinSyn;
	private static Vector nnpoutSyn;
	private static Vector nnpinSem;
	private static Vector nnpoutSem;

	private static Vector nnsinSyn;
	private static Vector nnsoutSyn;
	private static Vector nnsinSem;
	private static Vector nnsoutSem;

	private static Vector prpinSyn;
	private static Vector prpoutSyn;
	private static Vector prpinSem;
	private static Vector prpoutSem;

	private static Vector vbpinSyn;
	private static Vector vbpoutSyn;
	private static Vector vbpinSem;
	private static Vector vbpoutSem;

	private static Vector vbninSyn;
	private static Vector vbnoutSyn;
	private static Vector vbninSem;
	private static Vector vbnoutSem;

	private static Vector vbginSyn;
	private static Vector vbgoutSyn;
	private static Vector vbginSem;
	private static Vector vbgoutSem;

	private static Vector rbinSyn;
	private static Vector rboutSyn;
	private static Vector rbinSem;
	private static Vector rboutSem;

	private static Vector dtinSyn;
	private static Vector dtoutSyn;
	private static Vector dtinSem;
	private static Vector dtoutSem;

	private static DenseVector vbdinSyn;
	private static DenseVector vbdoutSyn;
	private static DenseVector vbdinSem;
	private static DenseVector vbdoutSem;

	private static File[] wordDirectories;

	private static String folderName;

	static {
		embeddingsMap = new LinkedHashMap<>();
	}

	public static void main(String[] args) {

		folderName = getFolderName(args);

		LOGGER = VSMLogger.setup(WordEmbeddingsFull.class.getName() + "_"
				+ folderName);

		System.out.println("++FOLDER NAME++" + folderName);

		topLevelWordFolders = getTopLevelWordFolders(VSMContant.WORD_VECS_EMBEDDED_CHUNKS);

		System.out.println("New!!!");

		sortByNumber(topLevelWordFolders);

		// formDirectoryStructure();

		for (File topLevelFolder : topLevelWordFolders) {

			if (topLevelFolder.getName().equalsIgnoreCase(folderName)) {

				getWordDirectories(topLevelFolder);

				for (File wordDirectory : wordDirectories) {

					wordEmbedding = new DenseVector(24000);

					embeddedFeatureVectors = new DenseVector[52];

					setup();

					chunks = VSMUtil.getWordChunks(wordDirectory, LOGGER);

					for (File chunk : chunks) {

						embeddedChunkVectors = VSMUtil.getEmbeddedChunkVecs(
								chunk, LOGGER);

						System.out.println("++++Number of Vectors+++ "
								+ embeddedChunkVectors.length);

						String chunkName = chunk.getName();

						if (embeddedChunkVectors != null) {

							switch (chunkName) {

							case "CC":
								embedCC();

								break;

							case "NN":
								embedNN();

								break;

							case "IN":
								embedIN();
								break;

							case "JJ":
								embedJJ();
								break;

							case "NNP":
								embedNNP();
								break;

							case "NNS":
								embedNNS();
								break;

							case "PRP":
								embedPRP();
								break;

							case "RB":
								embedRB();
								break;

							case "VBG":
								embedVBG();
								break;

							case "VBP":
								embedVBP();
								break;

							case "VBN":
								embedVBN();
								break;

							default:
								break;

							}

						}
					}

					formWordEmbedding(wordDirectory);

					serializeWordEmbedding(wordDirectory);
				}

			}
		}

		serializeEmbeddingsMap();

	}

	private static int checkWordEmbeddedFolder(File wordDirectory) {
		File file = new File(wordDirectory.getParent() + "_embed");

		if (file.exists()) {

			String filename = file.getAbsolutePath() + "/"
					+ wordDirectory.getName() + "/embedding.ser";

			File wordVecFile = new File(filename);
			if (wordVecFile.exists()) {
				System.out
						.println("+++++Word Embedding Already Exists hence returning and not forming the vectors");
				return -1;
			}
		}
		return 0;

	}

	private static String getFolderName(String... args) {
		if (args.length > 0) {
			return args[0];
		} else {
			System.out.println("PLEASE GIVE A FLOLDER NAME");
			System.exit(-1);
		}

		return null;

	}

	private static void serializeEmbeddingsMap() {
		ObjectOutput embeddings = null;
		try {
			embeddings = new ObjectOutputStream(new FileOutputStream(
					"/group/project/vsm-nfs/WordEmbeddings1/WordEmbeddings/wordEmbeddings"
							+ folderName + ".ser", false));
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

	private static void getWordDirectories(File topLevelFolder) {
		wordDirectories = topLevelFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
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

	private static void serializeWordEmbedding(File wordDirectory) {

		vecBean = new WordEmbeddingBean();

		vecBean.setWordEmbedding((DenseVector) wordEmbedding);

		System.out.println("+++INSERTING THE WORD IN THE MAP++"
				+ wordDirectory.getName());
		embeddingsMap.put(wordDirectory.getName(), (DenseVector) wordEmbedding);

		// serializeWordVector(vecBean, wordDirectory);

	}

	private static void formWordEmbedding(File wordDirectory) {

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
				wordEmbedding.add(idx, val);
			}

			startIdx = startIdx + vector.size();
		}

	}

	private static void embedVBN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					vbninSyn = vbninSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					vbnoutSyn = vbnoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					vbninSem = vbninSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					vbnoutSem = vbnoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		vbninSyn = vbninSyn.scale((double) 1 / count);
		vbnoutSyn = vbnoutSyn.scale((double) 1 / count);

		vbninSem = vbninSem.scale((double) 1 / count);
		vbnoutSem = vbnoutSem.scale((double) 1 / count);

	}

	private static void embedVBP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					vbpinSyn = vbpinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					vbpoutSyn = vbpoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					vbpinSem = vbpinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					vbpoutSem = vbpoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		vbpinSyn = vbpinSyn.scale((double) 1 / count);
		vbpoutSyn = vbpoutSyn.scale((double) 1 / count);

		vbpinSem = vbpinSem.scale((double) 1 / count);
		vbpoutSem = vbpoutSem.scale((double) 1 / count);

	}

	private static void embedVBG() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					vbginSyn = vbginSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					vbgoutSyn = vbgoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					vbginSem = vbginSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					vbgoutSem = vbgoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		vbginSyn = vbginSyn.scale((double) 1 / count);
		vbgoutSyn = vbgoutSyn.scale((double) 1 / count);

		vbginSem = vbginSem.scale((double) 1 / count);
		vbgoutSem = vbgoutSem.scale((double) 1 / count);

	}

	private static void embedRB() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					rbinSyn = rbinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					rboutSyn = rboutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					rbinSem = rbinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					rboutSem = rboutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		rbinSyn = rbinSyn.scale((double) 1 / count);
		rboutSyn = rboutSyn.scale((double) 1 / count);

		rbinSem = rbinSem.scale((double) 1 / count);
		rboutSem = rboutSem.scale((double) 1 / count);

	}

	private static void embedPRP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					prpinSyn = prpinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					prpoutSyn = prpoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					prpinSem = prpinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					prpoutSem = prpoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		prpinSyn = prpinSyn.scale((double) 1 / count);
		prpoutSyn = prpoutSyn.scale((double) 1 / count);
		prpinSem = prpinSem.scale((double) 1 / count);
		prpoutSem = prpoutSem.scale((double) 1 / count);

	}

	private static void embedNNS() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					nnsinSyn = nnsinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					nnsoutSyn = nnsoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					nnsinSem = nnsinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					nnsoutSem = nnsoutSem.add(vectorBean.getPsiSem());
			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		nnsinSyn = nnsinSyn.scale((double) 1 / count);
		nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

		nnsinSem = nnsinSem.scale((double) 1 / count);
		nnsoutSem = nnsoutSem.scale((double) 1 / count);

	}

	private static void embedNNP() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					nnpinSyn = nnpinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					nnpoutSyn = nnpoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					nnpinSem = nnpinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					nnpoutSem = nnpoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		nnpinSyn = nnpinSyn.scale((double) 1 / count);
		nnpoutSyn = nnpoutSyn.scale((double) 1 / count);

		nnpinSem = nnpinSem.scale((double) 1 / count);
		nnpoutSem = nnpoutSem.scale((double) 1 / count);

	}

	private static void embedJJ() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					jjinSyn = jjinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					jjoutSyn = jjoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					jjinSem = jjinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					jjoutSem = jjoutSem.add(vectorBean.getPsiSem());
			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		jjinSyn = jjinSyn.scale((double) 1 / count);
		jjoutSyn = jjoutSyn.scale((double) 1 / count);

		jjinSem = jjinSem.scale((double) 1 / count);
		jjoutSem = jjoutSem.scale((double) 1 / count);

	}

	private static void embedIN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					ininSyn = ininSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					inoutSyn = inoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					ininSem = ininSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					inoutSem = inoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		ininSyn = ininSyn.scale((double) 1 / count);
		inoutSyn = inoutSyn.scale((double) 1 / count);
		ininSem = ininSem.scale((double) 1 / count);
		inoutSem = inoutSem.scale((double) 1 / count);

	}

	private static void embedNN() {
		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());
			if (vectorBean != null) {
				if (vectorBean.getPhiEmbedded() != null)
					nninSyn = nninSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					nnoutSyn = nnoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					nninSem = nninSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					nnoutSem = nnoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		nninSyn = nninSyn.scale((double) 1 / count);
		nnoutSyn = nnoutSyn.scale((double) 1 / count);
		nninSem = nninSem.scale((double) 1 / count);
		nnoutSem = nnoutSem.scale((double) 1 / count);

	}

	private static void embedCC() {

		int count = 0;
		for (File embeddeVec : embeddedChunkVectors) {
			count++;
			EmbeddedVectorBean vectorBean = readFeatureVectorBeanEmbedded(embeddeVec
					.getAbsolutePath());

			if (vectorBean != null) {

				if (vectorBean.getPhiEmbedded() != null)
					ccinSyn = ccinSyn.add(vectorBean.getPhiEmbedded());

				if (vectorBean.getPsiEmbedded() != null)
					ccoutSyn = ccoutSyn.add(vectorBean.getPsiEmbedded());

				if (vectorBean.getPhiSem() != null)
					ccinSem = ccinSem.add(vectorBean.getPhiSem());

				if (vectorBean.getPsiSem() != null)
					ccoutSem = ccoutSem.add(vectorBean.getPsiSem());

			} else {
				LOGGER.severe("++++VECTOR BEAN NULL FOR: "
						+ embeddeVec.getAbsolutePath());
			}
		}

		ccinSyn = ccinSyn.scale((double) 1 / count);
		ccoutSyn = ccoutSyn.scale((double) 1 / count);

		ccinSem = ccinSem.scale((double) 1 / count);
		ccoutSem = ccoutSem.scale((double) 1 / count);

	}

	private static void setup() {

		ccinSyn = new DenseVector(30);
		ccoutSyn = new DenseVector(30);
		ccinSem = new DenseVector(14);
		ccoutSem = new DenseVector(14);

		nninSyn = new DenseVector(640);
		nnoutSyn = new DenseVector(640);
		nninSem = new DenseVector(640);
		nnoutSem = new DenseVector(640);

		ininSyn = new DenseVector(96);
		inoutSyn = new DenseVector(96);
		ininSem = new DenseVector(37);
		inoutSem = new DenseVector(37);

		jjinSyn = new DenseVector(640);
		jjoutSyn = new DenseVector(640);
		jjinSem = new DenseVector(100);
		jjoutSem = new DenseVector(100);

		nnpinSyn = new DenseVector(640);
		nnpoutSyn = new DenseVector(640);
		nnpinSem = new DenseVector(640);
		nnpoutSem = new DenseVector(640);

		nnsinSyn = new DenseVector(640);
		nnsoutSyn = new DenseVector(640);
		nnsinSem = new DenseVector(640);
		nnsoutSem = new DenseVector(640);

		prpinSyn = new DenseVector(42);
		prpoutSyn = new DenseVector(42);
		prpinSem = new DenseVector(13);
		prpoutSem = new DenseVector(13);

		vbpinSyn = new DenseVector(640);
		vbpoutSyn = new DenseVector(640);
		vbpinSem = new DenseVector(640);
		vbpoutSem = new DenseVector(640);

		vbninSyn = new DenseVector(640);
		vbnoutSyn = new DenseVector(640);
		vbninSem = new DenseVector(640);
		vbnoutSem = new DenseVector(640);

		vbginSyn = new DenseVector(640);
		vbgoutSyn = new DenseVector(640);
		vbginSem = new DenseVector(640);
		vbgoutSem = new DenseVector(640);

		rbinSyn = new DenseVector(640);
		rboutSyn = new DenseVector(640);
		rbinSem = new DenseVector(640);
		rboutSem = new DenseVector(640);

		dtinSyn = new DenseVector(10);
		dtoutSyn = new DenseVector(10);
		dtinSem = new DenseVector(4);
		dtoutSem = new DenseVector(4);

		vbdinSyn = new DenseVector(640);
		vbdoutSyn = new DenseVector(640);
		vbdinSem = new DenseVector(640);
		vbdoutSem = new DenseVector(640);

		embeddedFeatureVectors[0] = ininSyn;
		embeddedFeatureVectors[1] = inoutSyn;
		embeddedFeatureVectors[2] = ininSem;
		embeddedFeatureVectors[3] = inoutSem;

		embeddedFeatureVectors[4] = nninSyn;
		embeddedFeatureVectors[5] = nnoutSyn;
		embeddedFeatureVectors[6] = nninSem;
		embeddedFeatureVectors[7] = nnoutSem;

		embeddedFeatureVectors[8] = ccinSyn;
		embeddedFeatureVectors[9] = ccoutSyn;
		embeddedFeatureVectors[10] = ccinSem;
		embeddedFeatureVectors[11] = ccoutSem;

		embeddedFeatureVectors[12] = jjinSyn;
		embeddedFeatureVectors[13] = jjoutSyn;
		embeddedFeatureVectors[14] = jjinSem;
		embeddedFeatureVectors[15] = jjoutSem;

		embeddedFeatureVectors[16] = nnpinSyn;
		embeddedFeatureVectors[17] = nnpoutSyn;
		embeddedFeatureVectors[18] = nnpinSem;
		embeddedFeatureVectors[19] = nnpoutSem;

		embeddedFeatureVectors[20] = nnsinSyn;
		embeddedFeatureVectors[21] = nnsoutSyn;
		embeddedFeatureVectors[22] = nnsinSem;
		embeddedFeatureVectors[23] = nnsoutSem;

		embeddedFeatureVectors[24] = prpinSyn;
		embeddedFeatureVectors[25] = prpoutSyn;
		embeddedFeatureVectors[26] = prpinSem;
		embeddedFeatureVectors[27] = prpoutSem;

		embeddedFeatureVectors[28] = vbpinSyn;
		embeddedFeatureVectors[29] = vbpoutSyn;
		embeddedFeatureVectors[30] = vbpinSem;
		embeddedFeatureVectors[31] = vbpoutSem;

		embeddedFeatureVectors[32] = rbinSyn;
		embeddedFeatureVectors[33] = rboutSyn;
		embeddedFeatureVectors[34] = rbinSem;
		embeddedFeatureVectors[35] = rboutSem;

		embeddedFeatureVectors[36] = vbginSyn;
		embeddedFeatureVectors[37] = vbgoutSyn;
		embeddedFeatureVectors[38] = vbginSem;
		embeddedFeatureVectors[39] = vbgoutSem;

		embeddedFeatureVectors[40] = vbninSyn;
		embeddedFeatureVectors[41] = vbnoutSyn;
		embeddedFeatureVectors[42] = vbninSem;
		embeddedFeatureVectors[43] = vbnoutSem;

		embeddedFeatureVectors[44] = dtinSyn;
		embeddedFeatureVectors[45] = dtoutSyn;
		embeddedFeatureVectors[46] = dtinSem;
		embeddedFeatureVectors[47] = dtoutSem;

		embeddedFeatureVectors[48] = vbdinSyn;
		embeddedFeatureVectors[49] = vbdoutSyn;
		embeddedFeatureVectors[50] = vbdinSem;
		embeddedFeatureVectors[51] = vbdoutSem;

	}

	public static void serializeWordVector(WordEmbeddingBean vecBean,
			File wordDirectory) {

		File file = new File("/group/project/vsm-nfs/WordFeatureVecsEmbedded1/"
				+ folderName + "_embed");

		if (!file.exists()) {
			file.mkdir();
		}

		String filename = file.getAbsolutePath() + "/"
				+ wordDirectory.getName() + "/embedding.ser";

		File wordVecFile = new File(filename);
		if (wordVecFile.exists()) {
			System.out.println("+++++Word Embedding Already Exists");
			return;
		} else {
			try {
				wordVecFile.getParentFile().mkdir();
				wordVecFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
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
		} catch (IOException ex) {
			LOGGER.severe("IO excpetion while reading++++ " + fileName);
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			LOGGER.severe("FNE excpeiton while reading++++ " + fileName);
			cnfe.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
				else
					LOGGER.severe("FIS NULL+++ for +++ " + fileName);
				if (in != null)
					in.close();
			} catch (IOException e) {
				LOGGER.severe("+++EXCEPTION WHILE CLOSING THE FILE" + fileName);
				e.printStackTrace();
			}
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
