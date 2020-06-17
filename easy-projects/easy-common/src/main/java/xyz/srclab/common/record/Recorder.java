package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author sunqian
 */
public interface Recorder {

    static Recorder getDefault() {
        return null;
    }

    @Immutable
    Map<String, RecordEntry> resolve(Class<?> recordClass);

    default Map<String, @Nullable Object> asMap(Object record) {
        return Record0.newRecordViewMap(record, resolve(record.getClass()));
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object record) {
        return MapKit.immutable(asMap(record));
    }

    @Nullable
    default Object getValue(Object record, String key) throws NoSuchElementException {
        @Nullable RecordEntry entry = resolve(record.getClass()).get(key);
        if (entry == null) {
            throw new NoSuchElementException(key);
        }
        return entry.getValue(record);
    }

    default Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return Objects.requireNonNull(getValue(record, key));
    }

    default void setValue(Object record, String key, @Nullable Object value) throws NoSuchElementException {
        @Nullable RecordEntry entry = resolve(record.getClass()).get(key);
        if (entry == null) {
            throw new NoSuchElementException(key);
        }
        entry.setValue(record, value);
    }

    default void setValues(Object record, Map<String, @Nullable Object> values) {
        asMap(record).putAll(values);
    }

    default void copyEntries(Object source, Object dest) {

    }
}
