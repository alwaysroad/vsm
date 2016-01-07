package VSMTests;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class VSMTestStringUtils {

	public static void main(String... args) {
		List<String> wordList = new ArrayList<String>();
		wordList.add("a");
		wordList.add("boy");

		String string = StringUtils.join(wordList.toArray(), "_");
		System.out.println(string);
	}

}
