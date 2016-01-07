package VSMFeatureVectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import weka.core.Stopwords;
import no.uib.cipr.matrix.sparse.SparseVector;
import VSMConstants.VSMContant;
import VSMInterfaces.InsideFeatureVectorWord;
import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureVector;
import VSMInterfaces.OutsideFeatureVectorWord;
import VSMSerialization.VSMFeatureVectorBean;
import VSMSerialization.VSMWordFeatureVectorBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class VSMInsideFeatureVectorWords implements InsideFeatureVectorWord {

	@Override
	public SparseVector getInsideFeatureVectorPhi(Tree<String> insideTree,
			Alphabet wordDictionary, VSMWordFeatureVectorBean vectorBean) {

		/*
		 * Getting the vector dimension, i.e. the size of the dictionary
		 */
		int vectorDimensions = VSMUtil.getWordDictionarySize(wordDictionary);

		/*
		 * Storing the vector dimensions in the vector bean object
		 */
		vectorBean.setVectorDimensions(vectorDimensions);

		/*
		 * The inside tree terminals
		 */
		List<Tree<String>> insideTerminalList = insideTree.getTerminals();

		/*
		 * Data structure Inside words list
		 */
		ArrayList<String> insideWordsList = new ArrayList<String>();

		/*
		 * The inside words
		 */
		for (Tree<String> insideTerminal : insideTerminalList) {
			insideWordsList.add(insideTerminal.toString());
		}

		VSMUtil.lowercase(insideWordsList);
		VSMUtil.normalize(insideWordsList);

		/*
		 * Removing the word if it is a stop word or punctuation
		 */
		Iterator<String> lsitItr = insideWordsList.iterator();
		while (lsitItr.hasNext()) {

			String word = lsitItr.next();
			
			if (Stopwords.isStopword(word)) {
				lsitItr.remove();
			}
			/*
			 * Removing the non-alphanumeric chars including punctuations and
			 * all
			 */

			if (!StringUtils.isAlphanumeric(word)) {
				try {
					if (!Character.isLetterOrDigit(word.charAt(0))) {
						lsitItr.remove();
					}
				} catch (StringIndexOutOfBoundsException e) {
					System.out
							.println("**Catching the Exception and moving on***"
									+ e);
				}
			}
		}

		if (!insideWordsList.isEmpty()) {
			/*
			 * Declaring the sparse vector
			 */
			SparseVector phi = new VSMSparseVector(vectorDimensions);

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
			ArrayList<String> insideFeatures = new ArrayList<String>();

			/*
			 * Declaring some variables. Nothing fancy here!
			 */
			String feature = null;
			int vocabularyIndex = 0;

			/*
			 * Data structure that helps us take care of the duplicate indexes
			 */
			ArrayList<Integer> alreadyPresent = new ArrayList<Integer>();

			/*
			 * Iterating over the outside word list, to get the word indices in
			 * the list
			 */
			for (int i = 0; i < insideWordsList.size(); i++) {

				/*
				 * Getting the outside feature corresponding to the particular
				 * outside stack of trees corresponding to a particular node
				 */
				feature = insideWordsList.get(i);

				/*
				 * Adding the outside words in a list just for record
				 */
				insideFeatures.add(feature);

				/*
				 * Extracting the vocabulary index of the word
				 */
				vocabularyIndex = VSMUtil
						.getVocabIndex(wordDictionary, feature);

				/*
				 * TODO Scaling. Just a binary feature vector for now
				 */
				if (!alreadyPresent.contains(vocabularyIndex)) {
					// indices[i] = vocabularyIndex;
					// values[i] = 1.0;
					phi.add(vocabularyIndex, 1.0);
					alreadyPresent.add(vocabularyIndex);
				}
			}

			// System.out.println(phi.get(1));
			/*
			 * Forming the sparse vector
			 */
			// Arrays.sort(indices);
			// phi = new SparseVector(indices, values);

			/*
			 * Storing the information about the extracted features in the
			 * vector bean
			 */
			vectorBean.setInsideTreeFeatureList(insideFeatures);

			return phi;

		} else {
			return null;
		}
	}
}
