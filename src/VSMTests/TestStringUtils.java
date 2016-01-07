package VSMTests;

import org.apache.commons.lang3.StringUtils;

public class TestStringUtils {

	public static void main(String... args) {
		String word = ")";
		if (!StringUtils.isAlphanumeric(word)) {
			if (Character.isLetterOrDigit(word.charAt(0))) {
				System.out.println("**Do nothing**");
			} else {
				System.out.println("**We gotta remove you from the list***");
			}
		}
	}

}
