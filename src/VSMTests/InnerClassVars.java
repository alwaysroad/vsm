package VSMTests;

import VSMSerialization.VSMFeatureVectorBean;

public class InnerClassVars {

	private VSMFeatureVectorBean[] miniBatch;

	public InnerClassVars(VSMFeatureVectorBean[] miniBatch) {
		this.miniBatch = miniBatch;
	}

	public VSMFeatureVectorBean[] getMinibatch() {
		return this.miniBatch;
	}
}
