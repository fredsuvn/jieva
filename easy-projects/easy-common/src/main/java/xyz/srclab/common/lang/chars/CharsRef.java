package xyz.srclab.common.lang.chars;

import xyz.srclab.common.base.Checker;

public interface CharsRef extends CharSequence {

    static CharsRef of(CharSequence chars, int start, int end) {
        Checker.checkSubBounds(chars.length(), start, end);
        return new DefaultCharsRef(chars, start, end);
    }

    static CharsRef of(CharSequence chars, int start) {
        return of(chars, start, chars.length());
    }

    @Override
    CharsRef subSequence(int start, int end);

    default CharsRef subSequence(int start) {
        return subSequence(start, length());
    }

    int indexOf(char c);

    int indexOf(String string);

    int lastIndexOf(char c);

    int lastIndexOf(String string);

    CharsRef trim();
}
