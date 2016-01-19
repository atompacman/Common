package com.atompacman.toolkat.misc;

public class StringHelper {

    public static String splitCamelCase(String str) {
        return splitCamelCase(str, true);
    }

    public static String splitCamelCase(String str, boolean splitSingleLetters) {
        StringBuilder sb = new StringBuilder(str.length());
        sb.append(str.charAt(0));
        
        for (int i = 1; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (splitSingleLetters || Character.isLowerCase(str.charAt(i-1)) || 
                        (i < str.length() - 1 && Character.isLowerCase(str.charAt(i+1)))) {
                    sb.append(' ');
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String splitClassName(Object obj) {
        return splitClassName(obj, false);
    }
    
    public static String splitClassName(Object obj, boolean splitSingleLetters) {
        return splitCamelCase(obj.getClass().getSimpleName(), splitSingleLetters);
    }

    public static String capitalize(String word) {
        if (word == null || word.isEmpty() || word.charAt(0) < 'a' || word.charAt(0) > 'z') {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String title(String title) {
        return title(title, 0);
    }

    public static String title(String title, int spacesBetweenDots) {
        StringBuilder line = new StringBuilder();
        final int totalLen = 100;

        line.append('=');

        while ((line.length() + spacesBetweenDots) < totalLen) {
            for (int i = 0; i < spacesBetweenDots; ++i) {
                line.append(' ');
            }
            line.append('=');
        }
        while (line.length() < totalLen - 1) {
            line.append(' ');
        }
        if (line.length() == totalLen - 1) {
            line.append('=');
        }

        if (title != null && title.length() != 0) {
            title = " " + title + " ";
        } else {
            title = "";
        }
        int titleLength = title.length();
        int titleStartPos = (totalLen - titleLength + 1) / 2;

        if (titleStartPos < 0) {
            line.replace(0, title.length() - 1, title.substring(1));
        } else {
            line.replace(titleStartPos, titleStartPos + titleLength, title);
        }

        return line.toString();
    }
}
