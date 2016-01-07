package VSMCorpusExtraction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.CompressorException;

import VSMConstants.VSMContant;
import VSMLogger.VSMLogger;
import VSMUtilityClasses.VSMUtil;

public class CorpusExtraction {

	private static final Logger LOGGER;

	private static ArrayList<String> bllipCorpus;

	static {
		LOGGER = VSMLogger.setup(CorpusExtraction.class.getName());
	}

	public static void main(String... args) {

		bllipCorpus = VSMUtil.getBLLIPCorpus(VSMContant.BLLIP_CORPUS);

		LOGGER.info("Total Files in the BLLIP Corpus: " + bllipCorpus.size());

		for (String file : bllipCorpus) {

			System.out.println(file);

			try {
				VSMUtil.extractAndAddTrees(file);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE,
						"Exception While Reading the Following Corpus File: "
								+ file);
			} catch (CompressorException e) {
				LOGGER.log(Level.SEVERE, "Exception While Uncompressing: "
						+ file);
			}

		}
	}

}
