package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;

import java.util.Map;

/**
 * @author sunqian
 */
public class Record<T extends Record<T>> {

    private final Recorder resolver;
    private @Nullable Map<String, RecordEntry> entries;
    private @Nullable Map<String, @Nullable Object> view;

    protected Record() {
        this(Recorder.getDefault());
    }

    protected Record(Recorder resolver) {
        this.resolver = resolver;
    }

    @Immutable
    public Map<String, RecordEntry> toEntryMap() {
        if (entries == null) {
            synchronized (this) {
                if (entries == null) {
                    entries = resolver.resolve(getClass());
                }
            }
        }
        return entries;
    }

    @Immutable
    public Map<String, Object> toMap() {
        return MapKit.immutable(asMap());
    }

    public Map<String, Object> asMap() {
        if (view == null) {
            synchronized (this) {
                if (view == null) {
                    view = RecordKit.asMap(this, toEntryMap());
                }
            }
        }
        return view;
    }

    public T copy() {
    }

    public void set(Map<String, @Nullable Object> entries) {

    }

    @Override
    public int hashCode() {
        return toMap().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (that == null || !getClass().isAssignableFrom(that.getClass())) {
            return false;
        }
        if (that instanceof Record) {
            return toMap().equals(((Record) that).toMap());
        }
        Map<String, RecordEntry> thatEntries = resolver.resolve(that.getClass());
        return toMap().equals(RecordKit.toMap(that, thatEntries));
    }

    @Override
    public String toString() {
        return toMap().toString();
    }
}
