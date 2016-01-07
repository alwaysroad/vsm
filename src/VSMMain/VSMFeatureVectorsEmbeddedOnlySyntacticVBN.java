package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

import cern.clhep.PhysicalConstants;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;
import Jama.Matrix;
import VSMConstants.VSMContant;
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

public class VSMFeatureVectorsEmbeddedOnlySyntacticVBN {

	public static void main(String... args) throws ClassNotFoundException {

		PrintWriter logWriter = null;

		String directPath = VSMContant.SICK_BINARY_SENT_VECS;

		Object[] matricesSyn = null;
		// Object[] matricesSem = null;

		/*
		 * Getting the sentence directories
		 */
		File[] directories = new File(directPath).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				return !file.isHidden();
			}
		});

		sortByNumber(directories);

		/*
		 * Iterating over the sentence directories, the directories that contain
		 * chunks of feature vectors
		 */
		int count = 0;
		for (File directory : directories) {

			count++;

			/*
			 * Another bunch of directories, corresponding to each node that
			 * appears in the sentence
			 */
			File[] chunks = directory.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return !file.isHidden();
				}
			});

			/*
			 * Loop over the chunks now
			 */
			innerloop: for (File chunk : chunks) {

				if (chunk.getName().equalsIgnoreCase("VBN")) {

					/*
					 * Getting the lower dimensional embedding for the inside
					 * feature vector
					 */
					System.out
							.println("***Getting the projection matrices For***"
									+ chunk.getName());
					/*
					 * The syntactic projection matrices
					 */
					matricesSyn = VSMUtil.deserializeCCAVariantsRun(chunk
							.getName());

					/*
					 * Semantic projection matrices
					 */
					// matricesSem = VSMUtil.deserializeCCAVariantsRunSem(chunk
					// .getName());

					if (matricesSyn != null) {
						System.out.println("***Got the matrices***");
					} else {
						System.out
								.println("***Projections do not exist for this Chunk and hence ignore***");

					}
					int vectorCount = 0;
					/*
					 * The vectors corresponding to that chunk, syntactic and
					 * semantic
					 */
					File[] vectors = chunk.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file) {
							return !file.isHidden();
						}
					});
					System.out.println("***Directory***" + directory.getName());
					System.out.println("Chunk:***" + chunk.getName());
					System.out.println("***vectors**" + vectors);

					/*
					 * A very annoying fact about the list files is that it can
					 * sometime return null even if the directory has files,
					 * because maybe too many files are open.
					 */
					if (vectors != null) {
						for (File vector : vectors) {

							vectorCount++;
							/*
							 * Getting the vector bean
							 */
							VSMFeatureVectorBean vectorBean = VSMReadSerialObject
									.readFeatureVectorBean(vector
											.getAbsolutePath());

							Vector psiSynEmbedded = null;
							Vector phiSynEmbedded = null;

							// Vector psiSemEmbedded = null;
							// Vector phiSemEmbedded = null;

							VSMFeatureVectorBeanEmbedded vectorBeanEmbedded = new VSMFeatureVectorBeanEmbedded();
							/*
							 * Inside Projection Matrix
							 */
							if (matricesSyn != null) {
								if (matricesSyn[0] != null) {
									Matrix Ysyn = (Matrix) matricesSyn[0];
									// Matrix Ysem = (Matrix) matricesSem[0];

									/*
									 * Dense Matrix that holds YT
									 */
									DenseMatrix YTSyn = new DenseMatrix(
											Ysyn.getColumnDimension(),
											Ysyn.getRowDimension());

									// DenseMatrix YTSem = new DenseMatrix(
									// Ysem.getColumnDimension(),
									// Ysem.getRowDimension());

									/*
									 * Getting the MTJ Matrix
									 */
									DenseMatrix YMTJSyn = VSMUtil
											.createDenseMatrixMTJ(Ysyn);

									// DenseMatrix YMTJSem = VSMUtil
									// .createDenseMatrixMTJ(Ysem);

									Ysyn = null;
									// Ysem = null;

									/*
									 * Transform
									 */
									YMTJSyn.transpose(YTSyn);
									// YMTJSem.transpose(YTSem);

									YMTJSyn = null;

									/*
									 * Getting the inside feature vector
									 */
									SparseVector phiSyn = vectorBean.getPhi();
									// SparseVector phiSem =
									// vectorBean.getPhiSem();
									Vector phiDenseSyn = null;
									// Vector phiDenseSem = null;

									/*
									 * The vectors can be null and hence a null
									 * check, remember we are storing the null
									 * vectors as well
									 */
									if (phiSyn != null) {
										phiDenseSyn = new DenseVector(
												phiSyn.size());
										java.util.Iterator<VectorEntry> sparseVecItr = phiSyn
												.iterator();
										/*
										 * Iterating over the sparse vector
										 * entries
										 */
										while (sparseVecItr.hasNext()) {
											VectorEntry e = sparseVecItr.next();

											/*
											 * Getting the sparse vector index
											 * and values
											 */
											int idx = e.index();
											double val = e.get();

											/*
											 * Forming the dense inside feature
											 * vector
											 */
											phiDenseSyn.add(idx, val);
										}

										phiSynEmbedded = new DenseVector(
												YTSyn.numRows());
										YTSyn.mult(phiDenseSyn, phiSynEmbedded);

										phiSynEmbedded = VSMUtil
												.normalizeVec(phiSynEmbedded);

									}

									/*
									 * Serializing the lower dimensional vector
									 * embedding
									 */
									vectorBeanEmbedded
											.setPhi((DenseVector) phiSynEmbedded);
									// vectorBeanEmbedded
									// .setPhiSem((DenseVector) phiSemEmbedded);

									phiSynEmbedded = null;
									// phiSemEmbedded = null;

								}

								if (matricesSyn[1] != null) {
									/*
									 * Inside Projection Matrix
									 */
									Matrix ZSyn = (Matrix) matricesSyn[1];
									// Matrix ZSem = (Matrix) matricesSem[1];

									/*
									 * Dense Matrix that holds YT
									 */
									DenseMatrix ZTSyn = new DenseMatrix(
											ZSyn.getColumnDimension(),
											ZSyn.getRowDimension());
									// DenseMatrix ZTSem = new DenseMatrix(
									// ZSem.getColumnDimension(),
									// ZSem.getRowDimension());

									/*
									 * Getting the MTJ Matrix
									 */
									DenseMatrix ZMTJSyn = VSMUtil
											.createDenseMatrixMTJ(ZSyn);
									// DenseMatrix ZMTJSem = VSMUtil
									// .createDenseMatrixMTJ(ZSem);

									ZSyn = null;
									// ZSem = null;

									/*
									 * Transform
									 */
									ZMTJSyn.transpose(ZTSyn);
									// ZMTJSem.transpose(ZTSem);
									ZMTJSyn = null;
									// ZMTJSem = null;

									/*
									 * Getting the inside feature vector
									 */
									SparseVector psiSyn = vectorBean.getPsi();
									// SparseVector psiSem =
									// vectorBean.getPsiSem();

									/*
									 * Converting the sparse vector into dense
									 * vector
									 */
									Vector psiDenseSyn = null;
									// Vector psiDenseSem = null;

									if (psiSyn != null) {
										psiDenseSyn = new DenseVector(
												psiSyn.size());

										/*
										 * Iterator over the sparse vector MTJ
										 */
										java.util.Iterator<VectorEntry> sparseVecItrOut = psiSyn
												.iterator();

										/*
										 * Iterating over the sparse vector
										 * entries
										 */
										while (sparseVecItrOut.hasNext()) {
											VectorEntry e = sparseVecItrOut
													.next();

											/*
											 * Getting the sparse vector index
											 * and values
											 */
											int idx = e.index();
											double val = e.get();

											/*
											 * Forming the dense inside feature
											 * vector
											 */
											psiDenseSyn.add(idx, val);

										}

										/*
										 * Multiply the matrix and the vector,
										 * to get the continuous representation
										 */
										psiSynEmbedded = new DenseVector(
												ZTSyn.numRows());
										ZTSyn.mult(psiDenseSyn, psiSynEmbedded);

										psiSynEmbedded = VSMUtil
												.normalizeVec(psiSynEmbedded);
									}

								}
							}
							/*
							 * Serializing the continuous rep the semantic and
							 * the syntactic
							 */

							vectorBeanEmbedded
									.setPsi((DenseVector) psiSynEmbedded);
							// vectorBeanEmbedded.setPsiSem((DenseVector)
							// psiSemEmbedded);
							vectorBeanEmbedded.setSyntaxTree(vectorBean
									.getSyntaxTree());
							vectorBeanEmbedded.setInsideTree(vectorBean
									.getInsideTree());
							vectorBeanEmbedded.setLabel(chunk.getName());

							/*
							 * Taking the average, to get the average continuous
							 * representation
							 */
							// continousRepAvg.add(continuousRep);

							psiSynEmbedded = null;
							// psiSemEmbedded = null;
							System.gc();

							/*
							 * Serialize the syntactic embedded feature vector
							 */
							VSMSerializeFeatureVectorBeanEmbed
									.serializeVectorBeanSyntacticEmbedding(
											vectorBeanEmbedded,
											directory.getName(),
											vector.getName());

							/*
							 * Dividing by the number of vectors of the same
							 * type
							 */
							// continousRepAvg.scale(((double) 1 / (double)
							// vectorCount));
							/*
							 * Serialize the continuous rep average TODO and
							 * similarly form the continuous rep for the outside
							 * feature vectors
							 */

						}

						matricesSyn = null;
						// matricesSem = null;

					} else {

						/*
						 * We need to log this in a file so that we can handle
						 * this later
						 */
						try {
							logWriter = new PrintWriter(VSMContant.VBN_LOG);
							logWriter.println("***Directory Path***"
									+ chunk.getPath());
						} catch (FileNotFoundException e) {
							System.out
									.println("***Exception while writing in the log file**");
							e.printStackTrace();
						} finally {
							logWriter.flush();
							logWriter.close();
						}

						System.out.println("***The vectors are null***"
								+ directory.getName() + chunk.getName());
						/*
						 * Writing the information to a file
						 */
					}
				}

			}
		}

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
