package VSMPhraseEmbeddings;

import no.uib.cipr.matrix.DenseVector;

public class PhraseEmbeddingBean implements java.io.Serializable {

	/**
	 * Just the serial version ID
	 */
	private static final long serialVersionUID = 6668680166183369134L;
	private DenseVector phraseVector;

	public DenseVector getWordEmedding() {
		return phraseVector;
	}

	public void setPhraseEmbedding(DenseVector sentenceVector) {
		this.phraseVector = sentenceVector;
	}

}
