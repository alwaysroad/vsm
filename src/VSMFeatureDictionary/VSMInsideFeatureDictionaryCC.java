package VSMFeatureDictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import VSMConstants.VSMContant;
import VSMFeatureStores.VSMInsideFeatureObjStore;
import VSMInsideFeatureObjects.InsideBinFull;
import VSMInsideFeatureObjects.InsideBinLeft;
import VSMInsideFeatureObjects.InsideBinLeftPlus;
import VSMInsideFeatureObjects.InsideBinRight;
import VSMInsideFeatureObjects.InsideBinRightPlus;
import VSMInsideFeatureObjects.InsideNtHeadPos;
import VSMInsideFeatureObjects.InsideNtNumOfWords;
import VSMInsideFeatureObjects.InsideUnary;
import VSMInterfaces.InsideFeatureDictionary;
import VSMInterfaces.InsideFeatureObject;
import VSMInterfaces.InsideFeatureObjectStore;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.PTBTreeNormaliser;
import VSMUtilityClasses.VSMUtil;
import edu.berkeley.nlp.syntax.Constituent;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import edu.berkeley.nlp.syntax.Trees.PennTreeReader;

public class VSMInsideFeatureDictionaryCC implements InsideFeatureDictionary {

	private ArrayList<Alphabet> insideFeatureDictionaryList;
	private InsideFeatureObjectStore insideFeatureObjStore;
	private ArrayList<Alphabet> filteredFeatureDictionaryList;
	private ArrayList<InsideFeatureObject> objectStore;
	private ArrayList<InsideFeatureObject> updatedObjectStore;
	private Trees.StandardTreeNormalizer obj;
	private PTBTreeNormaliser transformedTree;

