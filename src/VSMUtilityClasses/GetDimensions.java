package VSMUtilityClasses;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import Jama.Matrix;

public class GetDimensions {

	public static void main(String... args) {
		String file = "/group/project/vsm-afs/projectionMatrices" + "/"
				+ args[0] + "/" + "projections" + args[1].toLowerCase()
				+ ".ser";

		Matrix[] projections = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			projections = (Matrix[]) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}

		System.out.println(projections[0].getColumnDimension() + "*"
				+ projections[0].getRowDimension());

		System.out.println(projections[1].getColumnDimension() + "*"
				+ projections[1].getRowDimension());

	}
}
