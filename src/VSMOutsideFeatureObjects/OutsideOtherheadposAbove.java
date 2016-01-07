package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.AbstractHeadFinder;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PennTreebankCollinsHeadFinder;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideOtherheadposAbove implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideOtherHeadposAbove;
	private Alphabet outsideOtherHeadposAboveFiltered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideOtherheadposAbove(OutsideFeatureObjectStore objectStore) {
		this.outsideOtherHeadposAbove = new Alphabet();
		this.outsideOtherHeadposAboveFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideOtherHeadposAbove.turnOnCounts();
		this.outsideOtherHeadposAboveFiltered.turnOnCounts();
		/*
		 * Register
		 */
		objectStore.registerOutsideFeatureObject(this);
	}

	/**
	 * For description see the interface description TODO
	 */
	@Override
	public String getOutsideFeature(Stack<Tree<String>> foottoroot) {
		/*
		 * TODO
		 */
		AbstractHeadFinder headfinder = new PennTreebankCollinsHeadFinder();
		Stack<Tree<String>> tempstack = new Stack<Tree<String>>();

		Tree<String> footTree = foottoroot.pop();
		tempstack.push(footTree);
		String footpos = headfinder.getHeadPartOfSpeech(footTree);

		String feature = "NOTVALID";

		// Pop and check
		while (!foottoroot.empty()) {
			Tree<String> parentTree = foottoroot.pop();
			tempstack.push(parentTree);

			String headpos = headfinder.getHeadPartOfSpeech(parentTree);
			if (!headpos.equals(footpos)) {
				feature = headpos;
				break;
			}
		}

		// Push back
		while (!tempstack.empty()) {
			Tree<String> item = tempstack.pop();
			foottoroot.push(item);
		}

		return feature;
	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.outsideOtherHeadposAbove;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideOtherHeadposAboveFiltered;
	}

}
