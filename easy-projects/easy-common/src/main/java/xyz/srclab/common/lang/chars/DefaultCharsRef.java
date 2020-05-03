package xyz.srclab.common.lang.chars;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;

final class DefaultCharsRef implements CharsRef {

    private final CharSequence source;
    private final int start;
    private final int end;

    private @Nullable CharSequence subCache;

    DefaultCharsRef(CharSequence source, int start, int end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public char charAt(int index) {
        return source.charAt(start + index);
    }

    @Override
    public CharsRef subSequence(int start, int end) {
        Checker.checkSubBounds(length(), start, end);
        return new DefaultCharsRef(this, start, end);
    }

    @Override
    public int indexOf(char c) {
        return CharsHelper.indexOf(source, start, end, c);
    }

    @Override
    public int indexOf(String string) {
        return CharsHelper.indexOf(source, start, end, string);
    }

    @Override
    public int lastIndexOf(char c) {
        return CharsHelper.lastIndexOf(source, start, end, c);
    }

    @Override
    public int lastIndexOf(String string) {
        return CharsHelper.lastIndexOf(source, start, end, string);
    }

    @Override
    public CharsRef trim() {
        int len = length();
        int st = 0;

        while ((st < len) && (charAt(st) <= ' ')) {
            st++;
        }
        while ((st < len) && (charAt(len - 1) <= ' ')) {
            len--;
        }
        return ((st > 0) || (len < length())) ? subSequence(st, len) : this;
    }

    @Override
    public String toString() {
        return getSubSequence().toString();
    }

    private CharSequence getSubSequence() {
        if (subCache == null) {
            subCache = source.subSequence(start, end);
        }
        return subCache;
    }
}
