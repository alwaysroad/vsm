package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideTreeabove1 implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideTreeAbove1;
	private Alphabet outsideTreeAbove1Filtered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideTreeabove1(OutsideFeatureObjectStore objectStore) {
		this.outsideTreeAbove1 = new Alphabet();
		this.outsideTreeAbove1Filtered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideTreeAbove1.turnOnCounts();
		this.outsideTreeAbove1Filtered.turnOnCounts();
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
		 * 
		 * extracting the feature TODO. The code is written by Dr Shay Cohen
		 */
		String feature = null;
		if (foottoroot.size() >= 2) {
			Tree<String> footTree = foottoroot.pop();
			Tree<String> parentTree = foottoroot.pop();

			/*
			 * TODO
			 */
			feature = VSMUtil.getStringFromParent(parentTree, footTree);

			/*
			 * Putting them back TODO
			 */
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
	public VSMUtilityClasses.Alphabet getFeatureDictionary() {
		return this.outsideTreeAbove1;
	}

	@Override
	public VSMUtilityClasses.Alphabet getFilteredDictionary() {
		return this.outsideTreeAbove1Filtered;
	}

}
