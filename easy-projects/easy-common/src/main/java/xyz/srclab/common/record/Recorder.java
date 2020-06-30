package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;

import java.lang.reflect.Type;
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

    @Immutable
    Map<String, RecordEntry> resolve(Type recordType);

    @Immutable
    default Map<String, RecordEntry> entryMap(Object record) {
        if (record instanceof Record) {
            return ((Record<?>) record).entryMap();
        }
        return resolve(record.getClass());
    }

    @Nullable
    default RecordEntry getEntry(Object record, String key) {
        if (record instanceof Record) {
            return ((Record<?>) record).entryMap().get(key);
        }
        return resolve(record.getClass()).get(key);
    }

    default RecordEntry getEntryNonNull(Object record, String key) throws NoSuchElementException {
        return Require.nonNullElement(getEntry(record, key));
    }

    @Nullable
    default Object getValue(Object record, String key) throws NoSuchElementException {
        return getEntryNonNull(record, key).getValue(record);
    }

    default Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return Require.nonNullElement(getValue(record, key));
    }

    default void setValue(Object record, String key, @Nullable Object value) throws NoSuchElementException {
        getEntryNonNull(record, key).setValue(record, value);
    }

    default void setValue(Object record, String key, @Nullable Object value, Converter converter)
            throws NoSuchElementException {
        getEntryNonNull(record, key).setValue(record, value, converter);
    }

    default Map<String, @Nullable Object> asMap(Object record) {
        if (record instanceof Record) {
            return ((Record<?>) record).asMap();
        }
        return RecorderSupport.newRecordView(record, entryMap(record));
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object record) {
        return MapKit.immutable(asMap(record));
    }

    default void set(Object record, Map<String, @Nullable Object> values) {
        Map<String, RecordEntry> entryMap = entryMap(record);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v);
        });
    }

    default void set(Object record, Map<String, @Nullable Object> values, Converter converter) {
        Map<String, RecordEntry> entryMap = entryMap(record);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v, converter);
        });
    }

    default <T> T copy(T record) {
        T newInstance = ClassKit.newInstance(record.getClass());
        copyEntries(record, newInstance);
        return newInstance;
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap) {
        Map<Object, @Nullable Object> source = Recorder0.anyAsMap(this, sourceRecordOrMap);
        Map<Object, @Nullable Object> dest = Recorder0.anyAsMap(this, destRecordOrMap);
        Recorder0.copyEntries0(source, dest);
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        Map<Object, @Nullable Object> source = Recorder0.anyAsMap(this, sourceRecordOrMap);
        if (destRecordOrMap instanceof Map) {
            Map<Object, @Nullable Object> dest = Cast.as(destRecordOrMap);
            Recorder0.copyEntries0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = this.entryMap(destRecordOrMap);
        destEntries.forEach((k, e) -> {
            if (!source.containsKey(k)) {
                return;
            }
            @Nullable Object value = source.get(k);
            e.setValue(destRecordOrMap, value, converter);
        });
    }

    default void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap) {
        Map<Object, @Nullable Object> source = Recorder0.anyAsMap(this, sourceRecordOrMap);
        Map<Object, @Nullable Object> dest = Recorder0.anyAsMap(this, destRecordOrMap);
        Recorder0.copyEntriesIgnoreNull0(source, dest);
    }

    default void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        Map<Object, @Nullable Object> source = Recorder0.anyAsMap(this, sourceRecordOrMap);
        if (destRecordOrMap instanceof Map) {
            Map<Object, @Nullable Object> dest = Cast.as(destRecordOrMap);
            Recorder0.copyEntriesIgnoreNull0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = this.entryMap(destRecordOrMap);
        destEntries.forEach((k, e) -> {
            if (!source.containsKey(k)) {
                return;
            }
            @Nullable Object value = source.get(k);
            if (value == null) {
                return;
            }
            e.setValue(destRecordOrMap, value, converter);
        });
    }

    default boolean contentEquals(Object recordOrMapA, Object recordOrMapB) {
        Map<Object, @Nullable Object> a = Recorder0.anyAsMap(this, recordOrMapA);
        Map<Object, @Nullable Object> b = Recorder0.anyAsMap(this, recordOrMapB);
        return a.equals(b);
    }
}
