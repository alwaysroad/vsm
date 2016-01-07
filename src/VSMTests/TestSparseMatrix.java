package VSMTests;

import no.uib.cipr.matrix.sparse.SparseVector;
import jeigen.SparseMatrixLil;

public class TestSparseMatrix {
	private static SparseMatrixLil sm;

	private static SparseVector vec;

	public static void main(String... args) {

		sm = new SparseMatrixLil("1 2 3 1;4 5 6 0");

		// sm.append(0, 1, 1.0);
		//
		// sm.append(1, 1, 4.0);
		//
		// sm.append(1, 2, 2.0);
		//
		// sm.append(5, 6, 1.0);
		//
		// sm.append(5, 9, 1.0);
		System.out.println(sm);
		System.out.print(sm.getColIdx(1) + " ");
		System.out.print(sm.getRowIdx(1) + " ");
		System.out.println(sm.getValue(1));
		System.out.println(sm.getSize());
	}
}
