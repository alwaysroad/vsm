package TestingJeigen;

import static jeigen.Shortcuts.*;
import jeigen.DenseMatrix;
import jeigen.SparseMatrixLil;

public class TestMatrices {
	public static void main(String... args) {
		DenseMatrix dm1 = null;
		DenseMatrix dm2 = null;

		dm1 = new DenseMatrix("1 2; 3 4"); // create new matrix
		// with rows {1,2} and {3,4}
		dm1 = new DenseMatrix(new double[][] { { 1, 2 }, { 3, 4 } }); // create
																		// new
																		// matrix
		// with rows {1,2} and {3,4}
		SparseMatrixLil sm1;
		sm1 = spdiag(rand(5, 1));
		System.out.println(sm1);
		System.out.println(sm1.mmul(sm1.t()));
	}
}
