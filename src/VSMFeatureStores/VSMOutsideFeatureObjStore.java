package VSMFeatureStores;

import java.util.ArrayList;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;

/**
 * The class has methods to extract the inside tree features and store them in
 * the inside feature dictionaries that are contained by the inside feature
 * objects
 * 
 * @author sameerkhurana10
 *
 */
public class VSMOutsideFeatureObjStore implements OutsideFeatureObjectStore {

	/**
	 * The data structure that holds all the feature objects. This is the store
	 */
	private ArrayList<OutsideFeatureObject> outsideFeatureObjStore = new ArrayList<OutsideFeatureObject>();

	@Override
	public void registerOutsideFeatureObject(OutsideFeatureObject o) {
		outsideFeatureObjStore.add(o);

	}

	@Override
	public void removeOutsideFeatureObject(OutsideFeatureObject o) {
		outsideFeatureObjStore.remove(o);

	}

	@Override
	public ArrayList<OutsideFeatureObject> getOutsideFeatureObjectStore() {
		return this.outsideFeatureObjStore;
	}

}
