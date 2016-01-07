package VSMTests;

import java.io.IOException;
import java.util.ArrayList;

import Jama.Matrix;
import VSMUtilityClasses.VSMUtil;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;

public class TestMATFile {

	public static void main(String... args) throws ClassNotFoundException,
			IOException {

		VSMUtil.createMatFileProjections("JJ");
		VSMUtil.createMatFileProjections("NNP");
		VSMUtil.createMatFileProjections("IN");
		VSMUtil.createMatFileProjections("S");
		VSMUtil.createMatFileProjections("VBN");
		VSMUtil.createMatFileProjections("NNS");

	}

}
