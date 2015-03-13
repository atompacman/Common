package com.atompacman.toolkat.misc;

public class StringHelper {

	public static String splitCamelCase(String str) {
		return splitCamelCase(str, true);
	}

	public static String splitCamelCase(String str, boolean splitSingleLetters) {
		StringBuilder sb = new StringBuilder();
		sb.append(str.charAt(0));

		for (int i = 1; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				if (splitSingleLetters || Character.isLowerCase(str.charAt(i-1))) {
					sb.append(' ');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String capitalize(String word) {
		if (word == null || word.isEmpty()) {
			return word;
		}
		if (word.charAt(0) < 'a' || word.charAt(0) > 'z') {
			return word;
		}
		
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
}
