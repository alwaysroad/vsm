package VSMTests;

import java.util.ArrayList;

import VSMUtilityClasses.VSMUtil;

public class TestLowerCaseMethod {

	public static void main(String... args) {
		ArrayList<String> wordList = new ArrayList<String>();

		wordList.add("Testing");
		wordList.add("LOWERcASE");
		wordList.add("MEthOD");
		wordList.add("9");

		ArrayList<String> newWordList = VSMUtil.lowercase(wordList);
		newWordList = VSMUtil.normalize(newWordList);

		for (String word : newWordList) {
			System.out.println(word);
		}

		for (String word : wordList) {
			System.out.println(word);
		}

	}
}
