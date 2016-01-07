package VSMSICKEmbeddedFeatureVecs;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uib.cipr.matrix.AbstractMatrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;
import Jama.Matrix;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialObject;
import VSMUtilityClasses.VSMUtil;

/**
 * This class forms the lower dimensional syntactic embedding of the high
 * dimensional binary inside and outside feature vector
 * 
 * @author sameerkhurana10
 *
 */

public class EmbeddedFeatureVectorsBLLIP {

	private static Logger LOGGER;

	private static String nonTerminal;

	private static File[] sentenceChunks;

	private static File[] chunkBinaryVectors;

	private static SerializeBllipEmbeddedFeatureVecs bean;

	private static DenseMatrix YTSyn;

	private static DenseMatrix YTSem;

	private static DenseMatrix ZTSyn;

	private static DenseMatrix ZTSem;

	private static File chunkDirectory;

	private static SparseVector phiSyn;

	private static SparseVector phiSem;

	private static SparseVector psiSyn;

	private static SparseVector psiSem;

	private static Vector psiSynEmbedded;
	private static Vector phiSynEmbedded;

	private static Vector psiSemEmbedded;
	private static Vector phiSemEmbedded;

	private static VSMFeatureVectorBean vectorBean;

	private static EmbeddedVectorBean vectorBeanEmbedded;

	private static ArrayList<String> sentenceDirectPaths;

	static {
		sentenceDirectPaths = new ArrayList<String>();
		bean = new SerializeBllipEmbeddedFeatureVecs();
	}

	public static void main(String... args) throws ClassNotFoundException {

		System.out.println("+++++");
		nonTerminal = VSMUtil.getNonTerminal(args);

		LOGGER = VSMLogger.setup(EmbeddedFeatureVectorsBLLIP.class.getName()
				+ "." + nonTerminal);

		LOGGER.info("Log File created Successfully for Non Terminal"
				+ nonTerminal);

		LOGGER.info("Getting the sentence directories");
		getBLLIPSentenceDirectories(VSMContant.BINARY_SENTENCE_VECS_BLLIP);

		if (sentenceDirectPaths == null) {
			LOGGER.log(
					Level.SEVERE,
					"Sentence Directories Cannot be Fetched Something Wrong with the File system again: Path Trying to read: "
							+ VSMContant.SICK_SENTENCES_WITH_CHUNKS);
			System.exit(-1);
		}

		System.out.println("Getting the 4 Projection Matrices for the Node "
				+ nonTerminal);

		formTransposedProjectionMatrices();

		LOGGER.info("Formed the Transposed projection matrices");

		int sentenceCount = 0;
		for (String sentenceDirectory : sentenceDirectPaths) {
			sentenceCount++;
			File sentenceDirectoryF = new File(sentenceDirectory);
			sentenceChunks = VSMUtil.getSentenceChunks(sentenceDirectoryF,
					LOGGER);

			if (sentenceChunks != null) {

				innerloop: for (File chunk : sentenceChunks) {
					if (chunk.getName().equalsIgnoreCase(
							nonTerminal.toUpperCase())) {
						chunkDirectory = chunk;
						break innerloop;
					} else {
						continue innerloop;
					}

				}

				if (chunkDirectory != null) {

					chunkBinaryVectors = VSMUtil.getBinaryChunkVectors(
							chunkDirectory, LOGGER);

					if (chunkBinaryVectors != null) {

						System.out
								.println("***Forming the Embeddings for the Binary Vectors for the Chunk: "
										+ chunkDirectory.getName());

						int nodeCount = 0;
						for (File vector : chunkBinaryVectors) {
							nodeCount++;
							vectorBean = VSMReadSerialObject
									.readFeatureVectorBean(vector
											.getAbsolutePath());

							phiSyn = vectorBean.getPhi();
							phiSem = vectorBean.getPhiSem();
							psiSyn = vectorBean.getPsi();
							psiSem = vectorBean.getPsiSem();

							if (phiSyn != null) {
								phiSynEmbedded = VSMUtil.getPhiSynEmbedded(
										phiSyn, YTSyn);
							}

							if (phiSem != null) {
								phiSemEmbedded = VSMUtil.getPhiSemEmbedded(
										phiSem, YTSem);
							}

							if (psiSyn != null) {
								psiSynEmbedded = VSMUtil.getPsiSynEmbedded(
										psiSyn, ZTSyn);
							}

							if (psiSem != null) {
								psiSemEmbedded = VSMUtil.getPsiSemEmbedded(
										psiSem, ZTSem);
							}

							vectorBeanEmbedded = new EmbeddedVectorBean();

							vectorBeanEmbedded
									.setPsiSynEmbedded((DenseVector) psiSynEmbedded);

							vectorBeanEmbedded
									.setPhiSynEmbedded((DenseVector) phiSynEmbedded);

							vectorBeanEmbedded
									.setPsiSemEmbedded((DenseVector) psiSemEmbedded);

							vectorBeanEmbedded
									.setPhiSemEmbedded((DenseVector) phiSemEmbedded);

							vectorBeanEmbedded.setLabel(chunkDirectory
									.getName());

							freeUpMemory();

							System.out
									.println("Serializing the 4 embedded Vectors");

							bean.serializeVectorBeanBLLIP(vectorBeanEmbedded,
									sentenceCount,
									sentenceDirectoryF.getName(), nodeCount);

						}

					}
				} else {
					LOGGER.info(nonTerminal
							+ " Chunk does not exist for this sentence");
				}
			}
		}
	}

