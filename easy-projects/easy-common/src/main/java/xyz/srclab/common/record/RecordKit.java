package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.object.Converter;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class RecordKit {

    private static final Recorder recorder = Recorder.getInstance();

    public static Recorder getDefault() {
        return Recorder.getInstance();
    }

    @Immutable
    public static Map<String, RecordEntry> resolve(Class<?> recordClass) {
        return recorder.resolve(recordClass);
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

    public static void copyEntries(Object source, Object dest) {
        recorder.copyEntries(source, dest);
    }

    public static void copyEntries(Object source, Object dest, Converter converter) {
        recorder.copyEntries(source, dest, converter);
    }

    public static void copyEntriesIgnoreNull(Object source, Object dest) {
        recorder.copyEntriesIgnoreNull(source, dest);
    }

    public static void copyEntriesIgnoreNull(Object source, Object dest, Converter converter) {
        recorder.copyEntriesIgnoreNull(source, dest, converter);
    }
}
