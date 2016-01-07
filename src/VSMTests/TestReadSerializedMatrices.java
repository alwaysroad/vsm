package VSMTests;

import jeigen.SparseMatrixLil;
import static jeigen.Shortcuts.*;
import org.junit.Test;

import cc.mallet.types.SparseVector;
import Jama.Matrix;
import VSMSerialization.VSMFeatureVectorBean;

import VSMSerialization.VSMReadSerialObject;
import VSMUtilityClasses.VSMUtil;

public class TestReadSerializedMatrices {
	@Test
	public void testReadSerializedObject() throws ClassNotFoundException {
		System.out.println(java.lang.Runtime.getRuntime().maxMemory());
		String fileName = "/afs/inf.ed.ac.uk/group/project/vsm/serializedprojections/NN";
		Object[] objects = VSMUtil.deserializeCCAVariantsRun("NN");
		Matrix Y = (Matrix) objects[0];
		System.out.println(Y.getColumnDimension());
		System.out.println(Y.getRowDimension());
		Matrix Z = (Matrix) objects[1];
		System.out.println(Z.getColumnDimension());
		System.out.println(Z.getRowDimension());
		System.out.println(Y);
		System.out.println(Z);

		// SparseMatrixLil sm2 = sprand(5, 3);
		// sm2.t();
		// sm2.mmul(sm2.t());
	}
}
