package VSMWordEmbeddingsNew;

import no.uib.cipr.matrix.DenseVector;

public class WordEmbeddingBean implements java.io.Serializable {

	/**
	 * Just the serial version ID
	 */
	private static final long serialVersionUID = 6668680166183369134L;
	private DenseVector sentenceVector;

	public DenseVector getWordEmedding() {
		return sentenceVector;
	}

	public void setWordEmbedding(DenseVector sentenceVector) {
		this.sentenceVector = sentenceVector;
	}

}
