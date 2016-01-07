package VSMInterfaces;

import java.util.ArrayList;

import VSMSerialization.VSMDictionaryBean;
import VSMUtilityClasses.Alphabet;

public interface ExtractDictionary {
	/**
	 * The method returns a MultiMap containing all the feature vectors
	 * corresponding to all the non-terminals that are present in the syntax
	 * tree file given by the following URI
	 * 
	 * @param URI
	 * @return
	 * @throws Exception
	 */
	// ArrayList<Alphabet> getFeatureMatrix(String URI) throws Exception;

	/**
	 * The method that does feature extraction and stores the features in the
	 * inside/outside feature dictionary and returns the updated feature
	 * dictionary
	 * 
	 * @param treeFilePaths
	 *            - The root directory of the trees corpus, that has all the
	 *            trees from which we have to extract the features
	 * @return - the updated feature dictionary, after storing all the extracted
	 *         features in the inside dictionary
	 * @throws Exception
	 */
	// ArrayList<Alphabet> getUpdatedFeatureDictionary(String URI)
	// throws Exception;

	ArrayList<Alphabet> getUpdatedFeatureDictionary(ArrayList<String> treeFilePaths,
			VSMDictionaryBean alreadyExistingDictionaryBean) throws Exception;

}
