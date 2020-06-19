package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.NonNull;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public interface Recorder {

    static Recorder defaultRecorder() {
        return RecorderSupport.defaultRecorder();
    }

    static RecorderBuilder newBuilder() {
        return RecorderBuilder.newBuilder();
    }

    RecordResolver resolver();

    Map<String, RecordEntry> resolve(Class<?> recordClass);

    @Nullable
    default RecordEntry getEntry(Object record, String key) {
        if (record instanceof Record<?>) {
            return ((Record<?>) record).entryMap().get(key);
        }
        return resolve(record.getClass()).get(key);
    }

    default RecordEntry getEntryNonNull(Object record, String key) throws NoSuchElementException {
        return NonNull.requireElement(getEntry(record, key));
    }

    @Immutable
    default Map<String, RecordEntry> getEntryMap(Object record) {
        if (record instanceof Record<?>) {
            return ((Record<?>) record).entryMap();
        }
        return resolve(record.getClass());
    }

    @Nullable
    default Object getValue(Object record, String key) throws NoSuchElementException {
        return getEntryNonNull(record, key).getValue(record);
    }

    default Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return NonNull.requireElement(getValue(record, key));
    }

    default void setValue(Object record, String key, @Nullable Object value) throws NoSuchElementException {
        getEntryNonNull(record, key).setValue(record, value);
    }

    default void setValue(Object record, String key, @Nullable Object value, Converter converter)
            throws NoSuchElementException {
        getEntryNonNull(record, key).setValue(record, value, converter);
    }

    default Map<String, @Nullable Object> asMap(Object record) {
        return Recorder0.asMap(this, record);
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object record) {
        return MapKit.immutable(asMap(record));
    }

    default void set(Object record, Map<String, @Nullable Object> values) {
        Recorder0.set(this, record, values);
    }

    default void set(Object record, Map<String, @Nullable Object> values, Converter converter) {
        Recorder0.set(this, record, values, converter);
    }

    default <T> T copy(T record) {
        return Recorder0.copy(this, record);
    }

    default void copyEntries(Object source, Object dest) {
        Recorder0.copyEntries(this, source, dest);
    }

    default void copyEntries(Object source, Object dest, Converter converter) {
        Recorder0.copyEntries(this, source, dest, converter);
    }

    default void copyEntriesIgnoreNull(Object source, Object dest) {
        Recorder0.copyEntriesIgnoreNull(this, source, dest);
    }

    default void copyEntriesIgnoreNull(Object source, Object dest, Converter converter) {
        Recorder0.copyEntriesIgnoreNull(this, source, dest, converter);
    }
}
