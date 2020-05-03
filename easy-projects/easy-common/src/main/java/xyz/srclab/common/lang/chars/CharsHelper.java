package xyz.srclab.common.lang.chars;

import xyz.srclab.common.base.Checker;

public class CharsHelper {

    public static int indexOf(CharSequence charSequence, int start, int end, char c) {
        Checker.checkSubBounds(charSequence.length(), start, end);
        for (int i = start; i < end; i++) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, int start, int end, char c) {
        Checker.checkSubBounds(charSequence.length(), start, end);
        for (int i = end - 1; i >= start; i--) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence charSequence, int start, int end, CharSequence chars) {
        Checker.checkSubBounds(charSequence.length(), start, end);
        int charsLength = chars.length();
        if (end - start < charsLength) {
            return -1;
        }
        int possibleTo = end - charsLength + 1;
        for (int i = start; i < possibleTo; i++) {
            int c = 0;
            for (int j = 0; j < charsLength; j++) {
                if (charSequence.charAt(i + j) != chars.charAt(j)) {
                    break;
                }
                c++;
            }
            if (c == charsLength) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, int start, int end, CharSequence chars) {
        Checker.checkSubBounds(charSequence.length(), start, end);
        int charsLength = chars.length();
        if (end - start < charsLength) {
            return -1;
        }
        int possibleFrom = start + charsLength - 1;
        for (int i = end - 1; i >= possibleFrom; i--) {
            int c = 0;
            int charsFrom = charsLength - 1;
            for (int j = charsFrom; j >= 0; j--) {
                if (charSequence.charAt(i - (charsFrom - j)) != chars.charAt(j)) {
                    break;
                }
                c++;
            }
            if (c == charsLength) {
                return i;
            }
        }
        return -1;
    }
}
