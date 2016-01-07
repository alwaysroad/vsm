package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;
import VSMSerialization.VSMSerializeSentenceVectorBean;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class VSMSentenceEmbeddingOnlySyntactic {

	public static void main(String[] args) {
		String directoryPath = VSMContant.SICK_EMBEDDED_SENT_VECS_SYNTACTIC;

		/*
		 * Getting the directories in which the embedded feature vectors are
		 * stored
		 */
		File[] sentences = new File(directoryPath).listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isHidden();
			}
		});

		sortByNumber(sentences);

		/*
		 * Just taking the top 100 sentences for now
		 */
		sentences = Arrays.copyOf(sentences, 100);

		/*
		 * For each directory
		 */
		int sentenceCount = 0;
		for (File sentence : sentences) {

			sentenceCount++;

			/*
			 * Let's just take 4 non terminals for testing
			 */
			no.uib.cipr.matrix.Vector sentenceVector = new DenseVector(1600);

			no.uib.cipr.matrix.Vector[] embeddedFeatureVectors = new DenseVector[32];

			/*
			 * Getting the chunks in a sentence
			 */
			File[] chunks = sentence.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return !file.isHidden();
				}
			});

			no.uib.cipr.matrix.Vector ccinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector ccoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ccinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ccoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nninSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nninSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nnoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector dtinSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector ininSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector inoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ininSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector inoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector auxin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector auxout = new DenseVector(50);

			no.uib.cipr.matrix.Vector jjinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector jjoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector jjinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector jjoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nnpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnpoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nnpinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nnpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nnsinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnsoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nnsinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector nnsoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector ppin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ppout = new DenseVector(50);

			no.uib.cipr.matrix.Vector prpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector prpoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector prpinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector prpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector sbarinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector sbaroutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector sbarinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector sbaroutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector vbdin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbdout = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbgin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbgout = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbpoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbpinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbninSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbnoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbninSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbnoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbginSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbgoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbginSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbgoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector rbinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector rboutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector rbinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector rboutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector sinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector soutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector sinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector soutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector npinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector npoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector npinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector npoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vpoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vpinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector ppinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector ppoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ppinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ppoutSem = new DenseVector(50);
			/*
			 * Initializing the embeddeVectorsArray
			 */
			embeddedFeatureVectors[0] = ininSyn;
			embeddedFeatureVectors[1] = inoutSyn;

			embeddedFeatureVectors[2] = nninSyn;
			embeddedFeatureVectors[3] = nnoutSyn;

			embeddedFeatureVectors[4] = ccinSyn;
			embeddedFeatureVectors[5] = ccoutSyn;

			// embeddedFeatureVectors[6] = dtin;
			// embeddedFeatureVectors[7] = dtout;
			// embeddedFeatureVectors[8] = auxin;
			// embeddedFeatureVectors[9] = auxout;
			embeddedFeatureVectors[6] = jjinSyn;
			embeddedFeatureVectors[7] = jjoutSyn;

			embeddedFeatureVectors[8] = nnpinSyn;
			embeddedFeatureVectors[9] = nnpoutSyn;

			embeddedFeatureVectors[10] = nnsinSyn;
			embeddedFeatureVectors[11] = nnsoutSyn;

			// embeddedFeatureVectors[16] = ppin;
			// embeddedFeatureVectors[17] = ppout;
			embeddedFeatureVectors[12] = prpinSyn;
			embeddedFeatureVectors[13] = prpoutSyn;

			embeddedFeatureVectors[14] = sbarinSyn;
			embeddedFeatureVectors[15] = sbaroutSyn;

			// embeddedFeatureVectors[22] = vbdin;
			// embeddedFeatureVectors[23] = vbdout;
			// embeddedFeatureVectors[24] = vbgin;
			// embeddedFeatureVectors[25] = vbgout;
			embeddedFeatureVectors[16] = vbpinSyn;
			embeddedFeatureVectors[17] = vbpoutSyn;

			embeddedFeatureVectors[18] = rbinSyn;
			embeddedFeatureVectors[19] = rboutSyn;

			embeddedFeatureVectors[20] = sinSyn;
			embeddedFeatureVectors[21] = soutSyn;

			embeddedFeatureVectors[22] = vbginSyn;
			embeddedFeatureVectors[23] = vbgoutSyn;

			embeddedFeatureVectors[24] = vbninSyn;
			embeddedFeatureVectors[25] = vbnoutSyn;

			embeddedFeatureVectors[26] = npinSyn;
			embeddedFeatureVectors[27] = npoutSyn;

			embeddedFeatureVectors[28] = vpinSyn;
			embeddedFeatureVectors[29] = vpoutSyn;

			embeddedFeatureVectors[30] = ppinSyn;
			embeddedFeatureVectors[31] = ppoutSyn;

			for (File chunk : chunks) {

				File[] embeddedVectors = chunk.listFiles(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return !file.isHidden();
					}
				});
				/*
				 * Getting the name of the chunk
				 */
				String chunkName = chunk.getName();

				// if (chunkName.equalsIgnoreCase("S")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// DenseVector outVec = vectorBean.getPsiEmbedded();
				// sinSyn = sinSyn.add(vectorBean.getPhiEmbedded());
				// soutSyn = soutSyn.add(outVec);
				// sinSem = sinSem.add(vectorBean.getPhiSem());
				// soutSem = soutSem.add(vectorBean.getPsiSem());
				//
				// }
				//
				// sinSyn = sinSyn.scale((double) 1 / count);
				// soutSyn = soutSyn.scale((double) 1 / count);
				// sinSem = sinSem.scale((double) 1 / count);
				// soutSem = soutSem.scale((double) 1 / count);
				// }

				if (chunkName.equalsIgnoreCase("CC")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							ccinSyn = ccinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							ccoutSyn = ccoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					ccinSyn = ccinSyn.scale((double) 1 / count);
					ccoutSyn = ccoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("NN")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());
						if (vectorBean.getPhiEmbedded() != null)
							nninSyn = nninSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							nnoutSyn = nnoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					nninSyn = nninSyn.scale((double) 1 / count);
					nnoutSyn = nnoutSyn.scale((double) 1 / count);

				}

				// if (chunkName.equalsIgnoreCase("DT")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// DenseVector phi = vectorBean.getPhiEmbedded();
				// DenseVector psi = vectorBean.getPsiEmbedded();
				// if (phi != null) {
				// dtin = dtin.add(phi);
				// }
				// if (psi != null) {
				// dtout = dtout.add(psi);
				// }
				//
				// }
				// if (dtin != null) {
				// dtin = dtin.scale((double) 1 / count);
				// }
				// if (dtout != null) {
				// dtout = dtout.scale((double) 1 / count);
				// }
				// }

				if (chunkName.equalsIgnoreCase("IN")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							ininSyn = ininSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							inoutSyn = inoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					ininSyn = ininSyn.scale((double) 1 / count);
					inoutSyn = inoutSyn.scale((double) 1 / count);

				}

				// if (chunkName.equalsIgnoreCase("AUX")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// auxin = auxin.add(vectorBean.getPhiEmbedded());
				// auxout = auxout.add(vectorBean.getPsiEmbedded());
				//
				// }
				//
				// auxin = auxin.scale((double) 1 / count);
				// auxout = auxout.scale((double) 1 / count);
				// }

				if (chunkName.equalsIgnoreCase("JJ")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							jjinSyn = jjinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							jjoutSyn = jjoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					jjinSyn = jjinSyn.scale((double) 1 / count);
					jjoutSyn = jjoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("NNP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							nnpinSyn = nnpinSyn
									.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							nnpoutSyn = nnpoutSyn.add(vectorBean
									.getPsiEmbedded());

					}

					nnpinSyn = nnpinSyn.scale((double) 1 / count);
					nnpoutSyn = nnpoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("NNS")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							nnsinSyn = nnsinSyn
									.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							nnsoutSyn = nnsoutSyn.add(vectorBean
									.getPsiEmbedded());

					}

					nnsinSyn = nnsinSyn.scale((double) 1 / count);
					nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

				}
				//
				// if (chunkName.equalsIgnoreCase("PP")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// ppin = ppin.add(vectorBean.getPhiEmbedded());
				// ppout = ppout.add(vectorBean.getPsiEmbedded());
				//
				// }
				//
				// ppin = ppin.scale((double) 1 / count);
				// ppout = ppout.scale((double) 1 / count);
				// }

				if (chunkName.equalsIgnoreCase("PRP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							prpinSyn = prpinSyn
									.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							prpoutSyn = prpoutSyn.add(vectorBean
									.getPsiEmbedded());

					}

					prpinSyn = prpinSyn.scale((double) 1 / count);
					prpoutSyn = prpoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("SBAR")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							sbarinSyn = sbarinSyn.add(vectorBean
									.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							sbaroutSyn = sbaroutSyn.add(vectorBean
									.getPsiEmbedded());

					}

					sbarinSyn = sbarinSyn.scale((double) 1 / count);
					sbaroutSyn = sbaroutSyn.scale((double) 1 / count);

				}

				// if (chunkName.equalsIgnoreCase("VBD")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// vbdinS = vbdin.add(vectorBean.getPhiEmbedded());
				// vbdout = vbdout.add(vectorBean.getPsiEmbedded());
				//
				// }
				//
				// vbdin = vbdin.scale((double) 1 / count);
				// vbdout = vbdout.scale((double) 1 / count);
				// }
				//
				// if (chunkName.equalsIgnoreCase("VBG")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// vbgin = vbgin.add(vectorBean.getPhiEmbedded());
				// vbgout = vbgout.add(vectorBean.getPsiEmbedded());
				//
				// }
				//
				// vbgin = vbgin.scale((double) 1 / count);
				// vbgout = vbgout.scale((double) 1 / count);
				// }

				// if (chunkName.equalsIgnoreCase("VBP")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				// .readFeatureVectorBeanEmbedded(embeddeVec
				// .getAbsolutePath());
				// vbpin = vbpin.add(vectorBean.getPhiEmbedded());
				// vbpout = vbpout.add(vectorBean.getPsiEmbedded());
				//
				// }
				//
				// vbpin = vbpin.scale((double) 1 / count);
				// vbpout = vbpout.scale((double) 1 / count);
				// }

				if (chunkName.equalsIgnoreCase("RB")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							rbinSyn = rbinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							rboutSyn = rboutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					rbinSyn = rbinSyn.scale((double) 1 / count);
					rboutSyn = rboutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("NP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							npinSyn = npinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							npoutSyn = npoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					npinSyn = npinSyn.scale((double) 1 / count);
					npoutSyn = npoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("VP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							vpinSyn = vpinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							vpoutSyn = vpoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					vpinSyn = vpinSyn.scale((double) 1 / count);
					vpoutSyn = vpoutSyn.scale((double) 1 / count);

				}

				if (chunkName.equalsIgnoreCase("PP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
								.readFeatureVectorBeanEmbedded(embeddeVec
										.getAbsolutePath());

						if (vectorBean.getPhiEmbedded() != null)
							ppinSyn = ppinSyn.add(vectorBean.getPhiEmbedded());

						if (vectorBean.getPsiEmbedded() != null)
							ppoutSyn = ppoutSyn
									.add(vectorBean.getPsiEmbedded());

					}

					ppinSyn = ppinSyn.scale((double) 1 / count);
					ppoutSyn = ppoutSyn.scale((double) 1 / count);

				}

			}

			/*
			 * Forming the sentence vector
			 */
			int startIdx = 0;
			// System.out.println(embeddedFeatureVectors[0]);
			Iterator<VectorEntry> iterator = null;
			for (Vector vector : embeddedFeatureVectors) {
				// System.out.println(vector.iterator());
				iterator = vector.iterator();
				while (iterator.hasNext()) {
					VectorEntry e = iterator.next();
					int idx = e.index() + startIdx;
					double val = e.get();
					sentenceVector.add(idx, val);
				}

				startIdx = startIdx + vector.size();
			}

			/*
			 * Forming the sentence vector bean
			 */
			VSMSentenceVecBean vecBean = new VSMSentenceVecBean();
			vecBean.setSentenceVector((DenseVector) sentenceVector);

			System.out.println("***Serializing the sentence vector****");
			/*
			 * Serializing the sentence vector bean
			 */
			VSMSerializeSentenceVectorBean.sentenceVectorBeanSyntactic(vecBean,
					sentence.getName());
			System.out.println("***Serialized****");

			// if (sentenceCount > 24) {
			// break;
			// }

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
