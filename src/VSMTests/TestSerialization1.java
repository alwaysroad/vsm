package VSMTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import VSMUtilityClasses.VSMUtil;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import jeigen.SparseMatrixLil;

public class TestSerialization1 {

	public static void main(String... args) {

		SparseMatrixLil sm = new SparseMatrixLil(5, 4);

		for (int i = 0; i < sm.rows; i++) {
			for (int j = 0; j < sm.cols; j++) {
				sm.append(i, j, 1.0);
			}
		}
		FlexCompRowMatrix matrix = VSMUtil.createSparseMatrixMTJFromJeigen(sm);

		for (MatrixEntry e : matrix) {
			System.out.println(e.row() + " " + e.column());
		}

		// System.out.println(sm.getRowIdxs().length);

		try {
			ObjectOutput output = new ObjectOutputStream(new FileOutputStream(
					new File("test.ser"), false));
			output.writeObject(sm);
			output.close();

			ObjectInput input = new ObjectInputStream(new FileInputStream(
					new File("test.ser")));
			SparseMatrixLil sm1 = (SparseMatrixLil) input.readObject();
			input.close();
			// System.out.println(sm1);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
