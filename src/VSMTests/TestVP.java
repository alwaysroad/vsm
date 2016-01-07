package VSMTests;

import java.io.File;
import java.io.FileFilter;

public class TestVP {

	public static void main(String... args) {
		String chunkPath = "/afs/inf.ed.ac.uk/group/project/vsm.restored/SICKBinarySentenceVecs/S_492/VP";
		File[] files = new File(chunkPath).listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				// TODO Auto-generated method stub
				return !file.isHidden();
			}
		});

		System.out.println("****vectors***" + files);
	}

}
