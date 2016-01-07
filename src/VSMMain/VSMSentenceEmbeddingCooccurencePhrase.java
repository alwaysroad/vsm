package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;

import org.apache.commons.lang3.StringUtils;

import weka.core.Stopwords;
import VSMConstants.VSMContant;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;
import VSMSerialization.VSMSerializeSentenceVectorBean;
import VSMSerialization.VSMWordEmbeddingMapBean;

/**
 * The class to generate the sentence vectors from the embedded feature vectors
 * 
 * @author s1444025
 *
 */

public class VSMSentenceEmbeddingCooccurencePhrase {

	public static void main(String[] args) {
		String directoryPath = VSMContant.SICK_BINARY_SENT_VECS;

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

		/*
		 * For each directory
		 */
		int sentenceCount = 0;

		/*
		 * Iterating over the sentences
		 */
		for (File sentence : sentences) {

			System.out.println("*****For sentence****" + sentence.getName());
			sentenceCount++;

			/*
			 * Let's just take 4 non terminals for testing
			 */
			no.uib.cipr.matrix.Vector sentenceVector = new DenseVector(600);

			no.uib.cipr.matrix.Vector[] embeddedFeatureVectors = new DenseVector[12];

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

			no.uib.cipr.matrix.Vector rbinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector rboutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector rbinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector rboutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector sinSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector soutSyn = new DenseVector(50);
			no.uib.cipr.matrix.Vector npinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector npoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vpinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vpoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector ppinSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector ppoutSem = new DenseVector(50);

			no.uib.cipr.matrix.Vector vbninSem = new DenseVector(50);
			no.uib.cipr.matrix.Vector vbnoutSem = new DenseVector(50);
			/*
			 * Initializing the embeddeVectorsArray
			 */
			int idx = 0;
			// embeddedFeatureVectors[0] = ininSyn;
			// embeddedFeatureVectors[1] = inoutSyn;
			// embeddedFeatureVectors[0] = ininSem;
			// embeddedFeatureVectors[1] = inoutSem;
			// embeddedFeatureVectors[4] = nninSyn;
			// embeddedFeatureVectors[5] = nnoutSyn;
			// embeddedFeatureVectors[2] = nninSem;
			// embeddedFeatureVectors[3] = nnoutSem;
			// embeddedFeatureVectors[8] = ccinSyn;
			// embeddedFeatureVectors[9] = ccoutSyn;
			// embeddedFeatureVectors[4] = ccinSem;
			// embeddedFeatureVectors[5] = ccoutSem;
			// embeddedFeatureVectors[6] = dtin;
			// embeddedFeatureVectors[7] = dtout;
			// embeddedFeatureVectors[8] = auxin;
			// embeddedFeatureVectors[9] = auxout;
			// embeddedFeatureVectors[12] = jjinSyn;
			// embeddedFeatureVectors[13] = jjoutSyn;
			// embeddedFeatureVectors[6] = jjinSem;
			// embeddedFeatureVectors[7] = jjoutSem;

			// embeddedFeatureVectors[16] = nnpinSyn;
			// embeddedFeatureVectors[17] = nnpoutSyn;
			// embeddedFeatureVectors[8] = nnpinSem;
			// embeddedFeatureVectors[9] = nnpoutSem;

			// embeddedFeatureVectors[20] = nnsinSyn;
			// embeddedFeatureVectors[21] = nnsoutSyn;
			// embeddedFeatureVectors[10] = nnsinSem;
			// embeddedFeatureVectors[11] = nnsoutSem;
			// embeddedFeatureVectors[16] = ppin;
			// embeddedFeatureVectors[17] = ppout;
			// embeddedFeatureVectors[24] = prpinSyn;
			// embeddedFeatureVectors[25] = prpoutSyn;
			embeddedFeatureVectors[0] = prpinSem;
			embeddedFeatureVectors[1] = prpoutSem;

			// embeddedFeatureVectors[28] = sbarinSyn;
			// embeddedFeatureVectors[29] = sbaroutSyn;
			embeddedFeatureVectors[2] = sbarinSem;
			embeddedFeatureVectors[3] = sbaroutSem;
			// embeddedFeatureVectors[22] = vbdin;
			// embeddedFeatureVectors[23] = vbdout;
			// embeddedFeatureVectors[24] = vbgin;
			// embeddedFeatureVectors[25] = vbgout;
			// embeddedFeatureVectors[32] = vbpinSyn;
			// embeddedFeatureVectors[33] = vbpoutSyn;
			// embeddedFeatureVectors[5] = vbpinSem;
			// embeddedFeatureVectors[6] = vbpoutSem;

			// embeddedFeatureVectors[36] = rbinSyn;
			// embeddedFeatureVectors[37] = rboutSyn;
			// embeddedFeatureVectors[18] = rbinSem;
			// embeddedFeatureVectors[19] = rboutSem;

			// embeddedFeatureVectors[40] = sinSyn;
			// embeddedFeatureVectors[41] = soutSyn;
			embeddedFeatureVectors[4] = npinSem;
			embeddedFeatureVectors[5] = npoutSem;

			embeddedFeatureVectors[6] = vpinSem;
			embeddedFeatureVectors[7] = vpoutSem;

			embeddedFeatureVectors[8] = ppinSem;
			embeddedFeatureVectors[9] = ppoutSem;

			embeddedFeatureVectors[10] = vbninSem;
			embeddedFeatureVectors[11] = vbnoutSem;

			// Getting the word embeddings map
			VSMWordEmbeddingMapBean bean = VSMReadSerialObject
					.readWordEmbeddingMapBean(VSMContant.CCA_WORD_EMBEDDING_OBJ);

			HashMap<String, double[]> embeddingsMap = bean.getEmbeddingsMap();

			/*
			 * Chunks that the sentence is made of
			 */
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
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;

						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										ccinSem.add(temp);
									}
								}
							}

							ccinSem = ccinSem.scale(1.0 / (double) insideWords
									.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										ccoutSem.add(temp);
									}
								}
							}

							ccoutSem = ccoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// ccinSyn = ccinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// ccoutSyn = ccoutSyn
						// .add(vectorBean.getPsiEmbedded());

						// if (vectorBean.getPhiSem() != null)
						// ccinSem = ccinSem.add(vectorBean.getPhiSem());
						//
						// if (vectorBean.getPsiSem() != null)
						// ccoutSem = ccoutSem.add(vectorBean.getPsiSem());

					}
					//
					// ccinSyn = ccinSyn.scale((double) 1 / count);
					// ccoutSyn = ccoutSyn.scale((double) 1 / count);

					// ccinSem = insideWordVecsAvg;
					// ccoutSem = outsideWordVecsAvg;

				}

				if (chunkName.equalsIgnoreCase("NN")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										nninSem.add(temp);
									}
								}
							}

							nninSem = nninSem.scale(1.0 / (double) insideWords
									.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										nnoutSem.add(temp);
									}
								}
							}

							nnoutSem = nnoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// nninSyn = nninSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// nnoutSyn = nnoutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// nninSyn = nninSyn.scale((double) 1 / count);
					// nnoutSyn = nnoutSyn.scale((double) 1 / count);
					// nninSem = insideWordVecsAvg;
					// nnoutSem = outsideWordVecsAvg;
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
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										ininSem.add(temp);
									}
								}
							}

							ininSem = ininSem.scale(1.0 / (double) insideWords
									.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										inoutSem.add(temp);
									}
								}
							}

							inoutSem = inoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// ininSyn = ininSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// inoutSyn = inoutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// ininSyn = ininSyn.scale((double) 1 / count);
					// inoutSyn = inoutSyn.scale((double) 1 / count);
					// ininSem = insideWordVecsAvg;
					// inoutSem = outsideWordVecsAvg;
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
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										jjinSem.add(temp);
									}
								}
							}

							jjinSem = jjinSem.scale(1.0 / (double) insideWords
									.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										jjoutSem.add(temp);
									}
								}
							}

							jjoutSem = jjoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// jjinSyn = jjinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// jjoutSyn = jjoutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// jjinSyn = jjinSyn.scale((double) 1 / count);
					// jjoutSyn = jjoutSyn.scale((double) 1 / count);
					//
					// jjinSem = insideWordVecsAvg;
					// jjoutSem = outsideWordVecsAvg;

				}

				if (chunkName.equalsIgnoreCase("NNP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										nnpinSem.add(temp);
									}
								}
							}

							nnpinSem = nnpinSem
									.scale(1.0 / (double) insideWords.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										nnpoutSem.add(temp);
									}
								}
							}

							nnpoutSem = nnpoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// nnpinSyn = nnpinSyn
						// .add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// nnpoutSyn = nnpoutSyn.add(vectorBean
						// .getPsiEmbedded());

					}

					// nnpinSyn = nnpinSyn.scale((double) 1 / count);
					// nnpoutSyn = nnpoutSyn.scale((double) 1 / count);
					//
					// nnpinSem = insideWordVecsAvg;
					// nnpoutSem = outsideWordVecsAvg;

				}

				if (chunkName.equalsIgnoreCase("NNS")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									no.uib.cipr.matrix.Vector temp = new DenseVector(
											embedding);
									nnsinSem.add(temp);
								}
							}

							nnsinSem = nnsinSem
									.scale(1.0 / (double) insideWords.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										nnsoutSem.add(temp);
									}
								}
							}

							nnsoutSem = nnsoutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// nnsinSyn = nnsinSyn
						// .add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// nnsoutSyn = nnsoutSyn.add(vectorBean
						// .getPsiEmbedded());
						//
						// if (vectorBean.getPhiSem() != null)
						// nnsinSem = nnsinSem.add(vectorBean.getPhiSem());
						//
						// if (vectorBean.getPsiSem() != null)
						// nnsoutSem = nnsoutSem.add(vectorBean.getPsiSem());
						//
					}

					// nnsinSyn = nnsinSyn.scale((double) 1 / count);
					// nnsoutSyn = nnsoutSyn.scale((double) 1 / count);

					// nnsinSem = insideWordVecsAvg;
					// nnsoutSem = outsideWordVecsAvg;
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
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										prpinSem.add(temp);
									}
								}
							}

							prpinSem = prpinSem
									.scale(1.0 / (double) insideWords.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word)) {
									double[] embedding = embeddingsMap
											.get(word);
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										prpoutSem.add(temp);
									}
								}
							}

							prpoutSem = prpoutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// prpinSyn = prpinSyn
						// .add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// prpoutSyn = prpoutSyn.add(vectorBean
						// .getPsiEmbedded());

					}

					// prpinSyn = prpinSyn.scale((double) 1 / count);
					// prpoutSyn = prpoutSyn.scale((double) 1 / count);
					// prpinSem = insideWordVecsAvg;
					// prpoutSem = outsideWordVecsAvg;
				}

				if (chunkName.equalsIgnoreCase("SBAR")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */
					int count = 0;
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										sbarinSem.add(temp);
									}
								}
							}

							sbarinSem = sbarinSem
									.scale(1.0 / (double) insideWords.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										sbaroutSem.add(temp);
									}
								}
							}

							sbaroutSem = sbaroutSem
									.scale(1.0 / (double) outsideWords.size());
						}

						// if (vectorBean.getPhiEmbedded() != null)
						// sbarinSyn = sbarinSyn.add(vectorBean
						// .getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// sbaroutSyn = sbaroutSyn.add(vectorBean
						// .getPsiEmbedded());
						//
						// }

						// sbarinSyn = sbarinSyn.scale((double) 1 / count);
						// sbaroutSyn = sbaroutSyn.scale((double) 1 / count);
						// sbarinSem = insideWordVecsAvg;
						// sbaroutSem = outsideWordVecsAvg;
					}
				}
				// if (chunkName.equalsIgnoreCase("VBD")) {
				// /*
				// * Now we are inside the chunk and getting the embedded
				// * vector representation
				// */
				// int count = 0;
				// for (File embeddeVec : embeddedVectors) {
				// count++;
				// VSMFeatureVectorBeanEmbedded vectorBean =
				// VSMReadSerialObject
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
				// VSMFeatureVectorBeanEmbedded vectorBean =
				// VSMReadSerialObject
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
				// VSMFeatureVectorBeanEmbedded vectorBean =
				// VSMReadSerialObject
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
					no.uib.cipr.matrix.Vector insideWordVecsAvg = null;
					no.uib.cipr.matrix.Vector outsideWordVecsAvg = null;
					for (File embeddeVec : embeddedVectors) {
						count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										rbinSem.add(temp);
									}
								}
							}

							rbinSem = rbinSem.scale(1.0 / (double) insideWords
									.size());
						}

						outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										rboutSem.add(temp);
									}
								}
							}

							rboutSem = rboutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// rbinSyn =
						// rbinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// rboutSyn = rboutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// rbinSyn = rbinSyn.scale((double) 1 / count);
					// rboutSyn = rboutSyn.scale((double) 1 / count);

					// rbinSem = insideWordVecsAvg;
					// rboutSem = outsideWordVecsAvg;
				}

				if (chunkName.equalsIgnoreCase("NP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */

					for (File embeddeVec : embeddedVectors) {
						// count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						// insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										npinSem.add(temp);
									}
								}
							}

							npinSem = npinSem.scale(1.0 / (double) insideWords
									.size());
						}

						// outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										npoutSem.add(temp);
									}
								}
							}

							npoutSem = npoutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// rbinSyn =
						// rbinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// rboutSyn = rboutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// rbinSyn = rbinSyn.scale((double) 1 / count);
					// rboutSyn = rboutSyn.scale((double) 1 / count);

					// rbinSem = insideWordVecsAvg;
					// rboutSem = outsideWordVecsAvg;
				}

				if (chunkName.equalsIgnoreCase("VP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */

					for (File embeddeVec : embeddedVectors) {
						// count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						// insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										vpinSem.add(temp);
									}
								}
							}

							vpinSem = vpinSem.scale(1.0 / (double) insideWords
									.size());
						}

						// outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										vpoutSem.add(temp);
									}
								}
							}

							vpoutSem = vpoutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// rbinSyn =
						// rbinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// rboutSyn = rboutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// rbinSyn = rbinSyn.scale((double) 1 / count);
					// rboutSyn = rboutSyn.scale((double) 1 / count);

					// rbinSem = insideWordVecsAvg;
					// rboutSem = outsideWordVecsAvg;
				}

				if (chunkName.equalsIgnoreCase("PP")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */

					for (File embeddeVec : embeddedVectors) {
						// count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						// insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										ppinSem.add(temp);
									}
								}
							}

							ppinSem = ppinSem.scale(1.0 / (double) insideWords
									.size());
						}

						// outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										ppoutSem.add(temp);
									}
								}
							}

							ppoutSem = ppoutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// rbinSyn =
						// rbinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// rboutSyn = rboutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// rbinSyn = rbinSyn.scale((double) 1 / count);
					// rboutSyn = rboutSyn.scale((double) 1 / count);

					// rbinSem = insideWordVecsAvg;
					// rboutSem = outsideWordVecsAvg;
				}

				if (chunkName.equalsIgnoreCase("VBN")) {
					/*
					 * Now we are inside the chunk and getting the embedded
					 * vector representation
					 */

					for (File embeddeVec : embeddedVectors) {
						// count++;
						VSMFeatureVectorBean vectorBean = VSMReadSerialObject
								.readFeatureVectorBean(embeddeVec
										.getAbsolutePath());

						List<String> allWords = vectorBean.getSyntaxTree()
								.getTerminalYield();
						List<String> insideWords = vectorBean.getInsideTree()
								.getTerminalYield();
						List<String> outsideWords = null;
						allWords.removeAll(insideWords);
						outsideWords = allWords;

						// cleaning up
						Iterator<String> insideWListItr = insideWords
								.iterator();
						while (insideWListItr.hasNext()) {

							String word = insideWListItr.next();

							if (Stopwords.isStopword(word)) {
								insideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										insideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}
						}

						Iterator<String> outsideWListItr = outsideWords
								.iterator();
						while (outsideWListItr.hasNext()) {
							String word = outsideWListItr.next();

							if (Stopwords.isStopword(word)) {
								outsideWListItr.remove();
							}

							if (!StringUtils.isAlphanumeric(word)) {
								try {
									if (!Character.isLetterOrDigit(word
											.charAt(0))) {
										outsideWListItr.remove();
									}
								} catch (StringIndexOutOfBoundsException e) {
									System.out
											.println("**Catching the Exception and moving on***"
													+ e);
								}
							}

						}

						// Getting the inside and outside vectors
						// insideWordVecsAvg = new DenseVector(50);
						if (!insideWords.isEmpty()) {
							for (String word : insideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										vbninSem.add(temp);
									}
								}
							}

							vbninSem = vbninSem
									.scale(1.0 / (double) insideWords.size());
						}

						// outsideWordVecsAvg = new DenseVector(50);
						if (!outsideWords.isEmpty()) {
							for (String word : outsideWords) {
								if (embeddingsMap.containsKey(word
										.toLowerCase())
										|| embeddingsMap.containsKey(word
												.toUpperCase())) {
									double[] embedding = embeddingsMap.get(word
											.toLowerCase());
									if (embedding != null) {
										no.uib.cipr.matrix.Vector temp = new DenseVector(
												embedding);
										vbnoutSem.add(temp);
									}
								}
							}

							vbnoutSem = vbnoutSem
									.scale(1.0 / (double) outsideWords.size());
						}
						//
						// if (vectorBean.getPhiEmbedded() != null)
						// rbinSyn =
						// rbinSyn.add(vectorBean.getPhiEmbedded());
						//
						// if (vectorBean.getPsiEmbedded() != null)
						// rboutSyn = rboutSyn
						// .add(vectorBean.getPsiEmbedded());

					}

					// rbinSyn = rbinSyn.scale((double) 1 / count);
					// rboutSyn = rboutSyn.scale((double) 1 / count);

					// rbinSem = insideWordVecsAvg;
					// rboutSem = outsideWordVecsAvg;
				}

			}

			/*
			 * Forming the sentence vector
			 */
			int startIdx = 0;
			int k = 0;
			for (no.uib.cipr.matrix.Vector vector : embeddedFeatureVectors) {

				System.out.println("*****" + k + "  " + vector + "********");
				k++;
				Iterator<VectorEntry> vectorItr = vector.iterator();
				while (vectorItr.hasNext()) {
					VectorEntry e = vectorItr.next();
					int idx1 = e.index() + startIdx;
					double val = e.get();
					sentenceVector.add(idx1, val);
				}

				startIdx = startIdx + vector.size();
			}

			System.out.println("***sentence size***" + sentenceVector);

			/*
			 * Forming the sentence vector bean
			 */
			VSMSentenceVecBean vecBean = new VSMSentenceVecBean();
			vecBean.setSentenceVector((DenseVector) sentenceVector);

			System.out.println("***Serializing the sentence vector****");
			/*
			 * Serializing the sentence vector bean
			 */
			VSMSerializeSentenceVectorBean.sentenceVectorBeanPhrases(vecBean,
					sentence.getName());
			System.out.println("***Serialized****");

			// if (sentenceCount > 24) {
			// break;
			// }
		}

	}
}
