package VSMFeatureDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import VSMConstants.VSMContant;
import VSMFeatureStores.VSMOutsideFeatureObjStore;
import VSMInterfaces.OutsideFeatureDictionary;
import VSMInterfaces.OutsideFeatureObject;
import VSMInterfaces.OutsideFeatureObjectStore;
import VSMOutsideFeatureObjects.OutsideFootNumwordsleft;
import VSMOutsideFeatureObjects.OutsideFootNumwordsright;
import VSMOutsideFeatureObjects.OutsideFootParent;
import VSMOutsideFeatureObjects.OutsideFootParentGrandParent;
import VSMOutsideFeatureObjects.OutsideOtherheadposAbove;
import VSMOutsideFeatureObjects.OutsideTreeAbove2;
import VSMOutsideFeatureObjects.OutsideTreeAbove3;
import VSMOutsideFeatureObjects.OutsideTreeabove1;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class VSMOutsideFeatureDictionaryJJ implements OutsideFeatureDictionary {

	private ArrayList<Alphabet> outsideFeatureDictionaryList;
	private OutsideFeatureObjectStore outsideFeatureObjStore;
	private ArrayList<Alphabet> filteredFeatureDictionaryList;
	private ArrayList<OutsideFeatureObject> objectStore;
	private ArrayList<OutsideFeatureObject> updatedObjectStore;

	private Trees.StandardTreeNormalizer obj;
	private PTBTreeNormaliser transformedTree;

	public VSMOutsideFeatureDictionaryJJ() {

		this.obj = new Trees.StandardTreeNormalizer();

		this.transformedTree = new PTBTreeNormaliser(true);
		/*
		 * The connection between the objects and the object store in which they
		 * register. The store is instantiated. The store with which the objects
		 * register
		 */
		this.outsideFeatureObjStore = new VSMOutsideFeatureObjStore();
		/*
		 * Instantiating the data structure, a list, that is used to store the
		 * inside dictionaries that associated with the feature objects in the
		 * store. Other objects access this reference variable when they want to
		 * access the non filtered dictionary
		 */
		this.outsideFeatureDictionaryList = new ArrayList<Alphabet>();
		/*
		 * Instantiating the data structure that is used to store all the
		 * filtered dictionaries. Other class objects access this data structure
		 * when they want to access the filtered dictionatries
		 */
		this.filteredFeatureDictionaryList = new ArrayList<Alphabet>();
		/*
		 * Creating the object store
		 */
		new OutsideFootNumwordsleft(outsideFeatureObjStore);
		new OutsideFootNumwordsright(outsideFeatureObjStore);
		new OutsideFootParent(outsideFeatureObjStore);
		new OutsideFootParentGrandParent(outsideFeatureObjStore);
		new OutsideOtherheadposAbove(outsideFeatureObjStore);
		new OutsideTreeabove1(outsideFeatureObjStore);
		new OutsideTreeAbove2(outsideFeatureObjStore);
		new OutsideTreeAbove3(outsideFeatureObjStore);

		/*
		 * Referencing the object store using a reference variable declared in
		 * this object. The outside objects access the object store by accessing
		 * the instance variable objectStore
		 */
		this.objectStore = this.outsideFeatureObjStore
				.getOutsideFeatureObjectStore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.InsideFeatureDictionary#updateFeatureDictionary(java.lang
	 * .String)
	 */
	@Override
	public void updateObjectStore(String URI) throws Exception {

		/*
		 * Iterator that iterates over the trees in the file defined by the URI
		 * passed to the method
		 */
		PennTreeReader treeReader = VSMUtil.getTreeReader(URI);
		/*
		 * Iterate over the trees
		 */

		while (treeReader.hasNext()) {

			/*
			 * Getting a particular tree
			 */
			Tree<String> syntaxTree = null;

			try {
				/*
				 * Transforming the penn tree the syntax tree to remove S1 i.e.
				 * the root node or the Top Bracket
				 */
				syntaxTree = transformedTree.process(treeReader.next());

			} catch (RuntimeException e) {
				System.out.println("exception" + e + " ::tree  " + syntaxTree);
			}
			if (syntaxTree != null) {
				/*
				 * Constituents Map is needed later
				 */
				Map<Tree<String>, Constituent<String>> constituentsMap = syntaxTree
						.getConstituents();
				/*
				 * Now we need to iterate over the nodes in a tree
				 */
				Iterator<Tree<String>> syntaxTreeIt = syntaxTree.iterator();
				/*
				 * Iterating but checking whether the tree is a leaf i.e. a
				 * word, then of course do not extract any features from the
				 * tree
				 */
				while (syntaxTreeIt.hasNext()) {
					/*
					 * Getting the inside tree associated with a particular node
					 */
					Tree<String> insideTree = syntaxTreeIt.next();
					/*
					 * Getting the foottoroot path
					 */
					Stack<Tree<String>> footToRoot = new Stack<Tree<String>>();
					/*
					 * Updating the stack with the desired path
					 */
					VSMUtil.updateFoottorootPath(footToRoot, syntaxTree,
							insideTree, constituentsMap);

					/*
					 * Setting the static variable which would be different for
					 * each node
					 */
					VSMUtil.getNumberOfOutsideWordsLeft(insideTree,
							constituentsMap, syntaxTree);
					/*
					 * Setting the static variable which is a feature that is
					 * different for each node
					 */
					VSMUtil.getNumberOfOutsideWordsRight(insideTree,
							constituentsMap, syntaxTree);
					/*
					 * Now we need to extract the inside features from this
					 * inside tree, Iterate over the object store
					 */
					if (!insideTree.isLeaf()
							&& insideTree.getLabel().equalsIgnoreCase("JJ")) {
						/*
						 * Updating the objectStore, one featureDictionary at a
						 * time
						 */
						for (OutsideFeatureObject outsideFeatureObj : objectStore) {
							/*
							 * Getting the dictionary object inside the feature
							 * object
							 */
							Alphabet featureDictionary = outsideFeatureObj
									.getFeatureDictionary();
							/*
							 * Getting the feature by calling the feature
							 * object's getInideFeature() function
							 */
							String feature = outsideFeatureObj
									.getOutsideFeature(footToRoot);
							/*
							 * Updating the dictionary object inside the feature
							 * object
							 */
							if (!feature.equals("NOTVALID")) {
								featureDictionary.lookupIndex(feature);
							}
						}
					}
				}
			}
		}
		/*
		 * Just giving the object store a new reference variable because it is
		 * updated after all, the same name does not make sense!! and it is more
		 * natural. Started off with an object store, did some feature
		 * extraction, updated the dictionaries and got an updated object store
		 */
		this.updatedObjectStore = objectStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureDictionary#getUpdatedObjectStore()
	 */
	@Override
	public ArrayList<OutsideFeatureObject> getUpdatedObjectStore() {
		if (objectStore == updatedObjectStore) {
			return this.updatedObjectStore;
		} else {
			System.err
					.println("The object store hasn't been updated yet, first call the updateObjectStore() method");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.InsideFeatureDictionary#getInsideFeatureDictionary(java
	 * .util.ArrayList)
	 */
	@Override
	public ArrayList<Alphabet> getOutsideFeatureDictionaryList(
			ArrayList<OutsideFeatureObject> updatedObjectStore) {
		/*
		 * Very important to instantiate a new list object here otherwise, we
		 * will just keep adding objects to the list instantiated in the
		 * constructor. This was a source of error while testing
		 */
		ArrayList<Alphabet> outsideFeatureDictionaryList = new ArrayList<Alphabet>();
		/*
		 * Extracting the feature dictionaries from the object store and adding
		 * to the ArrayList data structure
		 */
		for (OutsideFeatureObject outsideFeatureObj : updatedObjectStore) {
			outsideFeatureDictionaryList.add(outsideFeatureObj
					.getFeatureDictionary());
		}
		/*
		 * insideFeatureDictionaryList referring to the data
		 * insideFeatureDcitionaryList
		 */
		// this.outsideFeatureDictionaryList = outsideFeatureDictionaryList;
		return outsideFeatureDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.InsideFeatureDictionary#stopDictionaryGrowth(java.util.
	 * ArrayList)
	 */
	@Override
	public void stopDictionaryGrowth(
			ArrayList<Alphabet> outsideFeatureDictionaryList) {
		/*
		 * Stopping the dictionary growth
		 */
		for (Alphabet dictionary : outsideFeatureDictionaryList) {
			dictionary.stopGrowth();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.OutsideFeatureDictionary#getOutsideFeatureObjectStore()
	 */
	@Override
	public ArrayList<OutsideFeatureObject> getOutsideFeatureObjectStore() {
		return this.objectStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.InsideFeatureDictionary#getFilteredFeatureDictionaryList
	 * (java.util.ArrayList)
	 */
	@Override
	public ArrayList<Alphabet> getFilteredFeatureDictionaryList(
			ArrayList<OutsideFeatureObject> outsideFeatureObjStore) {

		/*
		 * Instantiating the filteredFeatureDictionaryList
		 */
		ArrayList<Alphabet> filteredFeatureDictionaryList = new ArrayList<Alphabet>();

		/*
		 * Extracting the filtered feature dictionary from the feature object
		 * store and adding that to the filteredDictionaryList
		 */
		for (OutsideFeatureObject outsideFeatureObj : outsideFeatureObjStore) {
			filteredFeatureDictionaryList.add(outsideFeatureObj
					.getFilteredDictionary());
		}
		return filteredFeatureDictionaryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * VSMInterfaces.InsideFeatureDictionary#filterFeatureDictionary(java.util
	 * .ArrayList)
	 */
	@Override
	public ArrayList<Alphabet> updateFilteredFeatureDictionary(
			ArrayList<Alphabet> outsideFeatureDictionaryList,
			ArrayList<Alphabet> filteredFeatureDictionaryList) {
		for (int i = 0; i < outsideFeatureDictionaryList.size(); i++) {
			/*
			 * Getting the dictionary
			 */
			Alphabet dictionary = outsideFeatureDictionaryList.get(i);
			/*
			 * Getting the features in the dictionary
			 */
			Object[] features = dictionary.map.keys();
			for (Object feature : features) {
				String outsideFeature = (String) feature;
				/*
				 * getting the frequency of the feature in the dictionary
				 */
				int featureFreq = dictionary.countMap.get(outsideFeature);

				/*
				 * Getting the filtered dictionary
				 */
				Alphabet filteredDictionary = filteredFeatureDictionaryList
						.get(i);

				/*
				 * Thresholding the feature frequency
				 */
				if (featureFreq >= VSMContant.THRESHOLD_FREQUENCY) {
					/*
					 * Store the feature in the filtered dictionary
					 */
					filteredDictionary.lookupIndex(outsideFeature);
					filteredDictionary.countMap
							.put(outsideFeature, featureFreq);
				} else {
					/*
					 * Assign a class NOTFREQUENT to the feature and store the
					 * feature NOTFREQUENT instead of the true feature
					 */
					if (filteredDictionary.contains("NOTFREQUENT")) {
						filteredDictionary.countMap.put("NOTFREQUENT",
								filteredDictionary.countMap.get("NOTFREQUENT")
										+ featureFreq);
					} else {
						filteredDictionary.lookupIndex("NOTFREQUENT");
						filteredDictionary.countMap.put("NOTFREQUENT",
								featureFreq);
					}
				}
			}
		}
		return filteredFeatureDictionaryList;
	}

	@Override
	public void allowDictionaryGrowth(ArrayList<Alphabet> featureDictionary) {
		/*
		 * Allow the dictionary growth
		 */
		for (Alphabet dictionary : featureDictionary) {
			dictionary.allowGrowth();

		}

	}

}
