package xyz.srclab.common.lang.chars;

import xyz.srclab.common.base.Checker;

public interface CharsRef extends CharSequence {

    static CharsRef of(CharSequence chars, int start, int end) {
        Checker.checkSubBounds(chars.length(), start, end);
        return new DefaultCharsRef(chars, start, end);
    }

    int indexOf(char c);

    int indexOf(String string);

    int lastIndexOf(char c);

    int lastIndexOf(String string);

    CharsRef trim();
}
