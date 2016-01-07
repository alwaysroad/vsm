package VSMInterfaces;

import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

/**
 * The interface is implemented by all the inside feature objects.
 * 
 * @author sameerkhurana10
 *
 */
public interface InsideFeatureObject {

	/**
	 * The method returns the desired inside feature extracted from the inside
	 * tree that is passed as an argument. Each inside feature object has its
	 * own implementation, of course, because each object extracts a different
	 * inside feature
	 * 
	 * @param insideTree
	 *            - the inside tree for a particular node in the tree
	 * @param isPreterminal
	 *            - whether the inside tree for the particular node is a
	 *            preTerminal or not, because if it is a pre-terminal then many
	 *            of the features will be invalid
	 * @return - inside feature i.e. a String
	 */
	String getInsideFeature(Tree<String> insideTree, boolean isPreterminal);

	/**
	 * This method can be used to retrieve the feature dictionary contained in
	 * the insideFeatureObject. The purpose of retrieving the dictionary is so
	 * that we can update it by calling the lookupindex method on it. The
	 * dictionary stores the values in the form of key value pairs, key being
	 * the feature and value being the index
	 * 
	 * @return
	 */
	Alphabet getFeatureDictionary();

	/**
	 * This method returns the filtered dictionary corresponding to the feature
	 * object
	 */
	Alphabet getFilteredDictionary();
}
