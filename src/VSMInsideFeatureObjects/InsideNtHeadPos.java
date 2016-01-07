package VSMInsideFeatureObjects;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.AbstractHeadFinder;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PennTreebankCollinsHeadFinder;
import edu.berkeley.nlp.syntax.Tree;

public class InsideNtHeadPos implements InsideFeatureObject {

	/**
	 * Declare the feature dictionary
	 */
	private Alphabet insideNtHeadPos;
	private Alphabet insideNtHeadPosFiltered;

	/**
	 * Instantiate the feature dictionary and register the class object
	 */
	public InsideNtHeadPos(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideNtHeadPos = new Alphabet();
		this.insideNtHeadPosFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.insideNtHeadPos.turnOnCounts();
		this.insideNtHeadPosFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The method extracts the feature (a, pos)
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		/*
		 * Using the Collins head finder
		 */
		AbstractHeadFinder headfinder = new PennTreebankCollinsHeadFinder();
		/*
		 * Getting the required POS tag only if the inside tree is not a leaf
		 */
		if (!insideTree.isLeaf()) {
			String headpos = headfinder.getHeadPartOfSpeech(insideTree);
			return (insideTree.getLabel() + "," + headpos);
		} else {
			System.err
					.println("Error from InsideNtHeadPos object The inside tree is a leaf and hence the head pos tag feature cannot be returned****"
							+ insideTree);
			return "NOTVALID";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObject#getFeatureDictionary()
	 */
	@Override
	public Alphabet getFeatureDictionary() {
		return this.insideNtHeadPos;
	}

	/*
	 * 
	 */
	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideNtHeadPosFiltered;
	}

}
