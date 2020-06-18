package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @author sunqian
 */
public interface Recorder {

    static Recorder defaultRecorder() {
        return Recorder0.defaultRecorder();
    }

    static RecorderBuilder newBuilder() {
        return RecorderBuilder.newBuilder();
    }

    @Immutable
    Map<String, RecordEntry> resolve(Class<?> recordClass);

    @Nullable
    default Object getValue(Object record, String key) throws NoSuchElementException {
        return Recorder0.getValue(record, key, this);
    }

    default Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return Objects.requireNonNull(getValue(record, key));
    }

    default void setValue(Object record, String key, @Nullable Object value) throws NoSuchElementException {
        Recorder0.setValue(record, key, value, this);
    }

    default void setValue(Object record, String key, @Nullable Object value, Converter converter)
            throws NoSuchElementException {
        Recorder0.setValue(record, key, value, this, converter);
    }

    default Map<String, @Nullable Object> asMap(Object record) {
        return Recorder0.asMap(record, this);
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object record) {
        return MapKit.immutable(asMap(record));
    }

    default void set(Object record, Map<String, @Nullable Object> values) {
        Recorder0.set(record, values, this);
    }

    default void set(Object record, Map<String, @Nullable Object> values, Converter converter) {
        Recorder0.set(record, values, this, converter);
    }

    default <T> T copy(T record) {
        return Recorder0.copy(record, this);
    }

    default void copyEntries(Object source, Object dest) {
        Recorder0.copyEntries(source, dest, this);
    }

    default void copyEntries(Object source, Object dest, Converter converter) {
        Recorder0.copyEntries(source, dest, this, converter);
    }

    default void copyEntriesIgnoreNull(Object source, Object dest) {
        Recorder0.copyEntriesIgnoreNull(source, dest, this);
    }

    default void copyEntriesIgnoreNull(Object source, Object dest, Converter converter) {
        Recorder0.copyEntriesIgnoreNull(source, dest, this, converter);
    }
}
