package VSMTests;

import java.util.ArrayList;
import java.util.Collection;

import no.uib.cipr.matrix.DenseVector;
import VSMUtilityClasses.VSMUtil;

public class TestGoldStandardExtraction {

	public static void main(String... args) {
		Collection<Double> goldVec = VSMUtil.getGoldStandard().values();
		DenseVector goldVector = new DenseVector(goldVec.size());

		int idx = 0;
		for (double gold : goldVec) {
			goldVector.add(idx, gold);
			idx++;
		}

		System.out.println(goldVector.size());
	}

}
