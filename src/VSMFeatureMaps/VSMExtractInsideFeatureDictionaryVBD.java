package VSMFeatureMaps;

import java.io.File;
import java.util.ArrayList;

import VSMFeatureDictionary.VSMInsideFeatureDictionaryVBD;
import VSMInterfaces.ExtractDictionary;
import VSMInterfaces.InsideFeatureDictionary;
import VSMInterfaces.InsideFeatureObject;
import VSMSerialization.VSMDictionaryBean;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;

/**
 * The class is developed to generate a MultiMap that stores all the inside
 * feature vectors corresponding to all the non terminals in a gives parse tree
 * corpus
 * 
 * @author sameerkhurana10
 *
 */
public class VSMExtractInsideFeatureDictionaryVBD implements ExtractDictionary {

	private InsideFeatureDictionary insideFeatureDictionary;

	/*
	 * Instantiating the dictionary, i.e. creating the initial object store.
	 * Hence when we make the object of this class, an object store is
	 * instantiated through the constructor of VSMInsideFeatureDictionary class
	 */
	public VSMExtractInsideFeatureDictionaryVBD() {

		/*
		 * Instantiating the inside feature dictionary with no features yet.
		 */
		insideFeatureDictionary = new VSMInsideFeatureDictionaryVBD();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.FeatureExtraction#getUpdatedFeatureDictionary(java.lang
	 * .String)
	 * 
	 * The method to extract the inside features and store them in the inside
	 * feature dictionary that was instantiated in the constructor and then
	 * return the updated dictionary
	 */
	@Override
	public ArrayList<Alphabet> getUpdatedFeatureDictionary(
			ArrayList<String> treeFilePaths,
			VSMDictionaryBean alreadyExistingDictionaryBean) throws Exception {

		/*
		 * Getting the initial object store. We will use the object store to
		 * update the inside feature dictionary.
		 */
		ArrayList<InsideFeatureObject> initialObjectStore = insideFeatureDictionary
				.getInsideFeatureObjectStore();

		/*
		 * Declaring some data structures
		 */
		ArrayList<InsideFeatureObject> updatedObjectStore = null;
		ArrayList<Alphabet> updatedInsideFeatureDictionary = null;
		ArrayList<Alphabet> initialFilteredDictionary = null;
		ArrayList<Alphabet> filteredInsideFeatureDictionary = null;

		int fileCount = 0;

		/*
		 * Extracting the inside features corresponding to each node in the
		 * parse trees contained in each file in the trees corpus
		 */
		System.out.println("***Extracting inside feature dictionary***");
		for (String treeFilePath : treeFilePaths) {
			/*
			 * If the treesfile count increases 400 then this is the end
			 */
			if (fileCount < 100000) {
				fileCount++;
				System.out.println("**File Count**" + fileCount);
				/*
				 * The below call to a method will update the object store,
				 * which is to say that we are updating the inside feature
				 * dictionary. The below method extracts the features and stores
				 * them in the initially empty inside feature dictionary
				 */
				insideFeatureDictionary.updateObjectStore(treeFilePath);

				/*
				 * Getting the updated object store, which will be used to now
				 * extract the updated dictionaries
				 */
				updatedObjectStore = insideFeatureDictionary
						.getUpdatedObjectStore();

				/*
				 * So here are the update inside dictionary. Which is unfiltered
				 * i.e. contains the infrequent features as well
				 */
				updatedInsideFeatureDictionary = insideFeatureDictionary
						.getInsideFeatureDictionaryList(updatedObjectStore);

				/*
				 * Now we need to filter the inside feature dictionary, i.e. we
				 * want to assign a class NOTFREQUENT to all the features that
				 * occur less than a particular threshhold count. Below is just
				 * an empty data structure that will be used to hold the
				 * filtered inside feature dictionary
				 */
				if (alreadyExistingDictionaryBean == null) {
					initialFilteredDictionary = insideFeatureDictionary
							.getFilteredFeatureDictionaryList(initialObjectStore);
				} else {
					initialFilteredDictionary = alreadyExistingDictionaryBean
							.getInsideFeatureDictionary();
					/*
					 * We need to allow the dictionary growth of the already
					 * existing dictionary because it had been stopped earlier
					 */
					insideFeatureDictionary
							.allowDictionaryGrowth(initialFilteredDictionary);
					System.out.println("Dictionary already exist dude!**");
				}
				/*
				 * The below method filters the inside feature dictionary and
				 * returns the filtered dictionary
				 */
				filteredInsideFeatureDictionary = insideFeatureDictionary
						.filterInsideFeatureDictionary(
								updatedInsideFeatureDictionary,
								initialFilteredDictionary);
			}
		}

		/*
		 * Stopping the dictionary growths before they can be used for feature
		 * extraction otherwise new features would be added to the dictionaries
		 * when we call the lookup index method
		 */
		insideFeatureDictionary
				.stopDictionaryGrowth(updatedInsideFeatureDictionary);
		insideFeatureDictionary
				.stopDictionaryGrowth(filteredInsideFeatureDictionary);

		/*
		 * Returning the filtered dictionary
		 */
		return filteredInsideFeatureDictionary;
	}
}
