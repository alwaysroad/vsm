package VSMTests;

import java.util.ArrayList;

import org.junit.Test;

import VSMSerialization.VSMDictionaryBean;

import VSMSerialization.VSMReadSerialMatrix;
import VSMUtilityClasses.Alphabet;

public class TestReadFeatureMatrixSerialized {
	@Test
	public void testReadSerializedObject() {
		// String fileName =
		// "/Users/sameerkhurana10/Documents/featurematrix/dictionary.ser";
		String fileName = "/afs/inf.ed.ac.uk/group/project/vsm/featuredictionary/dictionary.ser";
		VSMDictionaryBean matrixBean = VSMReadSerialMatrix
				.readSerializedDictionary(fileName);
		ArrayList<Alphabet> insideDictionary = matrixBean
				.getInsideFeatureDictionary();
		ArrayList<Alphabet> outsideDictionary = matrixBean
				.getOutsideFeatureDictionary();
		int count = 0;
		for (Alphabet dict : insideDictionary) {
			count += dict.size();
		}
		System.out.println(count);

		int count1 = 0;
		for (Alphabet dict : outsideDictionary) {
			count1 += dict.size();
		}
		System.out.println(count1);
	}
}
