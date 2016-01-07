package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideFootNumwordsright implements OutsideFeatureObject {

	/**
	 * Declaring the outside feature dictionaries
	 */
	private Alphabet outsideFootNumwordsRight;
	private Alphabet outsideFootNumwordsRightFiltered;

	/**
	 * TODO
	 */
	public static int outsideWordsRight;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideFootNumwordsright(OutsideFeatureObjectStore objectStore) {
		this.outsideFootNumwordsRight = new Alphabet();
		this.outsideFootNumwordsRightFiltered = new Alphabet();

		/*
		 * Counts
		 */
		this.outsideFootNumwordsRight.turnOnCounts();
		this.outsideFootNumwordsRightFiltered.turnOnCounts();
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
		 * Getting the top element of the stack without removing it. This
		 * statement gives us the inside tree or the foottree as we call it
		 */
		Tree<String> footTree = foottoroot.peek();
		String footlabel = footTree.getLabel();

		/*
		 * We can access the static variable in a non-static method
		 */
		return (footlabel + "," + outsideWordsRight);
	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.outsideFootNumwordsRight;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideFootNumwordsRightFiltered;
	}

}
