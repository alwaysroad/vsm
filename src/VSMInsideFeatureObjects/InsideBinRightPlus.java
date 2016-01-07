package VSMInsideFeatureObjects;

import java.util.List;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

/**
 * This is the InsideBinFull feature object class. The object of this class
 * holds a dictionary that stores the features of the type a -> b c. The object
 * of this class is also used to call the method that extracts the feature, a ->
 * b (c -> .)
 * 
 * @author sameerkhurana10
 *
 */
public class InsideBinRightPlus implements InsideFeatureObject {

	/**
	 * Declare the dictionary
	 */
	private Alphabet insideBinRightPlus;
	private Alphabet insideBinRightPlusFiltered;

	/**
	 * Instantiate the dictionary and register the feature object with the
	 * feature object store
	 */
	public InsideBinRightPlus(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideBinRightPlus = new Alphabet();
		this.insideBinRightPlusFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.insideBinRightPlus.turnOnCounts();
		this.insideBinRightPlusFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The feature that this method is extracting is of the form a -> b (c -> .)
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		if (!isPreterminal) {
			/*
			 * Getting the trees of the child nodes
			 */
			List<Tree<String>> children = insideTree.getChildren();
			/*
			 * Checking the list. It should have the required size
			 */
			if ((!children.isEmpty()) && children.size() > 1) {
				Tree<String> right = children.get(1);
				/*
				 * Getting the required feature string from the right tree c ->
				 * .
				 */
				String rightstr = VSMUtil.getTreeString(right);
				return (insideTree.getLabel() + "->"
						+ children.get(0).getLabel() + ",(" + rightstr + ")");
			}
		}
		/*
		 * The feature might not exist at all
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
		return this.insideBinRightPlus;
	}

	/*
	 * TODO
	 */
	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideBinRightPlusFiltered;
	}

}
