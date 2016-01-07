//package VSMSerialization;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//
//public class VSMReadFeatureMatrix {
//	public static VSMMatrixBean readFeatureMatrix(String fileName) {
//
//		VSMMatrixBean matrixBean = null;
//		FileInputStream fis = null;
//		ObjectInputStream in = null;
//
//		try {
//			fis = new FileInputStream(fileName);
//			in = new ObjectInputStream(fis);
//			matrixBean = (VSMMatrixBean) in.readObject();
//			in.close();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} catch (ClassNotFoundException cnfe) {
//			cnfe.printStackTrace();
//		}
//
//		return matrixBean;
//	}
// }
