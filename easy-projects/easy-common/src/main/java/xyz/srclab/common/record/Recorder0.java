package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.object.Converter;
import xyz.srclab.common.reflect.ClassKit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author sunqian
 */
final class Recorder0 {

    static Recorder defaultRecorder() {
        return RecorderHolder.INSTANCE;
    }

    @Nullable
    static Object getValue(Object record, String key, Recorder recorder) throws NoSuchElementException {
        @Nullable RecordEntry entry = getEntry(record, key, recorder);
        Checker.checkElement(entry != null, key);
        return entry.getValue(record);
    }

    static void setValue(Object record, String key, @Nullable Object value, Recorder recorder)
            throws NoSuchElementException {
        @Nullable RecordEntry entry = getEntry(record, key, recorder);
        Checker.checkElement(entry != null, key);
        entry.setValue(record, value);
    }

    static void setValue(Object record, String key, @Nullable Object value, Recorder recorder, Converter converter)
            throws NoSuchElementException {
        @Nullable RecordEntry entry = getEntry(record, key, recorder);
        Checker.checkElement(entry != null, key);
        entry.setValue(record, value == null ? null : converter.convert(value, entry.getGenericType()));
    }

    static Map<String, @Nullable Object> asMap(Object record, Recorder recorder) {
        return new RecordView(record, getEntryMap(record, recorder));
    }

    static void set(Object record, Map<String, @Nullable Object> values, Recorder recorder) {
        Map<String, RecordEntry> entryMap = getEntryMap(record, recorder);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v);
        });
    }

    static void set(Object record, Map<String, @Nullable Object> values, Recorder recorder, Converter converter) {
        Map<String, RecordEntry> entryMap = getEntryMap(record, recorder);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v == null ? null : converter.convert(v, entry.getGenericType()));
        });
    }

    static <T> T copy(T record, Recorder recorder) {
        T newInstance = ClassKit.newInstance(record.getClass());
        copyEntries(record, newInstance, recorder);
        return newInstance;
    }

    static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Recorder recorder) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : asMap(sourceRecordOrMap, recorder));
        Map<Object, Object> dest = Cast.as(destRecordOrMap instanceof Map ?
                destRecordOrMap : asMap(destRecordOrMap, recorder));
        copyEntries0(source, dest);
    }

    static void copyEntries(Object sourceRecordOrMap, Object destRecordOrMap, Recorder recorder, Converter converter) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : asMap(sourceRecordOrMap, recorder));
        if (destRecordOrMap instanceof Map) {
            Map<Object, Object> dest = Cast.as(destRecordOrMap);
            copyEntries0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = getEntryMap(destRecordOrMap, recorder);
        destEntries.forEach((k, e) -> {
            if (!source.containsKey(k)) {
                return;
            }
            @Nullable Object value = source.get(k);
            e.setValue(destRecordOrMap, value == null ? null : converter.convert(value, e.getGenericType()));
        });
    }

    static void copyEntriesIgnoreNull(Object sourceRecordOrMap, Object destRecordOrMap, Recorder recorder) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : asMap(sourceRecordOrMap, recorder));
        Map<Object, Object> dest = Cast.as(destRecordOrMap instanceof Map ?
                destRecordOrMap : asMap(destRecordOrMap, recorder));
        copyEntriesIgnoreNull0(source, dest);
    }

    static void copyEntriesIgnoreNull(
            Object sourceRecordOrMap, Object destRecordOrMap, Recorder recorder, Converter converter) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : asMap(sourceRecordOrMap, recorder));
        if (destRecordOrMap instanceof Map) {
            Map<Object, Object> dest = Cast.as(destRecordOrMap);
            copyEntriesIgnoreNull0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = getEntryMap(destRecordOrMap, recorder);
        destEntries.forEach((k, e) -> {
            if (!source.containsKey(k)) {
                return;
            }
            @Nullable Object value = source.get(k);
            if (value == null) {
                return;
            }
            e.setValue(destRecordOrMap, converter.convert(value, e.getGenericType()));
        });
    }

    private static void copyEntries0(Map<Object, Object> source, Map<Object, Object> dest) {
        for (Map.Entry<Object, Object> destEntry : dest.entrySet()) {
            Object key = destEntry.getKey();
            if (!source.containsKey(key)) {
                continue;
            }
            @Nullable Object value = source.get(key);
            destEntry.setValue(value);
        }
    }

    private static void copyEntriesIgnoreNull0(Map<Object, Object> source, Map<Object, Object> dest) {
        for (Map.Entry<Object, Object> destEntry : dest.entrySet()) {
            Object key = destEntry.getKey();
            if (!source.containsKey(key)) {
                continue;
            }
            @Nullable Object value = source.get(key);
            if (value == null) {
                continue;
            }
            destEntry.setValue(value);
        }
    }

    @Nullable
    private static RecordEntry getEntry(Object record, String key, Recorder recorder) {
        if (record instanceof Record<?>) {
            return ((Record<?>) record).entryMap().get(key);
        }
        return recorder.resolve(record.getClass()).get(key);
    }

    @Immutable
    private static Map<String, RecordEntry> getEntryMap(Object record, Recorder recorder) {
        if (record instanceof Record<?>) {
            return ((Record<?>) record).entryMap();
        }
        return recorder.resolve(record.getClass());
    }

    private static final class RecordView
            extends AbstractMap<String, @Nullable Object> implements Map<String, @Nullable Object> {

        private final Object record;
        private final @Immutable Map<String, RecordEntry> entryMap;

        private @Nullable Set<Entry<String, @Nullable Object>> entrySet;

        private RecordView(Object record, Map<String, RecordEntry> entryMap) {
            this.record = record;
            this.entryMap = MapKit.immutable(entryMap);
        }

        @Override
        @Nullable
        public Object get(Object key) {
            @Nullable RecordEntry entry = entryMap.get(key);
            Checker.checkElement(entry != null, key);
            return entry.getValue(record);
        }

        @Override
        @Nullable
        public Object put(String key, @Nullable Object value) {
            @Nullable RecordEntry entry = entryMap.get(key);
            Checker.checkElement(entry != null, key);
            return put0(entry, value);
        }

        @Override
        public Set<Entry<String, @Nullable Object>> entrySet() {
            if (entrySet == null) {
                entrySet = newEntrySet();
            }
            return entrySet;
        }

        private Set<Entry<String, @Nullable Object>> newEntrySet() {
            return SetKit.map(entryMap.entrySet(), e -> new Entry<String, @Nullable Object>() {

                @Override
                public String getKey() {
                    return e.getKey();
                }

                @Nullable
                @Override
                public Object getValue() {
                    return e.getValue().getValue(record);
                }

                @Nullable
                @Override
                public Object setValue(@Nullable Object value) {
                    return put0(e.getValue(), value);
                }
            });
        }

        @Nullable
        private Object put0(RecordEntry entry, @Nullable Object value) {
            if (!entry.isWriteable()) {
                throw new UnsupportedOperationException("Entry is not writeable: " + entry.getKey());
            }
            @Nullable Object old = entry.isReadable() ? entry.getValue(record) : null;
            entry.setValue(record, value);
            return old;
        }
    }

    private static final class RecorderHolder {

        public static final Recorder INSTANCE = Recorder.newBuilder()
                .handler(ResolverHandler.getBeanPatternHandler())
                .build();
    }
}
