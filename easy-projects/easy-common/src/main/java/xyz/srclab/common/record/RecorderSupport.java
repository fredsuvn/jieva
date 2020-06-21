package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

/**
 * @author sunqian
 */
final class RecorderSupport {

    static Recorder defaultRecorder() {
        return RecorderHolder.INSTANCE;
    }

    static RecordView newRecordView(Object record, Map<String, RecordEntry> entryMap) {
        return new RecordView(record, entryMap);
    }

    private static final class RecordView extends AbstractMap<String, Object> implements Map<String, Object> {

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
            return Require.nonNullElement(entryMap.get(key)).getValue(record);
        }

        @Override
        @Nullable
        public Object put(String key, @Nullable Object value) {
            return put0(Require.nonNullElement(entryMap.get(key)), value);
        }

        @Override
        public Set<Entry<String, @Nullable Object>> entrySet() {
            if (entrySet == null) {
                synchronized (this) {
                    if (entrySet == null) {
                        entrySet = newEntrySet();
                    }
                }
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
                .resolver(RecordResolver.defaultResolver())
                .build();
    }
}
