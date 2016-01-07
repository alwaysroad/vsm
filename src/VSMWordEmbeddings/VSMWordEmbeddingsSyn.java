package VSMWordEmbeddings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.SparseVector;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import Jama.Matrix;
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
import VSMSerialization.VSMWordEmbeddingSyn;
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

public class VSMWordEmbeddingsSyn {

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

		ArrayList<String> tokens = new ArrayList<String>();

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

		for (String treesPath : treePaths) {

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

						if (insideTree.isPreTerminal()
								&& !tokens.contains(token)) {

							/*
							 * This is the inside tree for which we want to form
							 * a feature vector and store it in the map
							 */
							insideTree = nodeTrees.next();

							/*
							 * Setting some static variables for the particular
							 * node. These are the features that are extracted
							 * and need to be updated brfore we extract them!. I
							 * know the below three lines don't make sense to
							 * you. I am sorry
							 */
							VSMUtil.setConstituentLength(constituentsMap
									.get(insideTree));
							VSMUtil.getNumberOfOutsideWordsLeft(insideTree,
									constituentsMap, syntaxTree);
							VSMUtil.getNumberOfOutsideWordsRight(insideTree,
									constituentsMap, syntaxTree);

							/*
							 * Creating the footoroot path for outside feature
							 * extraction. This is the stack of outside
							 * constituent trees corresponding to a node. We use
							 * this stack of tress to extract the outside
							 * features of course
							 */
							Stack<Tree<String>> foottoroot = new Stack<Tree<String>>();
							foottoroot = VSMUtil.updateFoottorootPath(
									foottoroot, syntaxTree, insideTree,
									constituentsMap);

							String featureDictionary = "/afs/inf.ed.ac.uk/group/project/vsm/featuredictionary/"
									+ insideTree.getLabel().toLowerCase()
									+ "/dictionary.ser";

							/*
							 * Getting the inside and outside feature
							 * dictionaries for the non-terminal AUX
							 */
							VSMDictionaryBean dictionaryBean = VSMReadSerialMatrix
									.readSerializedDictionary(featureDictionary);

							/*
							 * Getting the inside and outside feature
							 * dictionaries, that are used for forming the
							 * feature vectors
							 */
							ArrayList<Alphabet> outsideFeatureDictionary = dictionaryBean
									.getOutsideFeatureDictionary();
							ArrayList<Alphabet> insideFeatureDictionary = dictionaryBean
									.getInsideFeatureDictionary();

							VSMWordEmbeddingSyn vectorBeanSyn = new VSMWordEmbeddingSyn();

							/*
							 * Getting the lower dimensional embedding for the
							 * inside feature vector
							 */
							System.out
									.println("***Getting the projection matrices For***"
											+ insideTree.getLabel());

							DenseVector psiEmbedded = null;
							DenseVector phiEmbedded = null;

							VSMFeatureVectorBean vectorBean = new VSMFeatureVectorBean();

							tokens.add(token);

							/*
							 * Getting the Semantic projection matrices for the
							 * pre - terminal
							 */
							matrices = VSMUtil
									.deserializeCCAVariantsRun(insideTree
											.getLabel());

							/*
							 * Now getting the vectors
							 */

							SparseVector psi = new VSMOutsideFeatureVector()
									.getOutsideFeatureVectorPsi(foottoroot,
											outsideFeatureDictionary,
											vectorBean);

							SparseVector phi = new VSMInsideFeatureVector()
									.getInsideFeatureVectorPhi(insideTree,
											insideFeatureDictionary, vectorBean);

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
								Vector phiDense = new DenseVector(phi.size());

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
									 * Forming the dense inside feature vector
									 */
									phiDense.add(idx, val);
								}

								/*
								 * Multiply the matrix and the vector, to get
								 * the lower dimensional embedding
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
								Vector psiDense = new DenseVector(psi.size());

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
									 * Forming the dense inside feature vector
									 */
									psiDense.add(idx, val);
								}

								/*
								 * Multiply the matrix and the vector, to get
								 * the continuous representation
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
							 * Averaging the two vectors
							 */
							DenseVector wordEmbeddingSyn = new DenseVector(
									psiEmbedded.size());
							wordEmbeddingSyn = (DenseVector) psiEmbedded
									.add(phiEmbedded);
							/*
							 * Getting the word embedding semantic
							 */
							wordEmbeddingSyn = wordEmbeddingSyn
									.scale((double) 1 / (double) 2);

							vectorBeanSyn.setWordEmbeddingSem(wordEmbeddingSyn);

							/*
							 * Serialize this wordEmbedding to be later averaged
							 * with the syntactic embedding
							 */

							VSMSerializeSemWordEmbedding.serializeVectorBeanSyn(
									vectorBeanSyn, token);

						}

					}
				}

			}
		}
	}
}
