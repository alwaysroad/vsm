package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideTreeAbove3 implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideTreeAbove3;
	private Alphabet outsideTreeAbove3Filtered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideTreeAbove3(OutsideFeatureObjectStore objectStore) {
		this.outsideTreeAbove3 = new Alphabet();
		this.outsideTreeAbove3Filtered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideTreeAbove3.turnOnCounts();
		this.outsideTreeAbove3Filtered.turnOnCounts();
		/*
		 * register
		 */
		objectStore.registerOutsideFeatureObject(this);
	}

	/**
	 * For description see the interface description
	 */
	@Override
	public String getOutsideFeature(Stack<Tree<String>> foottoroot) {

		/*
		 * Getting the feature TODO
		 */
		String feature = null;
		if (foottoroot.size() >= 4) {
			Tree<String> footTree = foottoroot.pop();
			Tree<String> parentTree = foottoroot.pop();
			Tree<String> grandparentTree = foottoroot.pop();
			Tree<String> greatgrandparentTree = foottoroot.pop();

			/*
			 * The function written by Dr Shay Cohen
			 */
			feature = VSMUtil
					.getStringFromGreatgrandparent(greatgrandparentTree,
							grandparentTree, parentTree, footTree);

			// Putting them back
			foottoroot.push(greatgrandparentTree);
			foottoroot.push(grandparentTree);
			foottoroot.push(parentTree);
			foottoroot.push(footTree);
		} else {
			feature = "NOTVALID";
		}
		return feature;
	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.outsideTreeAbove3;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideTreeAbove3Filtered;
	}

}
