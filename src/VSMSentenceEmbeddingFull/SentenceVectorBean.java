package VSMSentenceEmbeddingFull;

import no.uib.cipr.matrix.DenseVector;

public class SentenceVectorBean implements java.io.Serializable {

	/**
	 * Just the serial version ID
	 */
	private static final long serialVersionUID = 6668680166183369134L;
	private DenseVector sentenceVector;

	public DenseVector getSentenceVector() {
		return sentenceVector;
	}

	public void setSentenceVector(DenseVector sentenceVector) {
		this.sentenceVector = sentenceVector;
	}

}
