package VSMTests;

import VSMUtilityClasses.Alphabet;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

public class TestVec {
	private static Vector x;

	public static void main(String... args) {
		Vector vec = new DenseVector(10);
		for (int i = 0; i < vec.size(); i++) {
			vec.set(i, i);
		}

		System.out.println(vec);
		Alphabet y = new Alphabet();
		y.allowGrowth();

		for (int i = 0; i <= 4; i++) {
			String x = "in1";
			if (y.contains("in1")) {
				// System.out.println("contains");
			} else {
				// System.out.println("not contains");
				y.lookupIndex(x);
			}
		}
	}
}
