package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideFootParent implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideFootParent;
	private Alphabet outsideFootParentFiltered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideFootParent(OutsideFeatureObjectStore objectStore) {
		this.outsideFootParent = new Alphabet();
		this.outsideFootParentFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideFootParent.turnOnCounts();
		this.outsideFootParentFiltered.turnOnCounts();
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
		 * Extracting the outside feature TODO
		 */
		String feature = null;
		if (foottoroot.size() >= 2) {
			/*
			 * TODO
			 */
			Tree<String> footTree = foottoroot.pop();
			Tree<String> parentTree = foottoroot.pop();

			feature = footTree.getLabel() + "," + parentTree.getLabel();

			// Putting them back
			foottoroot.push(parentTree);
			foottoroot.push(footTree);
		} else {
			feature = "NOTVALID";
		}
		return feature;
	}

	@Override
	public Alphabet getFeatureDictionary() {
		return this.outsideFootParent;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideFootParentFiltered;
	}

}