	private static void formTransposedProjectionMatrices() {

		Matrix[] semProjections = deserializeProjectionsSem();
		LOGGER.info("++Got the Semantic Projections++" + semProjections.length);

		Matrix ZSem = semProjections[0];
		Matrix YSem = semProjections[1];
		ZTSem = new DenseMatrix(ZSem.getColumnDimension(),
				ZSem.getRowDimension());
		YTSem = new DenseMatrix(YSem.getColumnDimension(),
				YSem.getRowDimension());

		tranpose("Sem", VSMUtil.createDenseMatrixMTJ(ZSem),
				VSMUtil.createDenseMatrixMTJ(YSem));

		ZSem = null;
		YSem = null;
		semProjections = null;
		System.gc();

		Matrix[] synProjections = deserializeProjectionsSyn();
		LOGGER.info("++++Got the Syntactic Projections++++"
				+ synProjections.length);

		Matrix ZSyn = synProjections[0];
		Matrix YSyn = synProjections[1];
		ZTSyn = new DenseMatrix(ZSyn.getColumnDimension(),
				ZSyn.getRowDimension());
		YTSyn = new DenseMatrix(YSyn.getColumnDimension(),
				YSyn.getRowDimension());

		tranpose("Syn", VSMUtil.createDenseMatrixMTJ(ZSyn),
				VSMUtil.createDenseMatrixMTJ(YSyn));

		synProjections = null;
		ZSyn = null;
		YSyn = null;
		System.gc();
	}

	private static void tranpose(String pref, DenseMatrix ZMTJ, DenseMatrix YMTJ) {
		if (pref.equalsIgnoreCase("Sem")) {
			ZMTJ.transpose(ZTSem);
			YMTJ.transpose(YTSem);
		} else if (pref.equalsIgnoreCase("Syn")) {
			ZMTJ.transpose(ZTSyn);
			YMTJ.transpose(YTSyn);
		}

	}

	/*
	 * Freeing up the memory
	 */
	private static void freeUpMemory() {

		psiSynEmbedded = null;
		phiSynEmbedded = null;
		psiSemEmbedded = null;
		phiSemEmbedded = null;

		System.gc();

	}

	private static Matrix[] deserializeProjectionsSyn() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/"
				+ nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssyn.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(
						new FileInputStream(fileName));

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out
						.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projections.ser file: "
								+ ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projection matrices object"
								+ e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***"
							+ nonTerminal);
			return null;
		}

	}

	private static Matrix[] deserializeProjectionsSem() {

		String fileDirPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/projectionMatrices/"
				+ nonTerminal;

		File fileDir = new File(fileDirPath);

		if (fileDir.exists()) {
			String fileName = fileDir.getAbsolutePath() + "/projectionssem.ser";

			Matrix[] matrices = new Matrix[2];

			try {

				@SuppressWarnings("resource")
				ObjectInput projectionsPath = new ObjectInputStream(
						new FileInputStream(fileName));

				matrices = (Matrix[]) projectionsPath.readObject();

				System.out
						.println("=======De-serialized the CCA Variant Run=======");
			} catch (IOException ioe) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projections.ser file: "
								+ ioe);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE,
						"Exception while reading the projection matrices object"
								+ e);
				e.printStackTrace();
			}

			return matrices;
		} else {

			System.out
					.println("***There is no such non-terminal for which we can get the projections***"
							+ nonTerminal);
			return null;
		}

	}

	public static void getBLLIPSentenceDirectories(
			String binarySentenceVecsBllip) {

		File[] topLevel = getTopLevelFolders(binarySentenceVecsBllip);

		// get the sentence directory paths
		for (File folder : topLevel) {
			File[] sentenceDirecs = getSentenceDirectories(folder);

			addSentenceDirecsToList(sentenceDirecs);
		}

	}

	private static void addSentenceDirecsToList(File[] sentenceDirecs) {
		for (File sentenceDirec : sentenceDirecs) {
			sentenceDirectPaths.add(sentenceDirec.getAbsolutePath());
		}

	}

	private static File[] getSentenceDirectories(File folder) {
		File[] sentenceDirectories = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				return !pathname.isHidden();
			}
		});
		return sentenceDirectories;
	}

	private static File[] getTopLevelFolders(String binarySentenceVecsBllip) {
		// get the folders in which sentence directories are present
		File[] topLevel = new File(binarySentenceVecsBllip)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {

						return !pathname.isHidden();
					}
				});
		return topLevel;
	}

}
