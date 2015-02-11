package com.atompacman.toolkat.misc;

public class StringHelper {

	public static String splitCamelCase(String str) {
		return splitCamelCase(str, 0);
	}
	
	public static String splitCamelCase(String str, int minLen) {
		StringBuilder sb = new StringBuilder();
		sb.append(str.charAt(0));
		
		for (int i = 1; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				boolean putSpace = true;
				int j;
				
				for (j = i; j >= 0 && (i - j + 1) != minLen; --j) {
					if (Character.isUpperCase(str.charAt(j))) {
						putSpace = false;
						break;
					}
				}
				if (putSpace && (i - j + 1) == minLen) {
					sb.append(' ');
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
