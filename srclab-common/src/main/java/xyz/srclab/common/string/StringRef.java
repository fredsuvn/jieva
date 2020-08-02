package xyz.srclab.common.string;

import xyz.srclab.annotation.Immutable;

import java.util.function.IntPredicate;

@Immutable
public interface StringRef<T extends CharSequence> extends CharSequence {

    static <T extends CharSequence> StringRef<T> of(T origin, int start) {
        return of(origin, start, origin.length());
    }

    static <T extends CharSequence> StringRef<T> of(T origin, int start, int end) {
        return StringRef0.newStringRef(origin, start, end);
    }

    T getOrigin();

    int start();

    void start(int start);

    int end();

    void end(int end);

    void trim();

    void trim(IntPredicate predicate);

    void trimStart();

    void trimStart(IntPredicate predicate);

    void trimEnd();

    void trimEnd(IntPredicate predicate);

    StringRef<T> clone();

    @Override
    StringRef<T> subSequence(int start, int end);

    default StringRef<T> subSequence(int start) {
        return subSequence(start, length());
    }
}
