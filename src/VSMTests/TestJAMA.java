package VSMTests;

import Jama.Matrix;

public class TestJAMA {

	public static void main(String... args) {
		double[][] d = new double[2][3];

		for (int j = 0; j < 2; j++) {
			d[0][j] = 1.0;

		}

		d[0][2] = 1.5;

		for (int k = 0; k < 3; k++) {
			d[1][k] = 2.0;
		}

		Matrix m = new Matrix(d);

		System.out.println(m);
		System.out.println(m.rank());
		for (double d1 : m.getRowPackedCopy()) {
			System.out.println(d1);
		}

	}
}
