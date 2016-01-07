package VSMInterfaces;

import java.util.Stack;

import VSMUtilityClasses.Alphabet;
import edu.berkeley.nlp.syntax.Tree;

/**
 * The interface is implemented by all the outside feature objects.
 * 
 * @author sameerkhurana10
 *
 */
public interface OutsideFeatureObject {
	/**
	 * This method returns the desired feature from the outside tree of a
	 * particular node in the tree. Each outside feature has its own
	 * implementation of the method, of course, because each object stores a
	 * different kind of outside tree feature
	 * 
	 * @param footToRoot
	 *            - this is a stack that is formed before calling this method
	 *            and then passed as an argument to this method. The stack
	 *            contains all the outside constituent trees corresponding to
	 *            the particular node under scrutiny
	 * @return - the desired outside feature i.e. a String
	 */
	String getOutsideFeature(Stack<Tree<String>> footToRoot);

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