	public VSMInsideFeatureDictionaryCC() {

		this.obj = new Trees.StandardTreeNormalizer();

		this.transformedTree = new PTBTreeNormaliser(true);

		/*
		 * The connection between the objects and the object store in which they
		 * register. The store is instantiated. The store with which the objects
		 * register
		 */
		this.insideFeatureObjStore = new VSMInsideFeatureObjStore();

		/*
		 * Instantiating the data structure, a list, that is used to store the
		 * inside dictionaries that associated with the feature objects in the
		 * store. Other objects access this reference variable when they want to
		 * access the non filtered dictionary
		 */
		this.insideFeatureDictionaryList = new ArrayList<Alphabet>();

		/*
		 * Instantiating the data structure that is used to store all the
		 * filtered dictionaries. Other class objects access this data structure
		 * when they want to access the filtered dictionatries
		 */
		this.filteredFeatureDictionaryList = new ArrayList<Alphabet>();

		/*
		 * Creating the object store
		 */
		new InsideNtNumOfWords(insideFeatureObjStore);
		new InsideBinRightPlus(insideFeatureObjStore);
		new InsideNtHeadPos(insideFeatureObjStore);
		new InsideBinFull(insideFeatureObjStore);
		new InsideBinLeft(insideFeatureObjStore);
		new InsideBinLeftPlus(insideFeatureObjStore);
		new InsideBinRight(insideFeatureObjStore);
		new InsideUnary(insideFeatureObjStore);

		/*
		 * Referencing the object store using a reference variable declared in
		 * this object. The outside objects access the object store by accessing
		 * the instance variable objectStore
		 */
		this.objectStore = this.insideFeatureObjStore
				.getInsideFeatureObjectStore();
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
		 * Iterate over the trees and take the 400 trees.txt files to form the
		 * feature dictionary
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

			/*
			 * Constituents Map is needed later
			 */
			if (syntaxTree != null) {

				/*
				 * Getting the constituent map corresponding to the syntax tree
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
					 * Very important step
					 */
					VSMUtil.setConstituentLength(constituentsMap
							.get(insideTree));

					/*
					 * Now we need to extract the inside features from this
					 * inside tree, Iterate over the object store. This if
					 * statement was added because the if we had a leaf for an
					 * inside tree we got a null pointer exception from the
					 * AbstractHeadFinder class. Trying to extract the
					 * dictionary for only NP
					 */
					if (!insideTree.isLeaf()
							&& insideTree.getLabel().equalsIgnoreCase("CC")) {
						/*
						 * Updating the objectStore, one featureDictionary at a
						 * time
						 */
						for (InsideFeatureObject insideFeatureObj : objectStore) {

							/*
							 * Getting the dictionary object from the feature
							 * object in the object store
							 */
							Alphabet featureDictionary = insideFeatureObj
									.getFeatureDictionary();

							/*
							 * Getting the feature by calling the feature
							 * object's getInideFeature() function
							 */
							String feature = insideFeatureObj.getInsideFeature(
									insideTree, insideTree.isPreTerminal());

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
	public ArrayList<InsideFeatureObject> getUpdatedObjectStore() {

		/*
		 * Getting the reference to the updated object store which is reference
		 * by the variable updatedObjectStore
		 */
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
	public ArrayList<Alphabet> getInsideFeatureDictionaryList(
			ArrayList<InsideFeatureObject> updatedObjectStore) {

		/*
		 * Very important to instantiate a new list object here otherwise, we
		 * will just keep adding objects to the list instantiated in the
		 * constructor. This was a source of error while testing
		 */
		ArrayList<Alphabet> insideFeatureDictionaryList = new ArrayList<Alphabet>();

		/*
		 * Extracting the feature dictionaries from the object store and adding
		 * to the ArrayList data structure
		 */
		for (InsideFeatureObject insideFeatureObj : updatedObjectStore) {
			insideFeatureDictionaryList.add(insideFeatureObj
					.getFeatureDictionary());
		}

		/*
		 * insideFeatureDictionaryList referring to the data
		 * insideFeatureDcitionaryList
		 */
		// this.insideFeatureDictionaryList = insideFeatureDictionaryList;
		return insideFeatureDictionaryList;
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
			ArrayList<Alphabet> insideFeatureDictionaryList) {
		/*
		 * Stopping the dictionary growth
		 */
		for (Alphabet dictionary : insideFeatureDictionaryList) {
			dictionary.stopGrowth();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see VSMInterfaces.InsideFeatureDictionary#getInsideFeatureObjectStore()
	 */
	@Override
	public ArrayList<InsideFeatureObject> getInsideFeatureObjectStore() {
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
			ArrayList<InsideFeatureObject> insideFeatureObjStore) {

		/*
		 * Creating the list here, no need to make it an instance variable
		 */
		ArrayList<Alphabet> filteredFeatureDictionaryList = new ArrayList<Alphabet>();

		/*
		 * Extracting the filtered feature dictionary from the feature object
		 * store and adding that to the filteredDictionaryList
		 */
		for (InsideFeatureObject insideFeatureObj : insideFeatureObjStore) {
			filteredFeatureDictionaryList.add(insideFeatureObj
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
	public ArrayList<Alphabet> filterInsideFeatureDictionary(
			ArrayList<Alphabet> insideFeatureDictionaryList,
			ArrayList<Alphabet> filteredFeatureDictionaryList) {

		for (int i = 0; i < insideFeatureDictionaryList.size(); i++) {

			/*
			 * Getting the dictionary
			 */
			Alphabet dictionary = insideFeatureDictionaryList.get(i);

			/*
			 * Getting the features in the dictionary
			 */
			Object[] features = dictionary.map.keys();

			for (Object feature : features) {
				String insideFeature = (String) feature;

				/*
				 * getting the frequency of the feature in the dictionary
				 */
				int featureFreq = dictionary.countMap.get(insideFeature);

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
					filteredDictionary.lookupIndex(insideFeature);
					filteredDictionary.countMap.put(insideFeature, featureFreq);
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
