package VSMFeatureDictionaries;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Logger;

import VSMFeatureDictionary.VSMInsideFeatureDictionaryCC;
import VSMInterfaces.ExtractDictionary;
import VSMInterfaces.InsideFeatureDictionary;
import VSMInterfaces.InsideFeatureObject;
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
public class ExtractInsideFeatureDictionary implements ExtractDictionary {

	private InsideFeatureDictionary insideFeatureDictionary;

	public ExtractInsideFeatureDictionary(String nonTerminal, Logger logger) {

		try {
			insideFeatureDictionary = new InsideFeatureDictionaryImpl(
					nonTerminal, logger);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<Alphabet> getUpdatedFeatureDictionary(
			ArrayList<String> treeFilePaths,
			VSMDictionaryBean alreadyExistingDictionaryBean) throws Exception {

		ArrayList<InsideFeatureObject> initialObjectStore = insideFeatureDictionary
				.getInsideFeatureObjectStore();

		ArrayList<InsideFeatureObject> updatedObjectStore = null;
		ArrayList<Alphabet> updatedInsideFeatureDictionary = null;
		ArrayList<Alphabet> initialFilteredDictionary = null;
		ArrayList<Alphabet> filteredInsideFeatureDictionary = null;

		int fileCount = 0;

		System.out.println("***Extracting inside feature dictionary***");
		for (String treeFilePath : treeFilePaths) {

			if (fileCount < 50000) {
				fileCount++;
				System.out.println("**File Count**" + fileCount);

				insideFeatureDictionary.updateObjectStore(treeFilePath);

				updatedObjectStore = insideFeatureDictionary
						.getUpdatedObjectStore();

				updatedInsideFeatureDictionary = insideFeatureDictionary
						.getInsideFeatureDictionaryList(updatedObjectStore);

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
					System.out.println("Dictionary already exist**");
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

			InsideFeatureDictionaryImpl.closeWriter();

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
