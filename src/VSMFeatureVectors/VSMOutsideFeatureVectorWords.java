package VSMFeatureVectors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import weka.core.Stopwords;
import no.uib.cipr.matrix.sparse.SparseVector;
import VSMConstants.VSMContant;
import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureVector;
import VSMInterfaces.OutsideFeatureVectorWord;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class VSMOutsideFeatureVectorWords implements OutsideFeatureVectorWord {

	@Override
	public SparseVector getOutsideFeatureVectorPsi(Tree<String> syntaxTree,
			Tree<String> insideTree, Alphabet wordDictionary,
			VSMWordFeatureVectorBean vectorBean) {

		/*
		 * Getting the vector dimension, i.e. the size of the dictionary
		 */
		int vectorDimensions = VSMUtil.getWordDictionarySize(wordDictionary);

		/*
		 * Storing the vector dimensions in the vector bean object
		 */
		vectorBean.setVectorDimensions(vectorDimensions);

		/*
		 * Getting the outside words that will be used to form a feature vector
		 */
		List<Tree<String>> allTerminals = syntaxTree.getTerminals();

		List<Tree<String>> insideTerminalList = insideTree.getTerminals();

		ArrayList<String> allWordsList = new ArrayList<String>();
		ArrayList<String> insideWordsList = new ArrayList<String>();
		ArrayList<String> outsideWordList = new ArrayList<String>();
		/*
		 * The outside words
		 */
		for (Tree<String> terminal : allTerminals) {
			allWordsList.add(terminal.toString());
		}

		/*
		 * The inside words
		 */
		for (Tree<String> insideTerminal : insideTerminalList) {
			insideWordsList.add(insideTerminal.toString());
		}

		/*
		 * This is the actual outside words when we remove the inside words
		 */
		allWordsList.removeAll(insideWordsList);

		outsideWordList = allWordsList;

		/*
		 * Mormalizing and lowercasing
		 */
		VSMUtil.lowercase(outsideWordList);
		VSMUtil.normalize(outsideWordList);

		/*
		 * Ignoring stop words and non alphanumerics
		 */
		Iterator<String> outsideWordListItr = outsideWordList.iterator();
		while (outsideWordListItr.hasNext()) {
			String word = outsideWordListItr.next();
			if (Stopwords.isStopword(word)) {
				outsideWordListItr.remove();
			}

			if (!StringUtils.isAlphanumeric(word)) {
				try {
					if (!Character.isLetterOrDigit(word.charAt(0))) {
						outsideWordListItr.remove();
					}
				} catch (StringIndexOutOfBoundsException e) {
					System.out
							.println("**Catching the Exception and moving on***"
									+ e);
				}
			}
		}

		if (!outsideWordList.isEmpty()) {
			/*
			 * Declaring the sparse vector
			 */
			SparseVector psi = new VSMSparseVector(vectorDimensions);

			/*
			 * Declaring the data structures that are updated and passed to the
			 * contructor of the class SparseVector which is provided by the
			 * Mallet Machine Learning toolbox
			 */
			// int[] indices = new int[vectorDimensions];
			// double[] values = new double[vectorDimensions];

			/*
			 * The data structure that stores the features that are extracted to
			 * form the feature vector. We store the below given data structure
			 * in the vector bean just for our record
			 */
			ArrayList<String> outsideFeatures = new ArrayList<String>();

			/*
			 * Declaring some variables. Nothing fancy here!
			 */
			String feature = null;
			int vocabularyIndex = 0;
			ArrayList<Integer> alreadyPresent = new ArrayList<Integer>();

			/*
			 * Iterating over the outside word list, to get the word indices in
			 * the list
			 */
			for (int i = 0; i < outsideWordList.size(); i++) {

				/*
				 * Getting the outside word
				 */
				feature = outsideWordList.get(i);

				/*
				 * Adding the outside words in a list just for record
				 */
				outsideFeatures.add(feature);

				/*
				 * Extracting the vocabulary index of the word
				 */
				vocabularyIndex = VSMUtil
						.getVocabIndex(wordDictionary, feature);

				/*
				 * TODO Scaling. Just a binary feature vector for now
				 */
				/*
				 * Very important if condition in order to form a binary feature
				 * vector. Ensures that there are no duplicates, otherwise they
				 * get counted
				 */
				if (!alreadyPresent.contains(vocabularyIndex)) {
					// indices[i] = vocabularyIndex;
					// values[i] = 1.0;
					psi.add(vocabularyIndex, 1.0);
					alreadyPresent.add(vocabularyIndex);
				}
			}

			/*
			 * Removing duplicates if we just want a binary feature vector
			 */
			// indices = VSMUtil.removeDuplicates(indices);

			/*
			 * Forming the sparse vector
			 */
			// psi = new SparseVector(indices, values);

			/*
			 * Storing the information about the extracted features in the
			 * vector bean
			 */
			vectorBean.setOutsideTreeFeatureList(outsideFeatures);

			return psi;
		} else {
			return null;
		}

	}
}
