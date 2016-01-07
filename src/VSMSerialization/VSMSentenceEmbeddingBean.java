package VSMSerialization;

import java.io.Serializable;

import no.uib.cipr.matrix.DenseVector;

public class VSMSentenceEmbeddingBean implements Serializable {
	
	private DenseVector sentenceEmbedding;

	public DenseVector getSentenceEmbedding() {
		return sentenceEmbedding;
	}

	public void setSentenceEmbedding(DenseVector sentenceEmbedding) {
		this.sentenceEmbedding = sentenceEmbedding;
	}

}
