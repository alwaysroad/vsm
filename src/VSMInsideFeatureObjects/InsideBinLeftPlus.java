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
 * (b -> d,e), c
 * 
 * @author sameerkhurana10
 *
 */
public class InsideBinLeftPlus implements InsideFeatureObject {

	/**
	 * Declare the feature dictionary
	 */
	private Alphabet insideBinLeftPlus;
	/**
	 * 
	 */
	private Alphabet insideBinLeftPlusFiltered;

	/**
	 * Instantiate the feature dictionary and registering the class object with
	 * the feature object store
	 */
	public InsideBinLeftPlus(InsideFeatureObjectStore insideFeatureObjStore) {
		this.insideBinLeftPlus = new Alphabet();
		this.insideBinLeftPlusFiltered = new Alphabet();
		/*
		 * Turn on the counts
		 */
		this.insideBinLeftPlus.turnOnCounts();
		this.insideBinLeftPlusFiltered.turnOnCounts();
		insideFeatureObjStore.registerInsideFeatureObject(this);
	}

	/**
	 * The feature that we are trying to extract here is of the form a -> (b ->
	 * d,e), c
	 */
	@Override
	public String getInsideFeature(Tree<String> insideTree,
			boolean isPreterminal) {
		if (!isPreterminal) {
			/*
			 * Getting the trees of all the child nodes of the node under
			 * scrutiny
			 */
			List<Tree<String>> children = insideTree.getChildren();

			/*
			 * Check the size of the children tree to make sure that the feature
			 * that we are trying to extract exists in the inside tree
			 */
			if ((!children.isEmpty()) && children.size() > 1) {
				Tree<String> left = children.get(0);
				/*
				 * Getting the left String i.e. the a->(b->d,e),c
				 */
				String leftString = VSMUtil.getTreeString(left);
				if (leftString != null) {
					return (insideTree.getLabel() + "->(" + leftString + ")," + children
							.get(1).getLabel());
				}
			}
		}
		return "NOTVALID";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObject#getFeatureDictionary()
	 */
	@Override
	public Alphabet getFeatureDictionary() {
		return this.insideBinLeftPlus;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.insideBinLeftPlusFiltered;
	}

}
