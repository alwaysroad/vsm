package VSMSerialization;

import java.util.LinkedHashMap;

/**
 * Class used to create the count map object that is serialized
 * 
 * @author sameerkhurana10
 *
 */
public class VSMCountMap implements java.io.Serializable {
	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 6147911206522096336L;

	private LinkedHashMap<String, Integer> countMapObject;

	public LinkedHashMap<String, Integer> getCountMap() {
		return countMapObject;
	}

	public void setCountMap(LinkedHashMap<String, Integer> countMap) {
		this.countMapObject = countMap;
	}

}
