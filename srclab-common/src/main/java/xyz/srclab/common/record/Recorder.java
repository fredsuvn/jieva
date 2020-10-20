package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapOps;
import xyz.srclab.common.collection.MapScheme;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

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

    RecordType resolve(Type type);

    default RecordType recordType(Object record) {
        if (record instanceof Record) {
            return ((Record<?>) record).recordType();
        }
        return resolve(record.getClass());
    }

    @Nullable
    default RecordEntry getEntry(Object record, String key) {
        if (record instanceof Record) {
            return ((Record<?>) record).recordType().entryMap().get(key);
        }
        return resolve(record.getClass()).entryMap().get(key);
    }

    default RecordEntry getEntryNonNull(Object record, String key) throws NoSuchElementException {
        return Require.notNullElement(getEntry(record, key));
    }

    @Nullable
    default Object getValue(Object record, String key) throws NoSuchElementException {
        return getEntryNonNull(record, key).getValue(record);
    }

    default Object getValueNonNull(Object record, String key) throws NoSuchElementException {
        return Require.notNullElement(getValue(record, key));
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
        return RecorderSupport.newRecordView(record, recordType(record).entryMap());
    }

    @Immutable
    default Map<String, @Nullable Object> toMap(Object record) {
        return MapOps.immutable(asMap(record));
    }

    default void set(Object record, Map<String, @Nullable Object> values) {
        Map<String, RecordEntry> entryMap = recordType(record).entryMap();
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v);
        });
    }

    default void set(Object record, Map<String, @Nullable Object> values, Converter converter) {
        Map<String, RecordEntry> entryMap = recordType(record).entryMap();
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v, converter);
        });
    }

    default <T> T copy(T record) {
        T newInstance = ClassKit.toInstance(record.getClass());
        copyEntries(record, newInstance);
        return newInstance;
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap) {
        copyEntries(sourceRecordOrMap, destRecordOrMap, Converter.defaultConverter());
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        copyEntries(sourceRecordOrMap, destRecordOrMap, destRecordOrMap.getClass(), converter);
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Type destType) {
        copyEntries(sourceRecordOrMap, destRecordOrMap, destType, Converter.defaultConverter());
    }

    default void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Type destType, Converter converter) {
        copyEntries(sourceRecordOrMap, destRecordOrMap, destType, converter, o -> true);
    }

    default void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap) {
        copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, Converter.defaultConverter());
    }

    default void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, destRecordOrMap.getClass(), converter);
    }

    default void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Type destType) {
        copyEntriesIgnoreNull(sourceRecordOrMap, destRecordOrMap, destType, Converter.defaultConverter());
    }

    default void copyEntriesIgnoreNull(
            Object sourceRecordOrMap, Object destRecordOrMap, Type destType, Converter converter) {
        copyEntries(sourceRecordOrMap, destRecordOrMap, destType, converter, Objects::nonNull);
    }

    default void copyEntries(
            Object sourceRecordOrMap, Object destRecordOrMap, Type destType,
            Converter converter, Predicate<@Nullable Object> predicate) {
        if (sourceRecordOrMap instanceof Map) {
            if (destRecordOrMap instanceof Map) {
                MapScheme mapScheme = MapScheme.getMapScheme(destType);
                copyMapToMap(
                        As.notNull(sourceRecordOrMap),
                        As.notNull(destRecordOrMap), mapScheme.keyType(), mapScheme.valueType(),
                        converter, predicate
                );
            } else {
                copyMapToRecord(
                        As.notNull(sourceRecordOrMap),
                        destRecordOrMap, resolve(destType),
                        converter, predicate
                );
            }
        } else {
            if (destRecordOrMap instanceof Map) {
                MapScheme mapScheme = MapScheme.getMapScheme(destType);
                copyRecordToMap(
                        sourceRecordOrMap, recordType(sourceRecordOrMap),
                        As.notNull(destRecordOrMap), mapScheme.keyType(), mapScheme.valueType(),
                        converter, predicate
                );
            } else {
                copyRecordToRecord(
                        sourceRecordOrMap, recordType(sourceRecordOrMap),
                        destRecordOrMap, resolve(destType),
                        converter, predicate
                );
            }
        }
    }

    default void copyRecordToMap(Object record, RecordType recordType,
                                 Map<Object, Object> map, Type keyType, Type valueType,
                                 Converter converter,
                                 Predicate<@Nullable Object> predicate
    ) {
        recordType.entryMap().forEach((name, entry) -> {
            Object mapKey = converter.convert(name, keyType);
            if (!map.containsKey(mapKey)) {
                return;
            }
            @Nullable Object source = entry.getValue(record);
            if (!predicate.test(source)) {
                return;
            }
            @Nullable Object target = source == null ? null : converter.convert(source, valueType);
            map.put(mapKey, target);
        });
    }

    default void copyRecordToRecord(Object sourceRecord, RecordType sourceRecordType,
                                    Object destRecord, RecordType destRecordType,
                                    Converter converter,
                                    Predicate<@Nullable Object> predicate
    ) {
        sourceRecordType.entryMap().forEach((name, entry) -> {
            @Nullable RecordEntry destEntry = destRecordType.entryMap().get(name);
            if (destEntry == null) {
                return;
            }
            @Nullable Object source = entry.getValue(sourceRecord);
            if (!predicate.test(source)) {
                return;
            }
            destEntry.setValue(destRecord, source, converter);
        });
    }

    default void copyMapToRecord(Map<?, ?> map,
                                 Object record, RecordType recordType,
                                 Converter converter,
                                 Predicate<@Nullable Object> predicate
    ) {
        map.forEach((key, value) -> {
            if (!predicate.test(value)) {
                return;
            }
            String recordKey = converter.toString(key);
            @Nullable RecordEntry destEntry = recordType.entryMap().get(recordKey);
            if (destEntry == null) {
                return;
            }
            destEntry.setValue(record, value, converter);
        });
    }


    default void copyMapToMap(Map<?, ?> sourceMap,
                              Map<Object, Object> destMap, Type destKeyType, Type destValueType,
                              Converter converter,
                              Predicate<@Nullable Object> predicate
    ) {
        sourceMap.forEach((key, value) -> {
            Object destKey = converter.convert(key, destKeyType);
            if (!destMap.containsKey(destKey)) {
                return;
            }
            if (!predicate.test(value)) {
                return;
            }
            @Nullable Object destValue = value == null ? null : converter.convert(value, destValueType);
            destMap.put(destKey, destValue);
        });
    }

    default boolean contentEquals(Object recordOrMapA, Object recordOrMapB) {
        Map<Object, @Nullable Object> a = Recorder0.anyAsMap(this, recordOrMapA);
        Map<Object, @Nullable Object> b = Recorder0.anyAsMap(this, recordOrMapB);
        return a.equals(b);
    }
}
