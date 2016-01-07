package VSMSICKEmbeddedFeatureVecsSyntactic;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.SparseVector;
import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSerializeFeatureVectorBeanEmbed;
import VSMUtilityClasses.VSMUtil;

/**
 * This class forms the lower dimensional syntactic embedding of the high
 * dimensional binary inside and outside feature vector
 * 
 * @author sameerkhurana10
 *
 */

public class FeatureVectorsEmbeddings {

	private final static Logger LOGGER;

	private static String nonTerminal;

	private static File[] directories;

	private static Object[] matricesSyn;

	private static File[] chunks;

	private static File chunkDirectory;

	private static File[] binaryVectors;

	private static VSMFeatureVectorBean vectorBean;

	private static Vector psiSynEmbedded;

	private static Vector phiSynEmbedded;

	private static DenseMatrix YTSyn;

	private static DenseMatrix ZTSyn;

	private static SparseVector phiSyn;

	private static VSMFeatureVectorBeanEmbedded vectorBeanEmbedded;

	private static SparseVector psiSyn;

	static {

		LOGGER = VSMLogger.setup(FeatureVectorsEmbeddings.class.getName());

	}

	public static void main(String... args) throws ClassNotFoundException {

		nonTerminal = VSMUtil.getNonTerminal(args);

		LOGGER.info("Log File Formed Successfully for " + nonTerminal);

		System.out.println("***Getting the Syntactic Projection Matrices***");

		matricesSyn = VSMUtil.getSyntacticProjectionMatricesTransposed(
				nonTerminal, LOGGER);

		directories = VSMUtil
				.getSICKSentenceDirectories(VSMContant.SICK_BINARY_SENT_VECS);

		YTSyn = (DenseMatrix) matricesSyn[0];
		ZTSyn = (DenseMatrix) matricesSyn[1];

		if (directories != null) {
			sortByNumber(directories);
		} else {
			LOGGER.log(Level.SEVERE,
					"Could not get the SICK Sentence Directories");
			System.exit(-1);
		}

		for (File sentenceDirectory : directories) {

			System.out.println("**Inside The Sentence Directory***\n"
					+ sentenceDirectory.getAbsolutePath());

			chunks = VSMUtil.getSentenceChunks(sentenceDirectory, LOGGER);

			if (chunks != null) {

				innerloop: for (File chunk : chunks) {
					if (chunk.getName().equalsIgnoreCase(
							nonTerminal.toUpperCase())) {
						chunkDirectory = chunk;
						break innerloop;
					} else {
						continue innerloop;
					}

				}

				System.out.println("***Got the Desired Chunk directory** "
						+ chunkDirectory);

				if (chunkDirectory != null) {

					binaryVectors = VSMUtil.getBinaryChunkVectors(
							chunkDirectory, LOGGER);

					if (binaryVectors != null) {

						System.out
								.println("**Forming Binary Vector Embeddings***");

						for (File vector : binaryVectors) {

							vectorBean = VSMReadSerialObject
									.readFeatureVectorBean(vector
											.getAbsolutePath());

							phiSyn = vectorBean.getPhi();
							psiSyn = vectorBean.getPsi();

							if (phiSyn != null) {
								phiSynEmbedded = VSMUtil.getPhiSynEmbedded(
										phiSyn, YTSyn);
							}

							if (psiSyn != null) {
								psiSynEmbedded = VSMUtil.getPsiSynEmbedded(
										psiSyn, ZTSyn);
							}

							vectorBeanEmbedded = new VSMFeatureVectorBeanEmbedded();
							vectorBeanEmbedded
									.setPhiSynEmbedded((DenseVector) phiSynEmbedded);

							vectorBeanEmbedded
									.setPsiSynEmbedded((DenseVector) psiSynEmbedded);

							freeUpMemory();

							VSMSerializeFeatureVectorBeanEmbed
									.serializeVectorBeanSyntacticEmbedding(
											vectorBeanEmbedded,
											sentenceDirectory.getName(),
											vector.getName());

						}
					}

				} else {
					LOGGER.info(nonTerminal
							+ " Chunk does not exist for this sentence");
				}
			}
		}

	}

	/*
	 * Freeing up the memory
	 */
	private static void freeUpMemory() {

		psiSynEmbedded = null;
		phiSynEmbedded = null;

		System.gc();

	}

	/**
	 * Overriding the Comaprator
	 * 
	 * @param files
	 */
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
