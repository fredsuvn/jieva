package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.reflect.ClassKit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * @author sunqian
 */
final class Recorder0 {

    static Map<String, @Nullable Object> asMap(Recorder _this, Object record) {
        return new RecordView(record, _this.getEntryMap(record));
    }

    static void set(Recorder _this, Object record, Map<String, @Nullable Object> values) {
        Map<String, RecordEntry> entryMap = _this.getEntryMap(record);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v);
        });
    }

    static void set(Recorder _this, Object record, Map<String, @Nullable Object> values, Converter converter) {
        Map<String, RecordEntry> entryMap = _this.getEntryMap(record);
        values.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(record, v == null ? null : converter.convert(v, entry.getGenericType()));
        });
    }

    static <T> T copy(Recorder _this, T record) {
        T newInstance = ClassKit.newInstance(record.getClass());
        copyEntries(_this, record, newInstance);
        return newInstance;
    }

    static void copyEntries(Recorder _this, Object sourceRecordOrMap, Object destRecordOrMap) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : _this.asMap(sourceRecordOrMap));
        Map<Object, Object> dest = Cast.as(destRecordOrMap instanceof Map ?
                destRecordOrMap : _this.asMap(destRecordOrMap));
        copyEntries0(source, dest);
    }

    static void copyEntries(Recorder _this, Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : _this.asMap(sourceRecordOrMap));
        if (destRecordOrMap instanceof Map) {
            Map<Object, Object> dest = Cast.as(destRecordOrMap);
            copyEntries0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = _this.getEntryMap(destRecordOrMap);
        destEntries.forEach((k, e) -> {
            if (!source.containsKey(k)) {
                return;
            }
            @Nullable Object value = source.get(k);
            e.setValue(destRecordOrMap, value == null ? null : converter.convert(value, e.getGenericType()));
        });
    }

    static void copyEntriesIgnoreNull(Recorder _this, Object sourceRecordOrMap, Object destRecordOrMap) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : _this.asMap(sourceRecordOrMap));
        Map<Object, Object> dest = Cast.as(destRecordOrMap instanceof Map ?
                destRecordOrMap : _this.asMap(destRecordOrMap));
        copyEntriesIgnoreNull0(source, dest);
    }

    static void copyEntriesIgnoreNull(
            Recorder _this, Object sourceRecordOrMap, Object destRecordOrMap, Converter converter) {
        Map<Object, Object> source = Cast.as(sourceRecordOrMap instanceof Map ?
                sourceRecordOrMap : _this.asMap(sourceRecordOrMap));
        if (destRecordOrMap instanceof Map) {
            Map<Object, Object> dest = Cast.as(destRecordOrMap);
            copyEntriesIgnoreNull0(source, dest);
            return;
        }
        Map<String, RecordEntry> destEntries = _this.getEntryMap(destRecordOrMap);
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
}
