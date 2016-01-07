package VSMFeatureMatrices;

import jeigen.SparseMatrixLil;

public class VSMSparseMatrix extends SparseMatrixLil implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4599852225519300490L;

	/**
	 * 
	 * @param rows
	 * @param cols
	 */
	public VSMSparseMatrix(int rows, int cols) {
		super(rows, cols);
	}

}
