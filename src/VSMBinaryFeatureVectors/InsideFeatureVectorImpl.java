package VSMBinaryFeatureVectors;

import java.util.ArrayList;

import no.uib.cipr.matrix.sparse.SparseVector;

import VSMFeatureDictionary.VSMInsideFeatureDictionaryAUX;
import VSMInterfaces.InsideFeatureObject;

import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class InsideFeatureVectorImpl implements InsideFeatureVector {

	@Override
	public SparseVector getInsideFeatureVectorPhi(Tree<String> insideTree,
			ArrayList<Alphabet> filteredDcitionary, FeatureVectorBean vectorBean) {

		/*
		 * Getting an objectStore to iterate over
		 */
		ArrayList<InsideFeatureObject> objectStore = new VSMInsideFeatureDictionaryAUX()
				.getInsideFeatureObjectStore();

		/*
		 * Getting the inside vector dimensions
		 */
		int vectorDimension = VSMUtil
				.getInsideFeatureVectorDimensions(filteredDcitionary);

		/*
		 * Storing the information about the inside feature vector dimensions in
		 * the vector bean
		 */
		vectorBean.setInsideFeatureVectorDim(vectorDimension);

		/*
		 * Initializing the inside feature vector with all zeros TODO, have a
		 * sparse vector instead
		 */
		SparseVector phi = new SparseVector(vectorDimension);

		/*
		 * Declaring data structures that hold information that is used to
		 * create the sparse vector
		 */
		// int[] indices = new int[vectorDimension];
		// double[] values = new double[vectorDimension];

		/*
		 * Data structure to hold the inside features
		 */
		ArrayList<String> insideTreeFeatureList = new ArrayList<String>();

		/*
		 * Declaring some variables
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
			 * Getting the inside feature for the passed inside tree
			 */
			feature = objectStore.get(i).getInsideFeature(insideTree,
					insideTree.isPreTerminal());

			/*
			 * Feature should not be equal to NOTVALID, because we do not store
			 * not valid in the dictionary
			 */
			if (!feature.equals("NOTVALID")) {

				/*
				 * Storing the inside feature in the list
				 */
				insideTreeFeatureList.add(feature);

				/*
				 * Index of the feature in the dictionary
				 */
				featureID = VSMUtil.getFeatureId(filteredDcitionary.get(i),
						feature);

				/*
				 * Updating the variables
				 */
				int index = startIndex + featureID;
				// indices[i] = index;
				// values[i] = 1.0;
				/*
				 * TODO Scaling. Just a binary feature vector for now
				 */
				// phi[startIndex + featureID] = 1.0;

				/*
				 * Forming the binary feature vector
				 */
				phi.add(index, 1.0);
			}

			/*
			 * Updating the start index
			 */
			startIndex += filteredDcitionary.get(i).size();

		}

		/*
		 * Creating the sparse vector
		 */
		// phi = new SparseVector(indices, values);

		/*
		 * Storing the extracted inside and outside features in the feature
		 * vector bean (which is serialized)
		 */
		vectorBean.setInsideTreeFeatureList(insideTreeFeatureList);

		return phi;
	}
}
