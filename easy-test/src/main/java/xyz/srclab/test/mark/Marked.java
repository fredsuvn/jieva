package xyz.srclab.test.mark;

import xyz.srclab.annotation.Nullable;

public interface Marked {

    default void mark() {
        mark(this);
    }

    default void mark(Object key) {
        mark(key, MarkHelper.generateDefaultMark(this, key));
    }

    default void mark(Object key, Object value) {
        MarkSupport.getMarks(this).put(key, value);
    }

    @Nullable
    default Object getActualMark() {
        return getActualMark(this);
    }

    @Nullable
    default Object getActualMark(Object key) {
        return MarkSupport.getMarks(this).get(key);
    }

    default void clearMarks() {
        MarkSupport.getMarks(this).clear();
    }
}
