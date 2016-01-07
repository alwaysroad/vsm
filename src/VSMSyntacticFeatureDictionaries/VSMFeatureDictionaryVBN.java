package VSMSyntacticFeatureDictionaries;

import java.io.File;
import java.util.ArrayList;

import VSMConstants.VSMContant;
import VSMFeatureMaps.VSMExtractInsideFeatureDictionaryVBN;
import VSMFeatureMaps.VSMExtractOutsideFeatureDictionaryVBN;
import VSMInterfaces.ExtractDictionary;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMSerializeFeatureDictionary;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;

/**
 * The class that forms the inside and outside feature dictionary for each node
 * and serialize it in a .ser file for later use. This is a one time computation
 * and hence not parallelizing. Does not take much time anyways
 * 
 * @author sameerkhurana10
 *
 */
public class VSMFeatureDictionaryVBN {

	public static void main(String... args) throws Exception {

		System.out.println("***Extracting feature Dictionary for VBN*****");
		/*
		 * Checking whether the user has given any command line argument.
		 * Expecting the path the corpus containing files with trees
		 */
		String rootDirectory = null;

		VSMDictionaryBean alreadyExistingDicionaryBean = null;

		/*
		 * If the dictionary already exists and the user wants to add new
		 * features to the already existing dictionary
		 */
		String alreadyExistingDictionary = null;

		if (args.length > 1) {
			/*
			 * The corpus path /Users/sameerkhurana10/blipp_corpus/trees (in my
			 * laptop)
			 */
			rootDirectory = args[0];
			alreadyExistingDictionary = args[1];
		} else {
			rootDirectory = VSMContant.EXTRACTED_TREE_CORPUS;
			alreadyExistingDictionary = VSMContant.FEATURE_DICT_VBN;
		}

		/*
		 * Already existing dictionary
		 */
		File dictionaryFile = new File(alreadyExistingDictionary);

		/*
		 * If a dictionary already exists then we would like to add the features
		 * to the already existing dictionary instead of creating the whole
		 * dictionary again
		 */
		if (dictionaryFile.exists()) {
			alreadyExistingDicionaryBean = VSMReadSerialMatrix
					.readSerializedDictionary(alreadyExistingDictionary);
			System.out.println("****Dictionary already ecists******");
		}

		/*
		 * Forming the inside and outside feature dictionaries
		 */
		ExtractDictionary extractInsideDict = new VSMExtractInsideFeatureDictionaryVBN();
		ExtractDictionary extractOutsideDict = new VSMExtractOutsideFeatureDictionaryVBN();

		System.out.println("**Dictionary Extraction**");

		/*
		 * Getting the tree paths
		 */
		ArrayList<String> treeFilePaths = VSMUtil.getFilePaths(new File(
				rootDirectory).listFiles());

		ArrayList<Alphabet> insideFeatureDictionary = extractInsideDict
				.getUpdatedFeatureDictionary(treeFilePaths,
						alreadyExistingDicionaryBean);
		ArrayList<Alphabet> outsideFeatureDictionary = extractOutsideDict
				.getUpdatedFeatureDictionary(treeFilePaths,
						alreadyExistingDicionaryBean);

		System.out.println("**Dictionary Extraction Done**");

		/*
		 * We got the dictionaries in the previous step, now it is time to
		 * serialize them. The dictionaries are stored in the below bean
		 */
		VSMDictionaryBean dictionaryBean = new VSMDictionaryBean();

		/*
		 * Storing the inside and outside feature dictionaries in a bean, which
		 * is to be serialized. Adding some new information to the dictionary
		 * bean i.e. the size of the dictionaries. The sized would be needed at
		 * the time of matrix creations
		 */
		dictionaryBean.setInsideFeatureDictionary(insideFeatureDictionary);
		dictionaryBean.setOutsideFeatureDictionary(outsideFeatureDictionary);
		dictionaryBean.setInsideDictionarySize(VSMUtil
				.getDictionarySize(insideFeatureDictionary));
		dictionaryBean.setOutsideDictionarySize(VSMUtil
				.getDictionarySize(outsideFeatureDictionary));

		/*
		 * Serializing the bean for future use, so that we do not have to
		 * extract the features again from the corpus.
		 */
		VSMSerializeFeatureDictionary featureDictionary = new VSMSerializeFeatureDictionary();

		System.out.println("***Serializing Dictionary Bean ***");
		featureDictionary.serializeFeatureDictionary(dictionaryBean, "vbn");
		
		VSMUtil.writeFeatureDictionary(dictionaryBean, "vbn");

	}
}