package VSMTests;

import VSMSerialization.VSMFeatureVectorBeanEmbedded;
import VSMSerialization.VSMReadSerialObject;

public class VSMTestFeatureVecBeanEmbed {

	public static void main(String... args) {
		String URI = "/afs/inf.ed.ac.uk/group/project/vsm.restored/SICKSyntacticOnlyEmbeddedFeaturevecs/S_1/JJ/JJ_1.ser";

		VSMFeatureVectorBeanEmbedded vectorBean = VSMReadSerialObject
				.readFeatureVectorBeanEmbedded(URI);

		System.out.println(vectorBean.getPhiEmbedded());
	}

}
