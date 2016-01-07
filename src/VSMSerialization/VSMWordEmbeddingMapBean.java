package VSMSerialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.netlib.util.doubleW;

public class VSMWordEmbeddingMapBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8813395693981086715L;
	
	private HashMap<String, double[]> embeddingsMap;

	public HashMap<String, double[]> getEmbeddingsMap() {
		return embeddingsMap;
	}

	public void setEmbeddingsMap(HashMap<String, double[]> embeddingsMap) {
		this.embeddingsMap = embeddingsMap;
	}

}
