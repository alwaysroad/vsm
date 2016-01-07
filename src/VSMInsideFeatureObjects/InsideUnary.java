package VSMInsideFeatureObjects;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class InsideUnary implements InsideFeatureObject {

	/**
	 * Declare the dictionary
	 */
	private Alphabet insideUnary;
	private Alphabet insideUnaryFiltered;

	/**
	 * Instantiate and register the class object with the feature object store
	 */
	public InsideUnary(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideUnary = new Alphabet();
		this.insideUnaryFiltered = new Alphabet();
		/*
		 * Turn on the counts i.e. instantiated the count map only once
		 */
		this.insideUnary.turnOnCounts();
		this.insideUnaryFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The code is used to extract features of the form a->x
	 * 
	 * @param insideTree
	 * @param isPreterminal
	 * @return
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		if (isPreterminal) {
			return (insideTree.getLabel() + "->" + insideTree.getChildren()
					.get(0).getLabel().toLowerCase());
		} else {
			return "NOTVALID";
		}
	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.insideUnary;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideUnaryFiltered;
	}

}
