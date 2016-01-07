package VSMInsideFeatureObjects;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class InsideNtNumOfWords implements InsideFeatureObject {

	/**
	 * Declare the feature dictionary
	 */
	private Alphabet insideNtNumOfWords;
	private Alphabet insideNtNumOfWordsFiltered;

	/**
	 * Static variable to store the Length of the constituent
	 */
	public static int length;

	/**
	 * Instantiate the feature dictionary and register the object with the
	 * feature object store
	 */
	public InsideNtNumOfWords(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideNtNumOfWords = new Alphabet();
		this.insideNtNumOfWordsFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.insideNtNumOfWords.turnOnCounts();
		this.insideNtNumOfWordsFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The function extracts the feature of the form (a,num). For more
	 * information see the interface documentation
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		
		return (insideTree.getLabel() + "," + length);
	}

	/*
	 * 
	 */
	@Override
	public Alphabet getFeatureDictionary() {
		return this.insideNtNumOfWords;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObject#getFilteredDictionary()
	 */
	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideNtNumOfWordsFiltered;
	}

}
