package VSMTests;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import cc.mallet.types.SparseVector;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;
import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMSerialization.VSMCountMap;
import VSMSerialization.VSMDictionaryBean;

import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialCountMap;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMSerializeCountMap;
import VSMSerialization.VSMSerializeFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;

public class TestVSMFeatureVectorSerialization {

	@Test
	public void testFeatureVectorSerialization() throws Exception {

		// VSMFeatureMatrixBean matrixBean = VSMReadSerialMatrix
		// .readFeatureMatrix("/Users/sameerkhurana10/Documents/featurematrix/dictionary.ser");
		VSMDictionaryBean matrixBean = VSMReadSerialMatrix
				.readSerializedDictionary("/Users/sameerkhurana10/Documents/featurematrixtest/dictionary.ser");

		ArrayList<Alphabet> updateFilteredDcitionaryOutside = matrixBean
				.getOutsideFeatureDictionary();
		ArrayList<Alphabet> updatedFilteredDictionaryInside = matrixBean
				.getInsideFeatureDictionary();

		/*
		 * Getting all the tree files
		 */
		// File[] files = new File("/Users/sameerkhurana10/blipp_corpus/trees")
		// .listFiles();
		// File[] files = new File("/Users/sameerkhurana10/blipp_corpus/trees")
		// .listFiles();
		File[] files = new File("/Users/sameerkhurana10/blipp_corpus/testtrees")
				.listFiles();

		/*
		 * Getting the iterator over all the trees in the file specified by the
		 * URI
		 */

		VSMSerializeFeatureVectorBean serializeBean = new VSMSerializeFeatureVectorBean();

		Trees.StandardTreeNormalizer obj = new Trees.StandardTreeNormalizer();
		PTBTreeNormaliser treeNormalizer = new PTBTreeNormaliser(true);
		for (File file : files) {
			PennTreeReader treeReader = VSMUtil.getTreeReader(file
					.getAbsolutePath());
			/*
			 * Iterating over all the trees
			 */
			while (treeReader.hasNext()) {
				/*
				 * Get the tree
				 */
				Tree<String> syntaxTree = null;
				/*
				 * Unmatched parentheses exception
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
					 * Processed syntax tree
					 */
					syntaxTree = treeNormalizer.process(syntaxTree);

					Map<Tree<String>, Constituent<String>> constituentsMap = syntaxTree
							.getConstituents();
					/*
					 * Iterator over the nodes of the tree
					 */
					Iterator<Tree<String>> nodeTrees = syntaxTree.iterator();
					/*
					 * Iterating over all the nodes
					 */
					// double[] psi = null;
					// double[] phi = null;
					no.uib.cipr.matrix.sparse.SparseVector psi = null;
					no.uib.cipr.matrix.sparse.SparseVector phi = null;
					Tree<String> insideTree = null;
					while (nodeTrees.hasNext()) {
						/*
						 * This is the inside tree for which we want to form a
						 * feature vector and store it in the map
						 */
						insideTree = nodeTrees.next();
						System.out.println("****Serializing for node  "
								+ insideTree.getLabel());
						/*
						 * Setting some static variables for the particular node
						 * feature
						 */
						VSMUtil.setConstituentLength(constituentsMap
								.get(insideTree));
						VSMUtil.getNumberOfOutsideWordsLeft(insideTree,
								constituentsMap, syntaxTree);
						VSMUtil.getNumberOfOutsideWordsRight(insideTree,
								constituentsMap, syntaxTree);

						/*
						 * Creating the footoroot path for outside feature
						 * extraction
						 */
						Stack<Tree<String>> foottoroot = new Stack<Tree<String>>();
						foottoroot = VSMUtil.updateFoottorootPath(foottoroot,
								syntaxTree, insideTree, constituentsMap);

						/*
						 * Only do stuff if inside tree is not a leaf
						 */
						if (!insideTree.isLeaf()) {

							/*
							 * Setting the object's properties that are stored
							 * in the .ser file
							 */
							VSMFeatureVectorBean vectorBean = new VSMFeatureVectorBean();

							// System.out.println(":::::::" + insideTree);
							/*
							 * Getting the inside feature vector phi
							 */
							psi = new VSMOutsideFeatureVector()
									.getOutsideFeatureVectorPsi(foottoroot,
											updateFilteredDcitionaryOutside,
											vectorBean);
							System.out
									.println("got the outside feature vector** "
											+ psi);
							phi = new VSMInsideFeatureVector()
									.getInsideFeatureVectorPhi(insideTree,
											updatedFilteredDictionaryInside,
											vectorBean);
							System.out
									.println("got the outside feature vector*** "
											+ phi);

							/*
							 * THe inside feature vector //
							 */
							vectorBean.setPhi(phi);
							/*
							 * // * The outside feature vector //
							 */
							vectorBean.setPsi(psi);
							// /*
							// * The inside tree from which the inside feature
							// vector
							// * is extracted
							// */
							vectorBean.setInsideTree(insideTree);
							// /*
							// * The label of the node for which the inside and
							// * outside feature vectors are extracted
							// */
							vectorBean.setLabel(insideTree.getLabel());
							// /*
							// * The tree from which the inside and outside
							// feature
							// * vectors are extracted
							// */
							vectorBean.setSyntaxTree(syntaxTree);
							// /*
							// * Setting the outside constituent trees from
							// which
							// the
							// * outside feature vector is extracted
							// */
							vectorBean.setFootToRoot(foottoroot);
							// /*
							// * Read the count map from the file, if it not
							// null
							// then
							// * call the other constructor, otherwise call the
							// empty
							// * constructor
							// */
							// // String fileURI =
							// //
							// "/Users/sameerkhurana10/Documents/serialization/countMap.ser";
							// // File file = new File(fileURI);
							// // if (!file.exists()) {
							// // VSMSerializeFeatureVectorBean serializeBean =
							// new
							// // VSMSerializeFeatureVectorBean();
							// // serializeBean.serializePhiBean(vectorBean);
							// // System.out.println("****does not exist***");
							// // } else {
							// // LinkedHashMap<String, Integer> countMap =
							// // VSMReadSerialCountMap
							// // .readCountMapObj(fileURI).getCountMap();
							// // VSMSerializeFeatureVectorBean serializeBean =
							// new
							// // VSMSerializeFeatureVectorBean(
							// // countMap);
							System.out.println("****heer here****"
									+ vectorBean.getInsideFeatureVectorDim());
							System.out.println(vectorBean
									.getInsideTreeFeatureList());
							serializeBean.serializeVectorBean(vectorBean);
							System.out.println("****heer here****"
									+ vectorBean.getInsideFeatureVectorDim());
							System.out.println(vectorBean
									.getInsideTreeFeatureList());
							System.out
									.println("Serialized the feature vector***");
							// // }
							//
							// //
							// outsideFeatureMatrix.put(insideTree.getLabel(),
							// psi);
							// }
							// }
							// }
							// }
							//

						}

					}
				}
			}
		}

		// /*
		// * Serialize the count map
		// */
		LinkedHashMap<String, Integer> countMap = VSMSerializeFeatureVectorBean
				.getCountMap();
		// /*
		// * The object that will be serialized
		// */
		VSMCountMap countMapObject = new VSMCountMap();
		countMapObject.setCountMap(countMap);
		// /*
		// * Serialize count Map
		// */
		VSMSerializeCountMap.serializeCountMap(countMapObject);
		System.out.println("count map serialized");
		// /*
		// * Test the serialized object, read the object
		// */
		LinkedHashMap<String, Integer> countMapRetireved = VSMReadSerialCountMap
				.readCountMapObj(
						"/Users/sameerkhurana10/Documents/serialization/countMap.ser")
				.getCountMap();
		System.out.println(countMapRetireved);
	}
}
