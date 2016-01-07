package VSMTests;

import java.io.File;

import no.uib.cipr.matrix.DenseVector;
import VSMConstants.VSMContant;
import VSMSerialization.VSMReadSerialObject;
import VSMSerialization.VSMSentenceVecBean;

public class VSMTestSentenceVecCBOW {

	public static void main(String... args) {
		String fileName = VSMContant.SENTENCE_EMBED_CHUNKS_BOW;
		File[] sentences = new File(fileName).listFiles();
		VSMSentenceVecBean vecBean = VSMReadSerialObject
				.readSerializedSentenceVec(sentences[0].getAbsolutePath());
		DenseVector vec = vecBean.getSentenceVector();
		System.out.println("**hi**");
		System.out.println(vec);
	}

}
