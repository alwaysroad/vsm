package VSMInterfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;

import VSMUtilityClasses.Alphabet;

/**
 * This interface is implemented both by the class InsideFeatureDictionary and
 * the class OutsideFeatureDictionary
 * 
 * @author sameerkhurana10
 *
 */

public interface InsideFeatureDictionary {

	/**
	 * Get the feature dictionaries by looping through all the feature objects
	 * in the store and calling the method getFeatureDictionary on each feature
	 * object
	 * 
	 * @param insideFeatureObjStore
	 *            - the store that contains all the registered feature objects
	 * @return - dictionary list containing all the dictionaries
	 */
	ArrayList<Alphabet> getInsideFeatureDictionaryList(
			ArrayList<InsideFeatureObject> insideFeatureObjStore);

	/**
	 * Get the filtered feature dictionaries by looping through all the feature
	 * objects in the store and calling the method getFilteredDictionary on all
	 * the objects
	 * 
	 * @param insideFeatureObjStore
	 * @return
	 */
	ArrayList<Alphabet> getFilteredFeatureDictionaryList(
			ArrayList<InsideFeatureObject> insideFeatureObjStore);

	/**
	 * The function updates the dictionary i.e. extracts the inside features and
	 * stores them in the already existing dictionary, that we get using the
	 * getInsideFeatureDictionaryMethod
	 * 
	 * @param URI
	 * @throws Exception
	 */
	public void updateObjectStore(String URI) throws Exception;

	/**
	 * The method is used to return the the object store variable
	 * 
	 * @return - the list of feature objects that registered themselves with the
	 *         store
	 */
	ArrayList<InsideFeatureObject> getInsideFeatureObjectStore();

	/**
	 * Filters the inside feature dictionary by thresholding the feature counts.
	 * If the feature counts in a dictionary are less than the threshold then we
	 * remove those features and instead place a class NOTSOFREQUENT instead of
	 * that feature
	 * 
	 * @param insideFeatureDictionary
	 * @return
	 */
	ArrayList<Alphabet> filterInsideFeatureDictionary(
			ArrayList<Alphabet> insideFeatureDictionaryList,
			ArrayList<Alphabet> filteredFeatureDictionaryList);

	/**
	 * The method is called after the feature dictionary is created. This method
	 * should be called in order to not add any new features in the dictionary
	 * when Alphabet's lookupindex method is called. It is very important to
	 * call this method otherwise there can be a mismatch in the inside feature
	 * vector dimensions when the inside feature vector ois created using this
	 * dictionary. This method loops over the feature dictionaries and call the
	 * stopGrowth method on each feature dictionary
	 * 
	 * @param insideFeatureDictionaryList
	 *            - the created feature list
	 */
	void stopDictionaryGrowth(ArrayList<Alphabet> insideFeatureDictionaryList);
	
	/**
	 * 
	 * @return
	 */
	ArrayList<InsideFeatureObject> getUpdatedObjectStore();
	
	/**
	 * The method is called after the feature dictionary is created. This method
	 * should be called in order to not add any new features in the dictionary
	 * when Alphabet's lookupindex method is called. It is very important to
	 * call this method otherwise there can be a mismatch in the inside feature
	 * vector dimensions when the inside feature vector ois created using this
	 * dictionary. This method loops over the feature dictionaries and call the
	 * stopGrowth method on each feature dictionary
	 * 
	 * @param insideFeatureDictionaryList
	 *            - the created feature list
	 */
	void allowDictionaryGrowth(ArrayList<Alphabet> insideFeatureDictionaryList);

}
