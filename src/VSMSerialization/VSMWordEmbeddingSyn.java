package VSMSerialization;

import java.io.Serializable;

import no.uib.cipr.matrix.DenseVector;

public class VSMWordEmbeddingSyn implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4497064505074068848L;
	private DenseVector wordEmbeddingSyn;

	public DenseVector getWordEmbeddingSyn() {
		return wordEmbeddingSyn;
	}

	public void setWordEmbeddingSem(DenseVector wordEmbeddingSyn) {
		this.wordEmbeddingSyn = wordEmbeddingSyn;
	}

}
