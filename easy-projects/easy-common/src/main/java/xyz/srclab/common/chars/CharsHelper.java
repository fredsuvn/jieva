package xyz.srclab.common.chars;

import xyz.srclab.common.base.Checker;

public class CharsHelper {

    public static int indexOf(CharSequence charSequence, int from, int to, char c) {
        Checker.checkSubBounds(charSequence.length(), from, to);
        for (int i = from; i < to; i++) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, int from, int to, char c) {
        Checker.checkSubBounds(charSequence.length(), from, to);
        for (int i = to - 1; i >= from; i--) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence charSequence, int from, int to, CharSequence chars) {
        Checker.checkSubBounds(charSequence.length(), from, to);
        int charsLength = chars.length();
        if (to - from < charsLength) {
            return -1;
        }
        int possibleTo = to - charsLength + 1;
        for (int i = from; i < possibleTo; i++) {
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

    public static int lastIndexOf(CharSequence charSequence, int from, int to, CharSequence chars) {
        Checker.checkSubBounds(charSequence.length(), from, to);
        int charsLength = chars.length();
        if (to - from < charsLength) {
            return -1;
        }
        int possibleFrom = from + charsLength - 1;
        for (int i = to - 1; i >= possibleFrom; i--) {
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
