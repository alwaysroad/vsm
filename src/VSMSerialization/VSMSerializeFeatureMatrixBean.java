//package VSMSerialization;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.util.LinkedHashMap;
//
//public class VSMSerializeFeatureMatrixBean {
//
//	public static void serializeMatrixBean(VSMMatrixBean matrixBean) {
//
//		File file = new File(
//				"/Users/sameerkhurana10/Documents/serializematrices/"
//						+ matrixBean.getNodeLabel());
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//
//		/*
//		 * The .ser file name
//		 */
//		String filename = file.getAbsolutePath() + "/" + matrixBean.getNodeLabel()
//				+ "_matrix.ser";
//
//		/*
//		 * Serializing the object
//		 */
//		FileOutputStream fos = null;
//		ObjectOutputStream out = null;
//
//		try {
//
//			fos = new FileOutputStream(filename);
//			out = new ObjectOutputStream(fos);
//			out.writeObject(matrixBean);
//			out.close();
//			fos.close();
//
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		}
//
//	}
//
// }
