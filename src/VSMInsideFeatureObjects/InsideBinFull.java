package VSMInsideFeatureObjects;

import java.util.List;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

/**
 * This is the InsideBinFull feature object class. The object of this class
 * holds a dictionary that stores the features of the type a -> b c. The object
 * of this class is also used to call the method that extracts the feature, a ->
 * b c
 * 
 * @author sameerkhurana10
 *
 */

public class InsideBinFull implements InsideFeatureObject {

	/**
	 * Declaring the feature dictionary. This dictionary holds all the features
	 * of the type a -> b c, where a, b, c are non-terminals
	 */
	private Alphabet insideBinFull;
	private Alphabet insideBinFullFiltered;

	/**
	 * Instantiate the dictionary and register the class object with the feature
	 * object store
	 */
	public InsideBinFull(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideBinFull = new Alphabet();
		this.insideBinFullFiltered = new Alphabet();
		/*
		 * Turn on counts right here, i.e. instantiate the countmap
		 */
		this.insideBinFull.turnOnCounts();
		this.insideBinFullFiltered.turnOnCounts();
		/*
		 * Register the class object
		 */
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The method extracts the feature a -> b c
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		/*
		 * You can extract a -> b c only if the node a is not a pre-terminal of
		 * course
		 */
		if (!isPreterminal) {
			/*
			 * Getting the children trees i.e. the node trees for the children
			 * nodes of the node under scrutiny
			 */
			List<Tree<String>> children = insideTree.getChildren();
			/*
			 * The if condition is added because the code was throwing error for
			 * nodes that do not have long enough inside trees
			 */
			if (children.size() > 1) {
				return (insideTree.getLabel() + "->"
						+ children.get(0).getLabel() + "," + children.get(1)
						.getLabel());
			}
		}
		/*
		 * If the feature does not exist for a particular node then return
		 * NOTVALID and hence nothing would be added to the the dictionary in
		 * this case
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
		return this.insideBinFull;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideBinFullFiltered;
	}

}
