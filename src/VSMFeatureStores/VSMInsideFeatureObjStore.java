package VSMFeatureStores;

import java.util.ArrayList;

import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;

/**
 * The class has methods to extract the inside tree features and store them in
 * the inside feature dictionaries that are contained by the inside feature
 * objects
 * 
 * @author sameerkhurana10
 *
 */
public class VSMInsideFeatureObjStore implements InsideFeatureObjectStore {

	/**
	 * The data structure that holds all the feature objects. This is the store
	 */
	private ArrayList<InsideFeatureObject> insideFeatureObjStore = new ArrayList<InsideFeatureObject>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObjectStore#registerInsideFeatureObject(
	 * VSMInterfaces.InsideFeatureObject)
	 */
	@Override
	public void registerInsideFeatureObject(InsideFeatureObject o) {
		/*
		 * Registering objects to the store
		 */
		insideFeatureObjStore.add(o);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObjectStore#removeInsideFeatureObject(
	 * VSMInterfaces.InsideFeatureObject)
	 */
	@Override
	public void removeInsideFeatureObject(InsideFeatureObject o) {
		insideFeatureObjStore.remove(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureObjectStore#getInsideFeatureObjectStore()
	 */
	@Override
	public ArrayList<InsideFeatureObject> getInsideFeatureObjectStore() {
		return this.insideFeatureObjStore;
	}

}
