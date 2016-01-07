package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;
import VSMSerialization.VSMSerializeSentenceVectorBean;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class VSMSentenceEmbedding {

	public static void main(String[] args) {
		String directoryPath = VSMContant.SICK_EMBEDDED_SENT_VECS;

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

		File[] arr = new File[100];

		sortByNumber(sentences);

		arr = Arrays.copyOf(sentences, 100);

		/*
		 * For each directory
		 */
		int sentenceCount = 0;
		for (File sentence : arr) {

			sentenceCount++;

			/*
			 * Let's just take 4 non terminals for testing
			 */
			no.uib.cipr.matrix.Vector sentenceVector = new DenseVector(3200);

			no.uib.cipr.matrix.Vector[] embeddedFeatureVectors = new DenseVector[64];

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
			no.uib.cipr.matrix.Vector ccinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector ccoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nninSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nninSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector dtinSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtoutSyn = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtinSem = new DenseVector(50);
			// no.uib.cipr.matrix.Vector dtoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector ininSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector inoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector ininSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector inoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector auxin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector auxout = new DenseVector(50);

			no.uib.cipr.matrix.Vector jjinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector jjoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector jjinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector jjoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nnpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnpoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnpinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector nnsinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnsoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnsinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector nnsoutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector ppin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector ppout = new DenseVector(50);

			no.uib.cipr.matrix.Vector prpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector prpoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector prpinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector prpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector sbarinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector sbaroutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector sbarinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector sbaroutSem = new DenseVector(50);

			// no.uib.cipr.matrix.Vector vbdin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbdout = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbgin = new DenseVector(50);
			// no.uib.cipr.matrix.Vector vbgout = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbpoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbpinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbninSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbnoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbninSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbnoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbginSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbgoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbginSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbgoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector rbinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector rboutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector rbinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector rboutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector sinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector soutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector sinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector soutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector npinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector npoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector npinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector npoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vpinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vpoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector vpinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector ppinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector ppoutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector ppinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector ppoutSem = new DenseVector(50);
			/*
			 * Initializing the embeddeVectorsArray
			 */
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
			// embeddedFeatureVectors[6] = dtin;
			// embeddedFeatureVectors[7] = dtout;
			// embeddedFeatureVectors[8] = auxin;
			// embeddedFeatureVectors[9] = auxout;
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
			// embeddedFeatureVectors[16] = ppin;
			// embeddedFeatureVectors[17] = ppout;
			embeddedFeatureVectors[24] = prpinSyn;
			embeddedFeatureVectors[25] = prpoutSyn;
			embeddedFeatureVectors[26] = prpinSem;
			embeddedFeatureVectors[27] = prpoutSem;

			embeddedFeatureVectors[28] = sbarinSyn;
			embeddedFeatureVectors[29] = sbaroutSyn;
			embeddedFeatureVectors[30] = sbarinSem;
			embeddedFeatureVectors[31] = sbaroutSem;
			// embeddedFeatureVectors[22] = vbdin;
			// embeddedFeatureVectors[23] = vbdout;
			// embeddedFeatureVectors[24] = vbgin;
			// embeddedFeatureVectors[25] = vbgout;
			embeddedFeatureVectors[32] = vbpinSyn;
			embeddedFeatureVectors[33] = vbpoutSyn;
			embeddedFeatureVectors[34] = vbpinSem;
			embeddedFeatureVectors[35] = vbpoutSem;

			embeddedFeatureVectors[36] = rbinSyn;
			embeddedFeatureVectors[37] = rboutSyn;
			embeddedFeatureVectors[38] = rbinSem;
			embeddedFeatureVectors[39] = rboutSem;

			embeddedFeatureVectors[40] = sinSyn;
			embeddedFeatureVectors[41] = soutSyn;
			embeddedFeatureVectors[42] = sinSem;
			embeddedFeatureVectors[43] = soutSem;

			embeddedFeatureVectors[44] = vbginSyn;
			embeddedFeatureVectors[45] = vbgoutSyn;
			embeddedFeatureVectors[46] = vbginSem;
			embeddedFeatureVectors[47] = vbgoutSem;

			embeddedFeatureVectors[48] = vbninSyn;
			embeddedFeatureVectors[49] = vbnoutSyn;
			embeddedFeatureVectors[50] = vbninSem;
			embeddedFeatureVectors[51] = vbnoutSem;

			embeddedFeatureVectors[52] = npinSyn;
			embeddedFeatureVectors[53] = npoutSyn;
			embeddedFeatureVectors[54] = npinSem;
			embeddedFeatureVectors[55] = npoutSem;

			embeddedFeatureVectors[56] = vpinSyn;
			embeddedFeatureVectors[57] = vpoutSyn;
			embeddedFeatureVectors[58] = vpinSem;
			embeddedFeatureVectors[59] = vpoutSem;

			embeddedFeatureVectors[60] = ppinSyn;
			embeddedFeatureVectors[61] = ppoutSyn;
			embeddedFeatureVectors[62] = ppinSem;
			embeddedFeatureVectors[63] = ppoutSem;

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

						if (vectorBean.getPhiSem() != null)
							ccinSem = ccinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							ccoutSem = ccoutSem.add(vectorBean.getPsiSem());

					}

					ccinSyn = ccinSyn.scale((double) 1 / count);
					ccoutSyn = ccoutSyn.scale((double) 1 / count);

					ccinSem = ccinSem.scale((double) 1 / count);
					ccoutSem = ccoutSem.scale((double) 1 / count);

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

						if (vectorBean.getPhiSem() != null)
							nninSem = nninSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							nnoutSem = nnoutSem.add(vectorBean.getPsiSem());

					}

					nninSyn = nninSyn.scale((double) 1 / count);
					nnoutSyn = nnoutSyn.scale((double) 1 / count);
					nninSem = nninSem.scale((double) 1 / count);
					nnoutSem = nnoutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							ininSem = ininSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							inoutSem = inoutSem.add(vectorBean.getPsiSem());

					}

					ininSyn = ininSyn.scale((double) 1 / count);
					inoutSyn = inoutSyn.scale((double) 1 / count);
					ininSem = ininSem.scale((double) 1 / count);
					inoutSem = inoutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							jjinSem = jjinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							jjoutSem = jjoutSem.add(vectorBean.getPsiSem());

					}

					jjinSyn = jjinSyn.scale((double) 1 / count);
					jjoutSyn = jjoutSyn.scale((double) 1 / count);

					jjinSem = jjinSem.scale((double) 1 / count);
					jjoutSem = jjoutSem.scale((double) 1 / count);

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

						if (vectorBean.getPhiSem() != null)
							nnpinSem = nnpinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							nnpoutSem = nnpoutSem.add(vectorBean.getPsiSem());

					}

					nnpinSyn = nnpinSyn.scale((double) 1 / count);
					nnpoutSyn = nnpoutSyn.scale((double) 1 / count);

					nnpinSem = nnpinSem.scale((double) 1 / count);
					nnpoutSem = nnpoutSem.scale((double) 1 / count);

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

						if (vectorBean.getPhiSem() != null)
							nnsinSem = nnsinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							nnsoutSem = nnsoutSem.add(vectorBean.getPsiSem());

					}

					nnsinSyn = nnsinSyn.scale((double) 1 / count);
					nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

					nnsinSem = nnsinSem.scale((double) 1 / count);
					nnsoutSem = nnsoutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							prpinSem = prpinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							prpoutSem = prpoutSem.add(vectorBean.getPsiSem());

					}

					prpinSyn = prpinSyn.scale((double) 1 / count);
					prpoutSyn = prpoutSyn.scale((double) 1 / count);
					prpinSem = prpinSem.scale((double) 1 / count);
					prpoutSem = prpoutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							sbarinSem = sbarinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							sbaroutSem = sbaroutSem.add(vectorBean.getPsiSem());

					}

					sbarinSyn = sbarinSyn.scale((double) 1 / count);
					sbaroutSyn = sbaroutSyn.scale((double) 1 / count);
					sbarinSem = sbarinSem.scale((double) 1 / count);
					sbaroutSem = sbaroutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							rbinSem = rbinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							rboutSem = rboutSem.add(vectorBean.getPsiSem());

					}

					rbinSyn = rbinSyn.scale((double) 1 / count);
					rboutSyn = rboutSyn.scale((double) 1 / count);

					rbinSem = rbinSem.scale((double) 1 / count);
					rboutSem = rboutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							vpinSem = vpinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							vpoutSem = vpoutSem.add(vectorBean.getPsiSem());

					}

					vpinSyn = vpinSyn.scale((double) 1 / count);
					vpoutSyn = vpoutSyn.scale((double) 1 / count);

					vpinSem = vpinSem.scale((double) 1 / count);
					vpoutSem = vpoutSem.scale((double) 1 / count);
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

						if (vectorBean.getPhiSem() != null)
							ppinSem = ppinSem.add(vectorBean.getPhiSem());

						if (vectorBean.getPsiSem() != null)
							ppoutSem = ppoutSem.add(vectorBean.getPsiSem());

					}

					ppinSyn = ppinSyn.scale((double) 1 / count);
					ppoutSyn = ppoutSyn.scale((double) 1 / count);

					ppinSem = ppinSem.scale((double) 1 / count);
					ppoutSem = ppoutSem.scale((double) 1 / count);
				}

			}

			/*
			 * Forming the sentence vector
			 */
			int startIdx = 0;
			for (no.uib.cipr.matrix.Vector vector : embeddedFeatureVectors) {
				Iterator<VectorEntry> vectorItr = vector.iterator();
				while (vectorItr.hasNext()) {
					VectorEntry e = vectorItr.next();
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
			VSMSerializeSentenceVectorBean.sentenceVectorBeanSynSem(vecBean,
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
