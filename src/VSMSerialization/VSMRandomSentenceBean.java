package VSMSerialization;

import java.io.Serializable;

import no.uib.cipr.matrix.DenseVector;

public class VSMRandomSentenceBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1613998014911796295L;
	
	private DenseVector randomSentenceVec;

	public DenseVector getRandomSentenceVec() {
		return randomSentenceVec;
	}

	public void setRandomSentenceVec(DenseVector randomSentenceVec) {
		this.randomSentenceVec = randomSentenceVec;
	}

}
