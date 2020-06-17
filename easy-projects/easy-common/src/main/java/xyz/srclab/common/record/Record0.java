package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sunqian
 */
final class Record0 {

    static Map<String, @Nullable Object> newRecordViewMap(Object record, Map<String, RecordEntry> entries) {
        return new RecordViewMap(record, entries);
    }

    private static final class RecordViewMap
            extends AbstractMap<String, @Nullable Object> implements Map<String, @Nullable Object> {

        private final Object record;
        private final @Immutable Map<String, RecordEntry> entries;

        private @Nullable Set<Entry<String, @Nullable Object>> entrySet;

        private RecordViewMap(Object record, Map<String, RecordEntry> entries) {
            this.record = record;
            this.entries = MapKit.immutable(entries);
        }

        @Override
        public Set<Entry<String, @Nullable Object>> entrySet() {
            if (entrySet == null) {
                entrySet = newEntrySet();
            }
            return entrySet;
        }

        @Override
        public Object put(String key, Object value) {
            @Nullable RecordEntry entry = entries.get(key);
            if (entry == null) {
                throw new NoSuchElementException("Record entry not found: " + key);
            }
            return put0(entry, value);
        }

        private Set<Entry<String, @Nullable Object>> newEntrySet() {
            return entries.entrySet()
                    .stream()
                    .map(e -> new Entry<String, @Nullable Object>() {
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
                                    RecordEntry entry = e.getValue();
                                    return put0(entry, value);
                                }
                            }
                    )
                    .collect(Collectors.toSet());
        }

        @Nullable
        private Object put0(RecordEntry entry, @Nullable Object value) {
            if (!entry.isWriteable()) {
                throw new UnsupportedOperationException("Record entry is not writeable: " + entry.getKey());
            }
            @Nullable Object old = entry.isReadable() ? entry.getValue(record) : null;
            entry.setValue(record, value);
            return old;
        }
    }
}
