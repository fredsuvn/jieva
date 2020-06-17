package xyz.srclab.common.record;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface RecordEntry {

    String getKey();

    @Nullable
    Object getValue(Object record);

    void setValue(Object record, @Nullable Object value);

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    boolean isWriteable();
}
