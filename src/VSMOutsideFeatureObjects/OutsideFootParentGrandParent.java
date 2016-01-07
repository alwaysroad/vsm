package VSMOutsideFeatureObjects;

import java.util.Stack;

import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

public class OutsideFootParentGrandParent implements OutsideFeatureObject {

	/**
	 * TODO Alphabets that stores all the features
	 */
	private Alphabet outsideFootParentGrandparent;
	private Alphabet outsideFootParentGrandparentFiltered;

	/**
	 * TODO Initialize the inside_unary alphabet in the constructor
	 */
	public OutsideFootParentGrandParent(OutsideFeatureObjectStore objectStore) {
		this.outsideFootParentGrandparent = new Alphabet();
		this.outsideFootParentGrandparentFiltered = new Alphabet();
		/*
		 * Counts
		 */
		this.outsideFootParentGrandparent.turnOnCounts();
		this.outsideFootParentGrandparentFiltered.turnOnCounts();
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
		 * Extract the feature TODO
		 */
		String feature = null;
		if (foottoroot.size() >= 3) {
			Tree<String> footTree = foottoroot.pop();
			Tree<String> parentTree = foottoroot.pop();
			Tree<String> grandparentTree = foottoroot.pop();

			/*
			 * TODO
			 */
			feature = footTree.getLabel() + "," + parentTree.getLabel() + ","
					+ grandparentTree.getLabel();

			/*
			 * TODO
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
		return this.outsideFootParentGrandparent;
	}

	@Override
	public Alphabet getFilteredDictionary() {
		return this.outsideFootParentGrandparentFiltered;
	}

}
