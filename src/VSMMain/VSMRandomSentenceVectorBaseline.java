package VSMMain;

import java.io.File;
import java.io.FileFilter;
import java.util.Random;

import no.uib.cipr.matrix.DenseVector;
import VSMConstants.VSMContant;
import VSMSerialization.VSMRandomSentenceBean;
import VSMSerialization.VSMSerializeFeatureVectorBeanTraining;
import VSMUtilityClasses.VSMUtil;

public class VSMRandomSentenceVectorBaseline {

	public static void main(String[] args) {

		Random r = new Random();

		VSMSerializeFeatureVectorBeanTraining serialize = new VSMSerializeFeatureVectorBeanTraining();

		File[] sentenceVecs = new File(VSMContant.SICK_BINARY_SENT_VECS)
				.listFiles(new FileFilter() {

					@Override
					public boolean accept(File file) {
						// TODO Auto-generated method stub
						return !file.isHidden();
					}
				});

		for (File file : sentenceVecs) {

			String sentence = file.getName();

			String path = VSMContant.RANDOM_SENTENCE_VECS + "/" + sentence
					+ ".ser";

			DenseVector sentenceVector = new DenseVector(2200);

			for (int i = 0; i < sentenceVector.size(); i++) {
				sentenceVector.add(i, VSMUtil.randomDouble(r, -1, 1));
			}

			VSMRandomSentenceBean vectorBean = new VSMRandomSentenceBean();
			vectorBean.setRandomSentenceVec(sentenceVector);
			/*
			 * Serialize the random vector
			 */
			System.out.println("***Serializing the random sentence vector***");
			serialize.serializeVectorBeanSentenceRand(vectorBean, path);
		}

	}

}
