package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class RecordKit {

    private static final Recorder recorder = Recorder.defaultRecorder();

    public static RecordResolver resolver() {
        return recorder.resolver();
    }

    public static RecordType resolve(Type type) {
        return recorder.resolve(type);
    }

    public static RecordType recordType(Object record) {
        return recorder.recordType(record);
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

    public static void setValue(Object record, String key, @Nullable Object value, Converter converter) throws NoSuchElementException {
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

    public static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Type destType) {
        recorder.copyEntries(sourceRecordOrMap, destRecordOrMap, destType);
    }

    public static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Type destType, Converter converter) {
        recorder.copyEntries(sourceRecordOrMap, destRecordOrMap, destType, converter);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, converter);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Type destType) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, destType);
    }

    public static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Type destType, Converter converter) {
        recorder.copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, destType, converter);
    }

    public static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Type destType, Converter converter, Predicate<@Nullable Object> predicate) {
        recorder.copyEntries(sourceRecordOrMap, destRecordOrMap, destType, converter, predicate);
    }

    public static void copyRecordToMap(Object record, RecordType recordType, Map<Object, Object> map, Type keyType, Type valueType, Converter converter, Predicate<@Nullable Object> predicate) {
        recorder.copyRecordToMap(record, recordType, map, keyType, valueType, converter, predicate);
    }

    public static void copyRecordToRecord(Object sourceRecord, RecordType sourceRecordType, Object destRecord, RecordType destRecordType, Converter converter, Predicate<@Nullable Object> predicate) {
        recorder.copyRecordToRecord(sourceRecord, sourceRecordType, destRecord, destRecordType, converter, predicate);
    }

    public static void copyMapToRecord(Map<?, ?> map, Object record, RecordType recordType, Converter converter, Predicate<@Nullable Object> predicate) {
        recorder.copyMapToRecord(map, record, recordType, converter, predicate);
    }

    public static void copyMapToMap(Map<?, ?> sourceMap, Map<Object, Object> destMap, Type destKeyType, Type destValueType, Converter converter, Predicate<@Nullable Object> predicate) {
        recorder.copyMapToMap(sourceMap, destMap, destKeyType, destValueType, converter, predicate);
    }

    public static boolean contentEquals(Object recordOrMapA, Object recordOrMapB) {
        return recorder.contentEquals(recordOrMapA, recordOrMapB);
    }
}
