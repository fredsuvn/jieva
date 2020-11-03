package xyz.srclab.test.mark;

import xyz.srclab.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

public interface MarkTesting {

    default void mark() {
        mark(this);
    }

    default void mark(Object key) {
        mark(key, MarkHelper.generateMark(this, key));
    }

    default void mark(Object key, Object value) {
        MarkSupport.getMarks(this).put(key, value);
    }

    default void unmark() {
        unmark(this);
    }

    default void unmark(Object key) {
        MarkSupport.getMarks(this).remove(key);
    }

    default void clearMarks() {
        MarkSupport.getMarks(this).clear();
    }

    @Nullable
    default Object getMark() {
        return getMark(this);
    }

    @Nullable
    default Object getMark(Object key) {
        return MarkSupport.getMarks(this).get(key);
    }

    default Map<Object, Object> getAllMarks() {
        return Collections.unmodifiableMap(MarkSupport.getMarks(this));
    }
}
