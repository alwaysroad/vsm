package VSMSerialization;

import java.io.Serializable;

import no.uib.cipr.matrix.DenseVector;

public class VSMWordEmbeddingSem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2172651922100576452L;
	private DenseVector wordEmbeddingSem;

	public DenseVector getWordEmbeddingSem() {
		return wordEmbeddingSem;
	}

	public void setWordEmbeddingSem(DenseVector wordEmbeddingSem) {
		this.wordEmbeddingSem = wordEmbeddingSem;
	}

}
