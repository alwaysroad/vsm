package VSMSemanticBinaryFeatureVectors;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMSerialization.VSMCountMap;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialCountMap;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMSerializeCountMap;
import VSMSerialization.VSMSerializeFeatureVectorBean;
import VSMSerialization.VSMSerializeFeatureVectorBeanWord;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import cc.mallet.types.SparseVector;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

/**
 * The class generates the feature vectors given an input inside and outside
 * feature dictionary for each non-terminal in a corpus of parse trees. The
 * inside and outside feature vectors are stored serialized so that we do not
 * have to create the vectors again and can use them whenever we want by
 * deserialising the object. We store sparse feature vectors. These feature
 * vectors will be used to learn the linear transforms corresponding to each
 * non-terminal in Matlab. The feature vectors are binary and not scaled for
 * now. Read about the scaling in NAACL2013 and then implement scaling if
 * required
 * 
 * @author sameerkhurana10
 *
 */

public class VSMFeatureVectorsWordDT {

	public static void main(String... args) throws Exception {

		/*
		 * Used to normalize the trees
		 */

		PTBTreeNormaliser treeNormalizer = new PTBTreeNormaliser(true);

		/*
		 * Data structure to hold the count map. Count map is very essential to
		 * create the proper directory structure in which the feature vectors
		 * are stored
		 */
		LinkedHashMap<String, Integer> countMap = null;

		/*
		 * Declaring the variable that will refer to the file that holds the
		 * serialized count map object
		 */
		String countMapLoc = null;

		/*
		 * Getting the feature dictionary path, i.e. the serialized file path.
		 * This dictionary will be used to form the feature vectors.
		 */
		String featureDictionary = null;

		/*
		 * This variable tells the code about the directory path where parse
		 * trees are stored from which feature vectors need to be extracted
		 * corresponding to all the nodes
		 */
		String parsedTreeCorpus = null;

		/*
		 * Reading some variables from the arguments given at the commandline.
		 * If they are available
		 */
		if (args.length > 2) {
			featureDictionary = args[0];
			parsedTreeCorpus = args[1];
			countMapLoc = args[2];
		} else {
			/*
			 * The feature dictionary that needs to be used while extracting
			 * features
			 */
			featureDictionary = "/afs/inf.ed.ac.uk/group/project/vsm/worddictionary/worddictionary.ser";
			/*
			 * The directory that holds the parse trees that are iterated over
			 * to extract the feature vector corresponding to the nodes
			 */
			parsedTreeCorpus = "/afs/inf.ed.ac.uk/group/project/vsm/trees";
			/*
			 * Necessary to get the appropriate directory structure
			 */
			// countMapLoc =
			// "/afs/inf.ed.ac.uk/group/project/vsm/countmapnodesamples/countMap.ser";
		}

		/*
		 * Getting the serialised dictionary bean object that contains the
		 * inside and outside feature dictionaries which are used to form the
		 * feature vectors
		 */
		VSMWordDictionaryBean dictionaryBean = VSMReadSerialWordDict
				.readSerializedDictionary(featureDictionary);

		/*
		 * Getting the inside and outside feature dictionaries, that are used
		 * for forming the feature vectors
		 */
		System.out.println("***Getting word dictionary*****");
		Alphabet wordDictionary = dictionaryBean.getWordDictionary();
		System.out.println(wordDictionary);
		// System.out.println(wordDictionary.size());

		/*
		 * The parsed tree corpus from where the feature vectors need to be
		 * extracted corresponding to all the nodes
		 */
		File[] files = new File(parsedTreeCorpus).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return !file.isHidden();
			}
		});

		ArrayList<String> filePaths = VSMUtil.getFilePaths(files);

		/*
		 * The obect that is used to serialize the feature vector bean. The
		 * feature vector bean storing the inside and outside feature vectors
		 * corresponding to a particular node in a tree. Each feature vector
		 * bean holds the feature vectors for one particular node
		 */
		VSMSerializeFeatureVectorBeanWord serializeBean = null;

		/*
		 * If we already have a serialized count map object then we would want
		 * to start from where we left
		 */
		// File fileCountMap = new File(countMapLoc);

		serializeBean = new VSMSerializeFeatureVectorBeanWord();
		// } else {
		// VSMCountMap countMapObj = VSMReadSerialCountMap
		// .readCountMapObj(countMapLoc);
		// System.out.println("inside the count map***");
		// serializeBean = new VSMSerializeFeatureVectorBeanWord(
		// countMapObj.getCountMap());
		// }

		/*
		 * Iterating over the corpus and extracting and serializing the feature
		 * vectors corresponding to each node. Note that we are creating sparse
		 * vectors for obvious reasons. Sorry for the long loop, TODO Make the
		 * code more modular
		 */

		for (String filePath : filePaths) {

			/*
			 * Getting an iterator over the trees in the file
			 */
			PennTreeReader treeReader = VSMUtil.getTreeReader(filePath);

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
					 * Extracting the constituents of a syntax tree
					 */
					Map<Tree<String>, Constituent<String>> constituentsMap = syntaxTree
							.getConstituents();

					/*
					 * Iterator over the nodes of the tree
					 */
					Iterator<Tree<String>> nodeTrees = syntaxTree.iterator();

					/*
					 * Sparse Inside and outside feature vectors declared
					 */
					no.uib.cipr.matrix.sparse.SparseVector psi = null;
					no.uib.cipr.matrix.sparse.SparseVector phi = null;
					Tree<String> insideTree = null;

					/*
					 * Iterating over all the nodes in a particular syntax tree
					 */
					while (nodeTrees.hasNext()) {

						/*
						 * This is the inside tree for which we want to form a
						 * feature vector and store it in the map
						 */
						insideTree = nodeTrees.next();

						/*
						 * Only do stuff if inside tree is not a leaf
						 */
						if (!insideTree.isLeaf()
								&& insideTree.getLabel().equalsIgnoreCase("DT")) {

							/*
							 * Setting the object's properties that are stored
							 * in the .ser file
							 */
							VSMWordFeatureVectorBean vectorBean = new VSMWordFeatureVectorBean();

							System.out
									.println("****Extracting inside and outside feature vectors for node****  "
											+ insideTree.getLabel());

							/*
							 * Getting the inside and outside feature vectors
							 * corresponding to the partcular node
							 */

							psi = new VSMOutsideFeatureVectorWords()
									.getOutsideFeatureVectorPsi(syntaxTree,
											insideTree, wordDictionary,
											vectorBean);

							phi = new VSMInsideFeatureVectorWords()
									.getInsideFeatureVectorPhi(insideTree,
											wordDictionary, vectorBean);
							System.out.println("got the sparse vectors*** "
									+ phi + psi);

							/*
							 * Storing the feature vectors in a bean which will
							 * be serialized for future use
							 */
							vectorBean.setPhi(phi);
							vectorBean.setPsi(psi);
							vectorBean.setInsideTree(insideTree);
							vectorBean.setLabel(insideTree.getLabel());
							vectorBean.setSyntaxTree(syntaxTree);

							/*
							 * Serialize the feature vector bean corresponding
							 * to the particular node. The feature vector bean
							 * contains the sparse inside and outside feature
							 * vectors
							 */
							serializeBean.serializeWordVectorBean(vectorBean);
							System.out
									.println("***Serialized the feature vector***");

						}

					}
				}
			}
		}

		/*
		 * We would also like to serialize the count map. The count map is the
		 * data structure that helps us store the .ser files in proper
		 * directories with proper names. So, if in future we want to extract
		 * feature vectors corresponding to more parse trees, we will start from
		 * where we left in the directory structure and file name
		 */

		/*
		 * Getting the updated count map
		 */
		countMap = VSMSerializeFeatureVectorBean.getCountMap();
		/*
		 * The object that will be serialized
		 */
		VSMCountMap countMapObject = new VSMCountMap();
		countMapObject.setCountMap(countMap);

		/*
		 * Serialize count map
		 */
		VSMSerializeCountMap.serializeCountMap(countMapObject);
		System.out.println("*****count map serialized****");
	}
}
