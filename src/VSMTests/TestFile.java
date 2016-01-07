package VSMTests;

import java.io.File;

import VSMConstants.VSMContant;

public class TestFile {

	public static void main(String... args) {
		String URL = VSMContant.WORD_VECS_EMBEDDED_CHUNKS;

		File file = new File(URL);
		System.out.println(file.getParent()+"_WordEmbeddings");
	}

}
