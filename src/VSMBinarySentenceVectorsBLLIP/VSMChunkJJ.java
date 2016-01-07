package VSMBinarySentenceVectorsBLLIP;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import no.uib.cipr.matrix.sparse.SparseVector;
import VSMConstants.VSMContant;
import VSMFeatureVectors.VSMInsideFeatureVector;
import VSMFeatureVectors.VSMInsideFeatureVectorWords;
import VSMFeatureVectors.VSMOutsideFeatureVector;
import VSMFeatureVectors.VSMOutsideFeatureVectorWords;
import VSMSerialization.VSMCountMap;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMReadSerialCountMap;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMReadSerialTreeCountObj;
import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMSerializeCountMap;
import VSMSerialization.VSMSerializeFeatureVectorBean;
import VSMSerialization.VSMSerializeFeatureVectorBeanTraining;
import VSMSerialization.VSMSerializeTreeCount;
import VSMSerialization.VSMTreeCount;
import VSMSerialization.VSMWordDictionaryBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
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

public class VSMChunkJJ {

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
		String treeCountObjLoc = null;

		/*
		 * Getting the feature dictionary path, i.e. the serialized file path.
		 * This dictionary will be used to form the feature vectors.
		 */
		String featureDictionarySyn = null;
		String featureDictionarySem = null;

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
			// featureDictionary = args[0];
			parsedTreeCorpus = args[1];
			treeCountObjLoc = args[2];
		} else {
			/*
			 * The feature dictionary that needs to be used while extracting
			 * features
			 */
			featureDictionarySyn = VSMContant.FEATURE_DICT_JJ;
			featureDictionarySem = VSMContant.WORD_DICT;
			/*
			 * The directory that holds the parse trees that are iterated over
			 * to extract the feature vector corresponding to the nodes
			 */
			parsedTreeCorpus = VSMContant.EXTRACTED_TREE_CORPUS;
			/*
			 * Necessary to get the appropriate directory structure
			 */
			// treeCountObjLoc =
			// "/afs/inf.ed.ac.uk/group/project/vsm/countmaptest/treecount.ser";
		}

		/*
		 * Getting the serialised dictionary bean object that contains the
		 * inside and outside feature dictionaries which are used to form the
		 * feature vectors
		 */

		VSMDictionaryBean dictionaryBean = VSMReadSerialMatrix
				.readSerializedDictionary(featureDictionarySyn);
		VSMWordDictionaryBean wordDictionaryBean = VSMReadSerialWordDict
				.readSerializedDictionary(featureDictionarySem);

		/*
		 * Getting the inside and outside feature dictionaries, that are used
		 * for forming the feature vectors
		 */
		ArrayList<Alphabet> outsideFeatureDictionary = dictionaryBean
				.getOutsideFeatureDictionary();
		ArrayList<Alphabet> insideFeatureDictionary = dictionaryBean
				.getInsideFeatureDictionary();

		Alphabet wordDictionary = wordDictionaryBean.getWordDictionary();

		/*
		 * The parsed tree corpus from where the feature vectors need to be
		 * extracted corresponding to all the nodes
		 */
		File[] trainingTreesFile = new File(parsedTreeCorpus)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						// TODO Auto-generated method stub
						return !file.isHidden();
					}
				});

		/*
		 * Got the 100000 trees
		 */
		ArrayList<String> filePaths = VSMUtil.getFilePaths(trainingTreesFile);

		/*
		 * The obect that is used to serialize the feature vector bean. The
		 * feature vector bean storing the inside and outside feature vectors
		 * corresponding to a particular node in a tree. Each feature vector
		 * bean holds the feature vectors for one particular node
		 */
		VSMSerializeFeatureVectorBeanTraining serializeBean = new VSMSerializeFeatureVectorBeanTraining();

		/*
		 * If we already have a serialized count map object then we would want
		 * to start from where we left
		 */
		// File fileCountMap = new File(treeCountObjLoc);
		//
		// if (!fileCountMap.exists()) {
		// serializeBean = new VSMSerializeFeatureVectorBeanTraining();
		//
		// } else {
		// // VSMTreeCount treeCountObj = VSMReadSerialTreeCountObj
		// // .redTreeCountObj(treeCountObjLoc);
		// // System.out.println("inside the count map***");
		// // serializeBean = new VSMSerializeFeatureVectorBeanTraining(
		// // treeCountObj.getTreeCount());
		// }

		/*
		 * Iterating over the corpus and extracting and serializing the feature
		 * vectors corresponding to each node. Note that we are creating sparse
		 * vectors for obvious reasons. Sorry for the long loop, TODO Make the
		 * code more modular
		 */

		/*
		 * Iterating over all the trees
		 */
		int treeCount = 0;
		for (String filePath : filePaths) {
			/*
			 * Getting an iterator over the trees in the file
			 */
			PennTreeReader treeReader = VSMUtil.getTreeReader(filePath);

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

					treeCount += 1;

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
					SparseVector psiSyn = null;
					SparseVector phiSyn = null;
					SparseVector psiSem = null;
					SparseVector phiSem = null;
					Tree<String> insideTree = null;

					/*
					 * Iterating over all the nodes in a particular syntax tree
					 */
					int nodeCount = 0;
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
								&& insideTree.getLabel().equalsIgnoreCase("JJ")) {

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

							nodeCount += 1;

							/*
							 * Setting the object's properties that are stored
							 * in the .ser file
							 */
							VSMFeatureVectorBean vectorBean = new VSMFeatureVectorBean();
							VSMWordFeatureVectorBean vectorBeanWord = new VSMWordFeatureVectorBean();

							System.out
									.println("****Extracting inside and outside feature vectors for node****  "
											+ insideTree.getLabel());

							/*
							 * Getting the inside and outside feature vectors
							 * corresponding to the partcular node
							 */
							psiSyn = new VSMOutsideFeatureVector()
									.getOutsideFeatureVectorPsi(foottoroot,
											outsideFeatureDictionary,
											vectorBean);
							phiSyn = new VSMInsideFeatureVector()
									.getInsideFeatureVectorPhi(insideTree,
											insideFeatureDictionary, vectorBean);
							psiSem = new VSMOutsideFeatureVectorWords()
									.getOutsideFeatureVectorPsi(syntaxTree,
											insideTree, wordDictionary,
											vectorBeanWord);
							phiSem = new VSMInsideFeatureVectorWords()
									.getInsideFeatureVectorPhi(insideTree,
											wordDictionary, vectorBeanWord);

							System.out.println("got the sparse vectors*** ");

							/*
							 * Storing the feature vectors in a bean which will
							 * be serialized for future use
							 */
							System.out.println("***Forming the Vector Bean***");
							vectorBean.setPhi(phiSyn);
							vectorBean.setPsi(psiSyn);
							vectorBean.setPhiSem(phiSem);
							vectorBean.setPsiSem(psiSem);

							vectorBean.setInsideTree(insideTree);
							vectorBean.setLabel(insideTree.getLabel());
							vectorBean.setSyntaxTree(syntaxTree);
							vectorBean.setFootToRoot(foottoroot);

							// vectorBeanWord.setInsideTree(insideTree);
							// vectorBeanWord.setLabel(insideTree.getLabel());
							// vectorBeanWord.setSyntaxTree(syntaxTree);

							System.out.println("***Entering serialization****");

							/*
							 * Serialize the feature vector bean corresponding
							 * to the particular node. The feature vector bean
							 * contains the sparse inside and outside feature
							 * vectors
							 */
							serializeBean.serializeVectorBeanBLLIP(vectorBean,
									treeCount, nodeCount);
							// serializeBean.serializeVectorBeanSem(
							// vectorBeanWord, treeCount, nodeCount);
							System.out
									.println("Serialized the feature vector***");

						}

					}
				}

				/*
				 * Taking a 100000 sentences
				 */
				if (treeCount == 50000) {
					break;
				}
			}

			/*
			 * We would also like to serialize the count map. The count map is
			 * the data structure that helps us store the .ser files in proper
			 * directories with proper names. So, if in future we want to
			 * extract feature vectors corresponding to more parse trees, we
			 * will start from where we left in the directory structure and file
			 * name
			 */

			/*
			 * Getting the tree count so that we can serialize it and start from
			 * where we left
			 */
			// treeCount = VSMSerializeFeatureVectorBeanTraining.getTreeCount();
			/*
			 * The object that will be serialized
			 */
			// VSMTreeCount treeCountObj = new VSMTreeCount();
			// treeCountObj.setTreeCount(treeCount);

			/*
			 * Serialize count map
			 */
			// VSMSerializeTreeCount.serializeCountMap(treeCountObj);
			// System.out.println("*****Tree Count Serialized****");

			System.out.println("***Done with chunk JJ****");
		}
	}
}
