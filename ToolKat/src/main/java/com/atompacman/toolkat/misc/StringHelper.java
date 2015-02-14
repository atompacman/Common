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
}
