package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideFootNumwordsleft implements OutsideFeatureObject {

	/**
	 * This is the data structure that acts as a feature dictionary
	 */
	private Alphabet outsideFootNumwordsLeft;
	/**
	 * This is the data structure that acts as the filtered feature dictionary
	 */
	private Alphabet outsideFootNumwordsLeftFiltered;

	/**
	 * TODO This variable belongs to the class and not any particular object
	 */
	public static int outsideWordsLeft;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideFootNumwordsleft(OutsideFeatureObjectStore objectStore) {
		/*
		 * Instantiate the feature dictionaries
		 */
		this.outsideFootNumwordsLeft = new Alphabet();
		this.outsideFootNumwordsLeftFiltered = new Alphabet();

		/*
		 * Turn on the counts
		 */
		this.outsideFootNumwordsLeft.turnOnCounts();
		this.outsideFootNumwordsLeftFiltered.turnOnCounts();
		/*
		 * Register the feature object with the outside feature object store
		 */
		objectStore.registerOutsideFeatureObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.OutsideFeatureObject#getOutsideFeature(java.util.Stack)
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
		return (footlabel + "," + outsideWordsLeft);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.OutsideFeatureObject#getFeatureDictionary()
	 */
	@Override
	public Alphabet getFeatureDictionary() {

		return this.outsideFootNumwordsLeft;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.OutsideFeatureObject#getFilteredDictionary()
	 */
	@Override
	public Alphabet getFilteredDictionary() {

		return this.outsideFootNumwordsLeftFiltered;
	}

}
