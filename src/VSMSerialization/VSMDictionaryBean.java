package VSMSerialization;

import java.util.ArrayList;

import VSMUtilityClasses.Alphabet;

public class VSMDictionaryBean implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7386981880325004682L;
	private ArrayList<Alphabet> insideFeatureDictionary;
	private ArrayList<Alphabet> outsideFeatureDictionary;
	private long insideDictionarySize;
	private long outsideDictionarySize;

	public ArrayList<Alphabet> getInsideFeatureDictionary() {
		return insideFeatureDictionary;
	}

	public void setInsideFeatureDictionary(
			ArrayList<Alphabet> insideFeatureDictionary) {
		this.insideFeatureDictionary = insideFeatureDictionary;
	}

	public ArrayList<Alphabet> getOutsideFeatureDictionary() {
		return outsideFeatureDictionary;
	}

	public void setOutsideFeatureDictionary(
			ArrayList<Alphabet> outsideFeatureDictionary) {
		this.outsideFeatureDictionary = outsideFeatureDictionary;
	}

	public long getInsideDictionarySize() {
		return insideDictionarySize;
	}

	public long getOutsideDictionarySize() {
		return outsideDictionarySize;
	}

	public void setOutsideDictionarySize(long outsideDictionarySize) {
		this.outsideDictionarySize = outsideDictionarySize;
	}

	public void setInsideDictionarySize(long dictionarySize) {
		this.insideDictionarySize = dictionarySize;
	}

}
