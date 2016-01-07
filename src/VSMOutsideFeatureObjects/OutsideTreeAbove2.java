package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideTreeAbove2 implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideTreeAbove2;
	private Alphabet outsideTreeAbove2Filtered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideTreeAbove2(OutsideFeatureObjectStore objectStore) {
		this.outsideTreeAbove2 = new Alphabet();
		this.outsideTreeAbove2Filtered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideTreeAbove2.turnOnCounts();
		this.outsideTreeAbove2Filtered.turnOnCounts();
		/*
		 * Register
		 */
		objectStore.registerOutsideFeatureObject(this);
	}

	/**
	 * For description see the interface description
	 */
	@Override
	public String getOutsideFeature(Stack<Tree<String>> foottoroot) {

		/*
		 * Extracting the feature TODO The piece of code is implemented by Dr
		 * Shay Cohen
		 */
		String feature = null;
		if (foottoroot.size() >= 3) {
			Tree<String> footTree = foottoroot.pop();
			Tree<String> parentTree = foottoroot.pop();
			Tree<String> grandparentTree = foottoroot.pop();

			/*
			 * The function that extracts the feature The function is developed
			 * by Dr Shay Cohen
			 */
			feature = VSMUtil.getStringFromGrandparent(grandparentTree,
					parentTree, footTree);

			/*
			 * Putting them back TODO
			 */
			foottoroot.push(grandparentTree);
			foottoroot.push(parentTree);
			foottoroot.push(footTree);
		} else {
			/*
			 * TODO
			 */
			feature = "NOTVALID";
		}
		return feature;

	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.outsideTreeAbove2;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideTreeAbove2Filtered;
	}
}
