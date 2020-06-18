package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.object.Converter;

import java.util.Map;

/**
 * @author sunqian
 */
public class Record<T extends Record<T>> {

    private final Recorder resolver;
    private @Nullable Map<String, RecordEntry> entryMap;
    private @Nullable Map<String, @Nullable Object> viewMap;

    protected Record() {
        this(Recorder.getDefault());
    }

    protected Record(Recorder resolver) {
        this.resolver = resolver;
    }

    @Immutable
    public Map<String, RecordEntry> entryMap() {
        if (entryMap == null) {
            synchronized (this) {
                if (entryMap == null) {
                    entryMap = resolver.resolve(getClass());
                }
            }
        }
        return entryMap;
    }

    public Map<String, Object> asMap() {
        if (viewMap == null) {
            synchronized (this) {
                if (viewMap == null) {
                    viewMap = resolver.asMap(this);
                }
            }
        }
        return viewMap;
    }

    @Immutable
    public Map<String, Object> toMap() {
        return MapKit.immutable(asMap());
    }

    public void set(Map<String, @Nullable Object> values) {
        resolver.set(this, values);
    }

    public void set(Map<String, @Nullable Object> values, Converter converter) {
        resolver.set(this, values, converter);
    }

    public T copy() {
        return Cast.as(resolver.copy(this));
    }

    public void copyEntries(Object dest) {
        resolver.copyEntries(this, dest);
    }

    public void copyEntries(Object dest, Converter converter) {
        resolver.copyEntries(this, dest, converter);
    }

    public void copyEntriesIgnoreNull(Object dest) {
        resolver.copyEntriesIgnoreNull(this, dest);
    }

    public void copyEntriesIgnoreNull(Object dest, Converter converter) {
        resolver.copyEntriesIgnoreNull(this, dest, converter);
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
            return toMap().equals(((Record<?>) that).toMap());
        }
        return toMap().equals(resolver.toMap(that));
    }

    @Override
    public String toString() {
        return toMap().toString();
    }
}
