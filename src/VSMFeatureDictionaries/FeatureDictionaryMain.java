package VSMFeatureDictionaries;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import VSMInterfaces.ExtractDictionary;
import VSMLogger.VSMLogger;
import VSMSerialization.VSMDictionaryBean;
import VSMSerialization.VSMReadSerialMatrix;
import VSMSerialization.VSMSerializeFeatureDictionary;
import VSMUtilityClasses.Alphabet;
import VSMUtilityClasses.VSMUtil;

public class FeatureDictionaryMain {

	private static final Logger LOGGER;

	private static String nonTerminal;

	private static VSMDictionaryBean existingDictionaryBean;

	private static String existingDictionaryPath;

	private static ArrayList<String> parsedCorpus;

	private static ArrayList<Alphabet> insideFeatureDictionary;

	private static ArrayList<Alphabet> outsideFeatureDictionary;

	static {
		LOGGER = VSMLogger.setup(FeatureDictionaryMain.class.getName());
	}

	public static void main(String... args) throws Exception {

		nonTerminal = VSMUtil.getNonTerminal(args);

		existingDictionaryPath = "/afs/inf.ed.ac.uk/group/project/vsm-afs/featuredictionary/" + nonTerminal
				+ "/dictionary.ser";

		if (new File(existingDictionaryPath).exists())
			existingDictionaryBean = ReadSerializedDictionary.readSerializedDictionary(existingDictionaryPath, LOGGER);

		/*
		 * TODO baked up some code, just to remove errors. Check later
		 */
		File[] files = VSMUtil.getTreeCorpus();
		ArrayList<String> parseCorpusList = new ArrayList<String>();
		for (File file : files) {
			parseCorpusList.add(file.getAbsolutePath());
		}
		parsedCorpus = parseCorpusList;

		if (parsedCorpus == null) {
			LOGGER.severe("Something wrong with the file system, could not get the tree corpus");
		}

		LOGGER.info("Extracting the Feature Dictionary for the Non Terminal " + nonTerminal);

		extractDictionaries();

		serializeAndWriteDictionary();

	}

	private static void serializeAndWriteDictionary() {

		VSMDictionaryBean dictionaryBean = new VSMDictionaryBean();

		dictionaryBean.setInsideFeatureDictionary(insideFeatureDictionary);

		dictionaryBean.setOutsideFeatureDictionary(outsideFeatureDictionary);

		dictionaryBean.setInsideDictionarySize(VSMUtil.getDictionarySize(insideFeatureDictionary));

		dictionaryBean.setOutsideDictionarySize(VSMUtil.getDictionarySize(outsideFeatureDictionary));

		SerializeFeatureDictionary featureDictionary = new SerializeFeatureDictionary();

		featureDictionary.serializeFeatureDictionary(dictionaryBean, nonTerminal.toLowerCase());

		// VSMUtil.writeFeatureDictionary(dictionaryBean,
		// nonTerminal.toLowerCase());

	}

	private static void extractDictionaries() {

		ExtractDictionary extractInsideDict = new ExtractInsideFeatureDictionary(nonTerminal, LOGGER);

		ExtractDictionary extractOutsideDict = new ExtractOutsideFeatureDictionary(nonTerminal, LOGGER);

		try {

			insideFeatureDictionary = extractInsideDict.getUpdatedFeatureDictionary(parsedCorpus,
					existingDictionaryBean);
			outsideFeatureDictionary = extractOutsideDict.getUpdatedFeatureDictionary(parsedCorpus,
					existingDictionaryBean);

		} catch (Exception e) {
			LOGGER.severe("Exception while extracting the inside and outside feature dictionaries" + e);
		}

	}
}