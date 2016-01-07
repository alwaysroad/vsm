package VSMTests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestPrintWriter {

	/*
	 * Testing whether print writer makes the file or not if the file does not
	 * already exist
	 */
	public static void main(String... args) {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(
					"/Users/sameerkhurana10/Documents/My Documents/My_Documents/vectorspacemodellingnlp/test.txt");
			writer.println("test");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			writer.flush();
			writer.close();
		}
	}
}
