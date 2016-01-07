package VSMBinaryFeatureVectors;

import java.util.ArrayList;
import java.util.Stack;

import no.uib.cipr.matrix.sparse.SparseVector;

import VSMFeatureDictionary.VSMOutsideFeatureDictionaryAUX;
import VSMInterfaces.OutsideFeatureObject;

import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideFeatureVectorImpl implements OutsideFeatureVector {

	@Override
	public SparseVector getOutsideFeatureVectorPsi(
			Stack<Tree<String>> foottoroot,
			ArrayList<Alphabet> filteredDcitionary, FeatureVectorBean vectorBean) {

		/*
		 * Getting the vector dimension
		 */
		int vectorDimensions = VSMUtil
				.getOutsideFeatureVectorDimensions(filteredDcitionary);

		/*
		 * Storing the vector dimensions in the vector bean object
		 */
		vectorBean.setOutsideFeatureVectorDim(vectorDimensions);

		/*
		 * Getting an objectStore to iterate over. It doesn't matter which
		 * feature dictionary we are using to get the object store because the
		 * object store is the same
		 */
		ArrayList<OutsideFeatureObject> objectStore = new VSMOutsideFeatureDictionaryAUX()
				.getOutsideFeatureObjectStore();

		/*
		 * Declaring the sparse vector
		 */
		SparseVector psi = new SparseVector(vectorDimensions);

		/*
		 * Declaring the data structures that are updated and passed to the
		 * contructor of the class SparseVector which is provided by the Mallet
		 * Machine Learning toolbox
		 */
		// int[] indices = new int[vectorDimensions];
		// double[] values = new double[vectorDimensions];

		/*
		 * The data structure that stores the features that are extracted to
		 * form the feature vector. We store the below given data structure in
		 * the vector bean just for our record
		 */
		ArrayList<String> outsideTreeFeatureList = new ArrayList<String>();

		/*
		 * Declaring some variables. Nothing fancy here!
		 */
		String feature = null;
		int featureID = 0;
		int startIndex = 0;

		/*
		 * Iterating over the object store, to extract the features from the
		 * given inside tree
		 */
		for (int i = 0; i < objectStore.size(); i++) {

			/*
			 * Getting the outside feature corresponding to the particular
			 * outside stack of trees corresponding to a particular node
			 */
			feature = objectStore.get(i).getOutsideFeature(foottoroot);

			/*
			 * Getting the featureID
			 */
			if (!feature.equals("NOTVALID")) {

				/*
				 * Adding the extracted feature to the list
				 */
				outsideTreeFeatureList.add(feature);

				/*
				 * Extracting the dictionary index of the feature from the
				 * outside feature dictionary
				 */
				featureID = VSMUtil.getFeatureId(filteredDcitionary.get(i),
						feature);

				/*
				 * TODO Scaling. Just a binary feature vector for now
				 */

				/*
				 * Getting the dictionary index
				 */
				int index = startIndex + featureID;

				/*
				 * Forming the sparse vector
				 */
				psi.add(index, 1.0);
				// indices[i] = index;
				// values[i] = 1.0;

			}
			startIndex += filteredDcitionary.get(i).size();
		}

		/*
		 * Forming the sparse vector
		 */
		// psi = new SparseVector(indices, values);

		/*
		 * Storing the information about the extracted features in the vector
		 * bean
		 */
		vectorBean.setOutsideTreeFeatureList(outsideTreeFeatureList);

		return psi;

	}
}
