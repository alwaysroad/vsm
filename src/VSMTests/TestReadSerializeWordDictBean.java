package VSMTests;

import org.junit.Test;

import VSMSerialization.VSMReadSerialWordDict;
import VSMSerialization.VSMWordDictionaryBean;

public class TestReadSerializeWordDictBean {

	public static void main(String... args) {
		VSMWordDictionaryBean dictionaryBean = VSMReadSerialWordDict
				.readSerializedDictionary("/afs/inf.ed.ac.uk/group/project/vsm/worddictionary/worddictionary.ser");
		System.out.println(dictionaryBean.getWordDictionary().size());
	}

}
