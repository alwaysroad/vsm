package VSMFeatureMaps;

import java.io.File;
import java.util.ArrayList;

import VSMFeatureDictionary.VSMOutsideFeatureDictionaryAUX;
import VSMInterfaces.ExtractDictionary;
import VSMInterfaces.OutsideFeatureDictionary;
import VSMInterfaces.OutsideFeatureObject;
import VSMSerialization.VSMDictionaryBean;
import VSMUtilityClasses.Alphabet;

/**
 * The class is developed to generate a MultiMap that stores all the inside
 * feature vectors corresponding to all the non terminals in a gives parse tree
 * corpus
 * 
 * @author sameerkhurana10
 *
 */
public class VSMExtractOutsideFeatureDictionaryAUX implements ExtractDictionary {

	private OutsideFeatureDictionary outsideFeatureDictionary;

	public VSMExtractOutsideFeatureDictionaryAUX() {

		/*
		 * Instantiating the outside feature dictionary which is empty in the
		 * beginning
		 */
		outsideFeatureDictionary = new VSMOutsideFeatureDictionaryAUX();

	}

	@Override
	public ArrayList<Alphabet> getUpdatedFeatureDictionary(
			ArrayList<String> treeFilePaths,
			VSMDictionaryBean alreadyExistingDictionaryBean) throws Exception {
		/*
		 * Initial object store i.e. an empty outside feature dictionary
		 */
		ArrayList<OutsideFeatureObject> initialObjectStore = outsideFeatureDictionary
				.getOutsideFeatureObjectStore();

		/*
		 * Declaring some data structures
		 */
		ArrayList<OutsideFeatureObject> updatedObjectStore = null;
		ArrayList<Alphabet> updateOutsideFeatureDictionary = null;
		ArrayList<Alphabet> initialFilteredDictionaryList = null;
		ArrayList<Alphabet> filteredOutsideFeatureDictionary = null;

		int fileCount = 0;
		/*
		 * Extracting features corresponding to each node in the parse trees
		 * that are contained in the parse trees in the files
		 */
		System.out.println("****Extracting outside feature dictionary****");
		for (String treePath : treeFilePaths) {

			/*
			 * If the file count increases 400 then stop extracing features
			 */
			if (fileCount < 100000) {
				fileCount++;
				System.out.println("***File Count***" + fileCount);
				/*
				 * The below method extracts the outside features and stores
				 * them in the outside feature dictionary. In other words the
				 * method updates the empty object store
				 */
				outsideFeatureDictionary.updateObjectStore(treePath);

				/*
				 * Getting the updated object store so that we can extract the
				 * updated outside feature dictionary
				 */
				updatedObjectStore = outsideFeatureDictionary
						.getUpdatedObjectStore();

				/*
				 * Get the updated outside dictionary
				 */
				updateOutsideFeatureDictionary = outsideFeatureDictionary
						.getOutsideFeatureDictionaryList(updatedObjectStore);

				/*
				 * The below data structure is used to hold the filtered outside
				 * feature dictionary
				 */
				if (alreadyExistingDictionaryBean == null) {
					initialFilteredDictionaryList = outsideFeatureDictionary
							.getFilteredFeatureDictionaryList(initialObjectStore);
				} else {
					initialFilteredDictionaryList = alreadyExistingDictionaryBean
							.getOutsideFeatureDictionary();

					/*
					 * Allowing the dictionary growth for the already existing
					 * dictionary so that new features can be added to the
					 * dictionary. this was a source of problem while trying to
					 * achieve this task
					 */
					outsideFeatureDictionary
							.allowDictionaryGrowth(initialFilteredDictionaryList);
				}
				/*
				 * Now do the filtering of the dictionary list and update the
				 * filtered dictionary list with the filtered dictionaries
				 */
				filteredOutsideFeatureDictionary = outsideFeatureDictionary
						.updateFilteredFeatureDictionary(
								updateOutsideFeatureDictionary,
								initialFilteredDictionaryList);
			}
		}

		/*
		 * Stop the dictionary growth before they can be used to extract the
		 * vectors because the lookup index method will just add new features to
		 * the dictionary which is not what we want
		 */
		outsideFeatureDictionary
				.stopDictionaryGrowth(updateOutsideFeatureDictionary);
		outsideFeatureDictionary
				.stopDictionaryGrowth(filteredOutsideFeatureDictionary);

		return filteredOutsideFeatureDictionary;
	}
}
