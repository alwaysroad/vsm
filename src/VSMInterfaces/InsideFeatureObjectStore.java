package VSMInterfaces;

import java.util.ArrayList;

import VSMUtilityClasses.Alphabet;

/**
 * The interface is implemented by the CreateInsideDictionary class
 * 
 * @author sameerkhurana10
 *
 */
public interface InsideFeatureObjectStore {

	/**
	 * This method is called by each inside feature object in its constructor so
	 * that the inside feature object gets registered in the list of feature
	 * objects maintained by InsideFeatureDictionary object.
	 * 
	 * @param o
	 *            - the inside feature object
	 */
	void registerInsideFeatureObject(InsideFeatureObject o);

	/**
	 * The method so that an inside feature object can remove itself from the
	 * register of inside feature objects
	 * 
	 * @param o
	 *            - the inside feature object
	 */
	void removeInsideFeatureObject(InsideFeatureObject o);

	/**
	 * This method just returns the register maintained by the
	 * InsideFeatureObjectStore object
	 * 
	 * @return - ArrayList<InsideFeatureObject> - this is the register
	 *         maintained by this object
	 */

	ArrayList<InsideFeatureObject> getInsideFeatureObjectStore();

}
