package xyz.srclab.common.string;

import xyz.srclab.annotation.Immutable;

@Immutable
public interface StringRef<T extends CharSequence> extends CharSequence {

    //public static StringRef of(CharSequence chars, int start, int end) {
    //    Checker.checkBounds(chars.length(), start, end);
    //    return new DefaultStringRef(chars, start, end);
    //}
    //
    //public static StringRef of(CharSequence chars, int start) {
    //    return of(chars, start, chars.length());
    //}

    @Override
    StringRef<T> subSequence(int start, int end);

    default StringRef<T> subSequence(int start) {
        return subSequence(start, length());
    }

    StringRef<T> trim();

    StringRef<T> trimStart();

    StringRef<T> trimEnd();

    T getOrigin();
}
