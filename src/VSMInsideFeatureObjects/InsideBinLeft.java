package VSMInsideFeatureObjects;

import java.util.List;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

/**
 * This is the InsideBinFull feature object class. The object of this class
 * holds a dictionary that stores the features of the type a -> b. The object of
 * this class is also used to call the method that extracts the feature, a -> b
 * 
 * @author sameerkhurana10
 *
 */
public class InsideBinLeft implements InsideFeatureObject {

	/**
	 * Declaring the feature dictionary
	 */
	private Alphabet insideBinLeft;
	private Alphabet insideBinLeftFiltered;

	/**
	 * Instantiate the dictionary and register the class object in the feature
	 * object store
	 */
	public InsideBinLeft(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideBinLeft = new Alphabet();
		this.insideBinLeftFiltered = new Alphabet();
		/*
		 * Turn on the counts
		 */
		this.insideBinLeft.turnOnCounts();
		this.insideBinLeftFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);

	}

	/**
	 * Extracts the feature of the type
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		/*
		 * Feature of the form a->b can be extracted only if the node is not a
		 * pre-terminal
		 */
		if (!isPreterminal) {
			/*
			 * Getting the trees of all the child nodes of the node under
			 * scrutiny
			 */
			List<Tree<String>> children = insideTree.getChildren();
			/*
			 * The list should not be empty
			 */
			if (!(children.isEmpty())) {
				return (insideTree.getLabel() + "->" + children.get(0)
						.getLabel());
			}
		}
		/*
		 * If the feature does not exist then return the below String
		 */
		return "NOTVALID";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObject#getFeatureDictionary()
	 */
	@Override
	public Alphabet getFeatureDictionary() {

		return this.insideBinLeft;
	}

	/*
	 * TODO
	 */
	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideBinLeftFiltered;
	}

}
