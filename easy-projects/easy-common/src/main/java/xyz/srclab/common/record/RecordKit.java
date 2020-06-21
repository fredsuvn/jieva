package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class RecordKit {

    private static final Recorder recorder = Recorder.defaultRecorder();

    @Immutable
    public static Map<String, RecordEntry> resolve(Class<?> recordClass) {
        return recorder.resolve(recordClass);
    }

    @Immutable
    public static Map<String, RecordEntry> entryMap(Object record) {
        return recorder.entryMap(record);
    }

    @Nullable
    public static RecordEntry getEntry(Object record, String key) {
        return recorder.getEntry(record, key);
    }

    public static RecordEntry getEntryNonNull(Object record, String key) throws NoSuchElementException {
        return recorder.getEntryNonNull(record, key);
    }

    @Nullable
    public static Object getValue(Object record, String key) throws NoSuchElementException {
        return recorder.getValue(record, key);
    }

    public static Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return recorder.getValueNonNull(record, key);
    }

    public static void setValue(Object record, String key, @Nullable Object value) throws NoSuchElementException {
        recorder.setValue(record, key, value);
    }

    public static void setValue(
            Object record, String key, @Nullable Object value, Converter converter) throws NoSuchElementException {
        recorder.setValue(record, key, value, converter);
    }

    public static Map<String, @Nullable Object> asMap(Object record) {
        return recorder.asMap(record);
    }

    @Immutable
    public static Map<String, @Nullable Object> toMap(Object record) {
        return recorder.toMap(record);
    }

    public static void set(Object record, Map<String, @Nullable Object> values) {
        recorder.set(record, values);
    }

    public static void set(Object record, Map<String, @Nullable Object> values, Converter converter) {
        recorder.set(record, values, converter);
    }

    public static <T> T copy(T record) {
        return recorder.copy(record);
    }

    public static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap) {
        recorder.copyEntries(sourceRecordOrMap, destRecordOrMap);
    }

    public static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        recorder.copyEntries(sourceRecordOrMap, destRecordOrMap, converter);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, converter);
    }

    public static boolean contentEquals(Object recordOrMapA, Object recordOrMapB) {
        return recorder.contentEquals(recordOrMapA, recordOrMapB);
    }
}
