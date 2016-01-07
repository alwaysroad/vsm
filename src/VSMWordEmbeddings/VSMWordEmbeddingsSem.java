package VSMWordEmbeddings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import weka.core.Stopwords;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import Jama.Matrix;
import VSMConstants.VSMContant;
import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMSerializeSemWordEmbedding;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordEmbeddingSem;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;

/**
 * This class has code to produce the word embeddings for each word. We parse
 * through all the nodes, check if it is a pre-terminal, if it is then we get
 * the projection matrix for the node. The syntactic and the semantic projection
 * matrices. Four in total. Multiply the inside binary semantic vector with the
 * inside semantic projection matrix, inside syntactic feature vector with
 * inside syntactic projection matrix, same with the outside. The average all
 * the vectors together to get the word embedding of a particular word
 * 
 * @author s1444025
 *
 */

public class VSMWordEmbeddingsSem {

	public static void main(String... args) throws Exception {

		/*
		 * These are the projection matrices used to form the lower dimensional
		 * representation for the sentence
		 */
		Object[] matrices = null;

		/*
		 * Used to normalize the trees
		 */

		PTBTreeNormaliser treeNormalizer = new PTBTreeNormaliser(true);

		Alphabet tokens = new Alphabet();
		tokens.turnOnCounts();
		tokens.allowGrowth();

		String featureDictionary = "/afs/inf.ed.ac.uk/group/project/vsm/worddictionary/worddictionary.ser";

		/*
		 * Getting the serialised dictionary bean object that contains the
		 * inside and outside feature dictionaries which are used to form the
		 * feature vectors
		 */
		VSMWordDictionaryBean dictionaryBean = VSMReadSerialWordDict
				.readSerializedDictionary(featureDictionary);

		Alphabet wordDictionary = dictionaryBean.getWordDictionary();

		/*
		 * Data structure to hold the count map. Count map is very essential to
		 * create the proper directory structure in which the feature vectors
		 * are stored
		 */
		String parsedTreeCorpus = "/afs/inf.ed.ac.uk/group/project/vsm/trees/";

		File[] files = new File(parsedTreeCorpus).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.isHidden();
			}
		});

		/*
		 * Getting all the available tree files
		 */
		ArrayList<String> treePaths = VSMUtil.getFilePaths(files);
		int countTree = 0;
		for (String treesPath : treePaths) {

			countTree++;

			/*
			 * Getting an iterator over the trees in the file
			 */
			PennTreeReader treeReader = VSMUtil.getTreeReader(treesPath);

			/*
			 * Iterating over all the trees
			 */
			while (treeReader.hasNext()) {

				/*
				 * The syntax tree
				 */
				Tree<String> syntaxTree = null;

				/*
				 * Unmatched parentheses exception. Does this mean that the
				 * BLLIP corpus sometimes does not have correct parse trees?
				 * Strange
				 */
				try {
					syntaxTree = treeReader.next();
				} catch (RuntimeException e) {
					System.out.println("exception" + e + " ::tree  "
							+ syntaxTree);
				}

				/*
				 * Do stuff only if the syntax tree is a valid one
				 */
				if (syntaxTree != null) {

					/*
					 * Process the syntax tree to remove the top bracket
					 */
					syntaxTree = treeNormalizer.process(syntaxTree);

					/*
					 * Iterator over the nodes of the tree
					 */
					Iterator<Tree<String>> nodeTrees = syntaxTree.iterator();

					/*
					 * Extracting the constituents of a syntax tree
					 */
					Map<Tree<String>, Constituent<String>> constituentsMap = syntaxTree
							.getConstituents();

					/*
					 * Iterating over all the nodes in a particular syntax tree
					 */

					Tree<String> insideTree = null;

					while (nodeTrees.hasNext()) {

						/*
						 * This is the inside tree for which we want to form a
						 * feature vector and store it in the map
						 */
						insideTree = nodeTrees.next();

						/*
						 * The token for which we want to find the embedding
						 */

						String token = insideTree.getTerminalYield().get(0);

						/*
						 * Removing the stop words
						 */

						if (insideTree.isPreTerminal()
								&& Stopwords.isStopword(token)) {

							/*
							 * Forming the token dictionary so that we can get
							 * the count map
							 */
							tokens.lookupIndex(token);

							System.out.println("***Token***" + token);

							VSMWordEmbeddingSem vectorBeanSem = new VSMWordEmbeddingSem();

							/*
							 * Getting the lower dimensional embedding for the
							 * inside feature vector
							 */
							System.out
									.println("***Getting the projection matrices For***"
											+ insideTree.getLabel());

							DenseVector psiEmbedded = null;
							DenseVector phiEmbedded = null;

							VSMWordFeatureVectorBean vectorBean = new VSMWordFeatureVectorBean();

							/*
							 * Getting the Semantic projection matrices for the
							 * pre - terminal
							 */
							matrices = VSMUtil
									.deserializeCCAVariantsRunSem(insideTree
											.getLabel());
							if (matrices != null) {
								/*
								 * Now getting the vectors
								 */

								SparseVector psi = new VSMOutsideFeatureVectorWords()
										.getOutsideFeatureVectorPsi(syntaxTree,
												insideTree, wordDictionary,
												vectorBean);

								SparseVector phi = new VSMInsideFeatureVectorWords()
										.getInsideFeatureVectorPhi(insideTree,
												wordDictionary, vectorBean);

								/*
								 * Inside Projection Matrix
								 */
								if (matrices[0] != null) {
									Matrix Y = (Matrix) matrices[0];

									/*
									 * Dense Matrix that holds YT
									 */
									DenseMatrix YT = new DenseMatrix(
											Y.getColumnDimension(),
											Y.getRowDimension());

									/*
									 * Getting the MTJ Matrix
									 */
									DenseMatrix YMTJ = VSMUtil
											.createDenseMatrixMTJ(Y);

									Y = null;

									/*
									 * Transform
									 */
									YMTJ.transpose(YT);
									YMTJ = null;

									/*
									 * Converting the sparse vector into dense
									 * vector
									 */
									Vector phiDense = new DenseVector(
											wordDictionary.size());

									/*
									 * Iterator over the sparse vector MTJ
									 */
									java.util.Iterator<VectorEntry> sparseVecItr = phi
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
										phiDense.add(idx, val);
									}

									/*
									 * Multiply the matrix and the vector, to
									 * get the lower dimensional embedding
									 */
									phiEmbedded = new DenseVector(YT.numRows());

									/*
									 * Form the embeddings
									 */

									/*
									 * Forming the embedding
									 */
									YT.mult(phiDense, phiEmbedded);

								}

								if (matrices[1] != null) {
									/*
									 * Inside Projection Matrix
									 */
									Matrix Z = (Matrix) matrices[1];

									/*
									 * Dense Matrix that holds YT
									 */
									DenseMatrix ZT = new DenseMatrix(
											Z.getColumnDimension(),
											Z.getRowDimension());

									/*
									 * Getting the MTJ Matrix
									 */
									DenseMatrix ZMTJ = VSMUtil
											.createDenseMatrixMTJ(Z);

									Z = null;

									/*
									 * Transform
									 */
									ZMTJ.transpose(ZT);
									ZMTJ = null;

									/*
									 * Converting the sparse vector into dense
									 * vector
									 */
									Vector psiDense = new DenseVector(
											wordDictionary.size());

									/*
									 * Iterator over the sparse vector MTJ
									 */
									java.util.Iterator<VectorEntry> sparseVecItrOut = psi
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
										psiDense.add(idx, val);
									}

									/*
									 * Multiply the matrix and the vector, to
									 * get the continuous representation
									 */
									psiEmbedded = new DenseVector(ZT.numRows());
									// Vector continousRepAvg = new
									// DenseVector(YT.numRows());

									/*
									 * Forming the continuous representation
									 */
									ZT.mult(psiDense, psiEmbedded);
								}

								/*
								 * Concatenating the two vectors, inside and
								 * outside
								 */
								DenseVector wordEmbeddingSem = new DenseVector(
										2 * psiEmbedded.size());
								Iterator<VectorEntry> psiItr = psiEmbedded
										.iterator();
								Iterator<VectorEntry> phiItr = phiEmbedded
										.iterator();

								int count = 0;
								while (psiItr.hasNext()) {

									VectorEntry e = psiItr.next();
									wordEmbeddingSem.add(count, e.get());
									count++;

								}

								while (phiItr.hasNext()) {
									VectorEntry e = phiItr.next();
									wordEmbeddingSem.add(count, e.get());

									count++;
								}

								System.out.println("***word embedding size"
										+ wordEmbeddingSem.size());
								// wordEmbeddingSem = (DenseVector) psiEmbedded
								// .add(phiEmbedded);
								// /*
								// * Getting the word embedding semantic
								// */
								// wordEmbeddingSem = wordEmbeddingSem
								// .scale((double) 1 / (double) 2);

								vectorBeanSem
										.setWordEmbeddingSem(wordEmbeddingSem);

								/*
								 * Serialize this wordEmbedding to be later
								 * averaged with the syntactic embedding
								 */

								VSMSerializeSemWordEmbedding
										.serializeVectorBean(vectorBeanSem,
												token, insideTree.getLabel(),
												tokens.countMap.get(token));

							}
						}

					}
				}

			}

			if (countTree == 1) {
			}
		}
	}
}
