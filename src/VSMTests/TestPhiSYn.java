package VSMTests;

import VSMBinaryFeatureVectors.FeatureVectorBean;
import VSMSerialization.VSMReadSerialObject;

public class TestPhiSYn {

	public static void main(String... args) {

		String phiSynP = "/group/project/vsm-afs/binaryFeatureVectors/CC/CC_1/CC_13365.ser";

		FeatureVectorBean vectorBean = VSMReadSerialObject
				.readBinaryFeaturVec(phiSynP);

		System.out.println(vectorBean.getPhi());
		System.out.println(vectorBean.getPhiSem());
		System.out.println(vectorBean.getPsi());
		System.out.println(vectorBean.getPsiSem());
	}

}
