package VSMFeatureVecsEmbeddedSICK;

import java.io.File;
import java.io.FileFilter;

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

public class VSMFeatureVectorsEmbeddedJJ {

	public static void main(String... args) throws ClassNotFoundException {

		String directPath = VSMContant.SICK_BINARY_SENT_VECS;

		Object[] matricesSyn = null;
		Object[] matricesSem = null;

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

		/*
		 * Iterating over the sentence directories, the directories that contain
		 * chunks of feature vectors
		 */
		for (File directory : directories) {

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

				if (chunk.getName().equalsIgnoreCase("JJ")) {

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
					matricesSem = VSMUtil.deserializeCCAVariantsRunSem(chunk
							.getName());

					if (matricesSyn != null && matricesSem != null) {
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

					for (File vector : vectors) {

						vectorCount++;
						/*
						 * Getting the vector bean
						 */
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(vector.getAbsolutePath());

						Vector psiSynEmbedded = null;
						Vector phiSynEmbedded = null;

						Vector psiSemEmbedded = null;
						Vector phiSemEmbedded = null;

						VSMFeatureVectorBeanEmbedded vectorBeanEmbedded = new VSMFeatureVectorBeanEmbedded();
						/*
						 * Inside Projection Matrix
						 */
						if (matricesSyn != null && matricesSem != null) {
							if (matricesSyn[0] != null
									&& matricesSem[0] != null) {
								Matrix Ysyn = (Matrix) matricesSyn[0];
								Matrix Ysem = (Matrix) matricesSem[0];

								/*
								 * Dense Matrix that holds YT
								 */
								DenseMatrix YTSyn = new DenseMatrix(
										Ysyn.getColumnDimension(),
										Ysyn.getRowDimension());

								DenseMatrix YTSem = new DenseMatrix(
										Ysem.getColumnDimension(),
										Ysem.getRowDimension());

								/*
								 * Getting the MTJ Matrix
								 */
								DenseMatrix YMTJSyn = VSMUtil
										.createDenseMatrixMTJ(Ysyn);

								DenseMatrix YMTJSem = VSMUtil
										.createDenseMatrixMTJ(Ysem);

								Ysyn = null;
								Ysem = null;

								/*
								 * Transform
								 */
								YMTJSyn.transpose(YTSyn);
								YMTJSem.transpose(YTSem);

								YMTJSyn = null;

								/*
								 * Getting the inside feature vector
								 */
								SparseVector phiSyn = vectorBean.getPhi();
								SparseVector phiSem = vectorBean.getPhiSem();
								Vector phiDenseSyn = null;
								Vector phiDenseSem = null;

								/*
								 * The vectors can be null and hence a null
								 * check, remember we are storing the null
								 * vectors as well
								 */
								if (phiSyn != null) {
									phiDenseSyn = new DenseVector(phiSyn.size());
									java.util.Iterator<VectorEntry> sparseVecItr = phiSyn
											.iterator();
									/*
									 * Iterating over the sparse vector entries
									 */
									while (sparseVecItr.hasNext()) {
										VectorEntry e = sparseVecItr.next();

										/*
										 * Getting the sparse vector index and
										 * values
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

								if (phiSem != null) {
									phiDenseSem = new DenseVector(phiSem.size());

									/*
									 * Iterator over the sparse vector MTJ
									 */

									/*
									 * Iterator over the sparse vector MTJ
									 */
									java.util.Iterator<VectorEntry> sparseVecSemItr = phiSem
											.iterator();

									/*
									 * Iterating over the sparse vector entries
									 */
									while (sparseVecSemItr.hasNext()) {
										VectorEntry e = sparseVecSemItr.next();

										/*
										 * Getting the sparse vector index and
										 * values
										 */
										int idx = e.index();
										double val = e.get();

										/*
										 * Forming the dense inside feature
										 * vector
										 */
										phiDenseSem.add(idx, val);
									}

									/*
									 * Multiply the matrix and the vector, to
									 * get the lower dimensional embedding
									 */

									phiSemEmbedded = new DenseVector(
											YTSem.numRows());
									YTSem.mult(phiDenseSem, phiSemEmbedded);

									phiSemEmbedded = VSMUtil
											.normalizeVec(phiSemEmbedded);

								}

								/*
								 * Serializing the lower dimensional vector
								 * embedding
								 */
								vectorBeanEmbedded
										.setPhi((DenseVector) phiSynEmbedded);
								vectorBeanEmbedded
										.setPhiSem((DenseVector) phiSemEmbedded);

								phiSynEmbedded = null;
								phiSemEmbedded = null;

							}

							if (matricesSyn[1] != null
									&& matricesSem[1] != null) {
								/*
								 * Inside Projection Matrix
								 */
								Matrix ZSyn = (Matrix) matricesSyn[1];
								Matrix ZSem = (Matrix) matricesSem[1];

								/*
								 * Dense Matrix that holds YT
								 */
								DenseMatrix ZTSyn = new DenseMatrix(
										ZSyn.getColumnDimension(),
										ZSyn.getRowDimension());
								DenseMatrix ZTSem = new DenseMatrix(
										ZSem.getColumnDimension(),
										ZSem.getRowDimension());

								/*
								 * Getting the MTJ Matrix
								 */
								DenseMatrix ZMTJSyn = VSMUtil
										.createDenseMatrixMTJ(ZSyn);
								DenseMatrix ZMTJSem = VSMUtil
										.createDenseMatrixMTJ(ZSem);

								ZSyn = null;
								ZSem = null;

								/*
								 * Transform
								 */
								ZMTJSyn.transpose(ZTSyn);
								ZMTJSem.transpose(ZTSem);
								ZMTJSyn = null;
								ZMTJSem = null;

								/*
								 * Getting the inside feature vector
								 */
								SparseVector psiSyn = vectorBean.getPsi();
								SparseVector psiSem = vectorBean.getPsiSem();

								/*
								 * Converting the sparse vector into dense
								 * vector
								 */
								Vector psiDenseSyn = null;
								Vector psiDenseSem = null;

								if (psiSyn != null) {
									psiDenseSyn = new DenseVector(psiSyn.size());

									/*
									 * Iterator over the sparse vector MTJ
									 */
									java.util.Iterator<VectorEntry> sparseVecItrOut = psiSyn
											.iterator();

									/*
									 * Iterating over the sparse vector entries
									 */
									while (sparseVecItrOut.hasNext()) {
										VectorEntry e = sparseVecItrOut.next();

										/*
										 * Getting the sparse vector index and
										 * values
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
									 * Multiply the matrix and the vector, to
									 * get the continuous representation
									 */
									psiSynEmbedded = new DenseVector(
											ZTSyn.numRows());
									ZTSyn.mult(psiDenseSyn, psiSynEmbedded);

									psiSynEmbedded = VSMUtil
											.normalizeVec(psiSynEmbedded);

								}

								if (psiSem != null) {
									psiDenseSem = new DenseVector(psiSem.size());
									/*
									 * Iterator over the sparse vector MTJ
									 */
									java.util.Iterator<VectorEntry> sparseVecItrSemOut = psiSem
											.iterator();

									/*
									 * Iterating over the sparse vector entries
									 */
									while (sparseVecItrSemOut.hasNext()) {
										VectorEntry e = sparseVecItrSemOut
												.next();

										/*
										 * Getting the sparse vector index and
										 * values
										 */
										int idx = e.index();
										double val = e.get();

										/*
										 * Forming the dense inside feature
										 * vector
										 */
										psiDenseSem.add(idx, val);
									}

									psiSemEmbedded = new DenseVector(
											ZTSem.numRows());
									// Vector continousRepAvg = new
									// DenseVector(YT.numRows());

									/*
									 * Forming the continuous representation
									 */

									ZTSem.mult(psiDenseSem, psiSemEmbedded);

									psiSemEmbedded = VSMUtil
											.normalizeVec(psiSemEmbedded);

								}

							}
						}
						/*
						 * Serializing the continuous rep the semantic and the
						 * syntactic
						 */

						vectorBeanEmbedded.setPsi((DenseVector) psiSynEmbedded);
						vectorBeanEmbedded
								.setPsiSem((DenseVector) psiSemEmbedded);
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
						psiSemEmbedded = null;
						System.gc();

						/*
						 * Serialize
						 */
						VSMSerializeFeatureVectorBeanEmbed.serializeVectorBean(
								vectorBeanEmbedded, directory.getName(),
								vector.getName());

						/*
						 * Dividing by the number of vectors of the same type
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
					matricesSem = null;

				}
			}
		}
	}
}
