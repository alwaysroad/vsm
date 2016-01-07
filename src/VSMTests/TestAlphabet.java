package VSMTests;

import VSMUtilityClasses.Alphabet;

public class TestAlphabet {

	public static void main(String... args) {
		Alphabet dict = new Alphabet();
		dict.allowGrowth();
		dict.turnOnCounts();

		String[] words = new String[] { "sam", "test", "alpha" };

		for (String word : words) {
			System.out.println(word);
			if (!dict.contains(word)) {
				dict.lookupIndex(word);
			}

		}

		System.out.println(dict.reverseMap);
	}
}
