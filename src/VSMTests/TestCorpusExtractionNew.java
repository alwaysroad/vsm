package VSMTests;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.compress.compressors.CompressorException;

import VSMUtilityClasses.VSMUtil;

public class TestCorpusExtractionNew {

	public static void main(String... args) {

		/*
		 * Checking whether the program is running on bravas or not
		 */
		InetAddress ip;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Your current IP address : " + ip);
			System.out.println("Your current Hostname : " + hostname);

		} catch (UnknownHostException e) {

			e.printStackTrace();
		}

		/*
		 * The directory root
		 */
		String directoryRoot = "/group/corpora/public/bllip/bllip_nanc_mem/data";

		/*
		 * Get all the directories into the data directory
		 */
		File[] files = VSMUtil.getFiles(directoryRoot);

		Collections.sort(Arrays.asList(files), new Comparator<File>() {
			public int compare(File file1, File file2) {
				String a = file1.getName().replaceFirst("^0+(?!$)", "");
				String b = file2.getName().replaceFirst("^0+(?!$)", "");
				return Integer.parseInt(a) - Integer.parseInt(b);
			}
		});

		for (File file : files) {
			System.out.println(file.getName());
		}

		ArrayList<File> filePaths = VSMUtil.getFilePathsCorpusSpecific(files,
				args[0]);

		System.out.println("***Total Files in BLLIP Corpus***"
				+ filePaths.size());
		/*
		 * Testing
		 */
		int count = 0;
		for (File file : filePaths) {
			// System.out.println(path);
			count += 1;
			// System.out.println(count);
			System.out.println(file.getName());
			VSMUtil.extractAndAddTreesCorpusSpecific(file.getAbsolutePath(),
					args[0]);

		}
	}

}
