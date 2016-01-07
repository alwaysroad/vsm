package VSMTests;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.commons.compress.compressors.CompressorException;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import VSMUtilityClasses.VSMUtil;

public class TestCorpusExtractionReverse {

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
		/*
		 * Pass the files array into the getFilePaths method that returns a list
		 * of all the file paths in the BLLIP corpus
		 */
		ArrayList<String> filePaths = VSMUtil.getFilePaths(files);

		java.util.ListIterator<String> li = filePaths.listIterator(filePaths
				.size());

		System.out.println(filePaths.size());
		/*
		 * Testing
		 */
		int count = 0;
		while (li.hasPrevious()) {
			System.out.println(li.previous());
			count += 1;

			/*
			 * Handling the exception, for some .bz2, it is not able to
			 * uncompress them
			 */
			try {
				VSMUtil.extractAndAddTrees(li.previous());
			} catch (IOException e) {
				System.out.println("***Catching the exception, IOEXCEPTION**"
						+ e + li.previous());
			} catch (CompressorException e) {
				System.out
						.println("***Exception while uncompressing the .bz2 file**"
								+ e + li.previous());
			}
			/*
			 * Looking for a 100000 tree files
			 */
			if (count == 100000) {
				break;
			}
		}
	}

}
