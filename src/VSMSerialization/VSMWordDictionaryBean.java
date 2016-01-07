package VSMSerialization;

import java.util.ArrayList;

import VSMUtilityClasses.Alphabet;

public class VSMWordDictionaryBean implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8126718653290465720L;

	private Alphabet wordDictionary;

	public void setWordList(Alphabet wordDictionary) {
		this.wordDictionary = wordDictionary;
	}

	public Alphabet getWordDictionary() {
		// TODO Auto-generated method stub
		return this.wordDictionary;
	}
}
