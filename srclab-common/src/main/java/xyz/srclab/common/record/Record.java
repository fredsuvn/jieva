package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.collection.MapOps;
import xyz.srclab.common.convert.Converter;

import java.util.Map;

/**
 * @author sunqian
 */
public abstract class Record<T extends Record<T>> {

    protected final Recorder recorder;
    protected transient @Nullable RecordType recordType;
    protected transient @Nullable Map<String, @Nullable Object> viewMap;

    protected Record() {
        this(Recorder.defaultRecorder());
    }

    protected Record(Recorder recorder) {
        this.recorder = recorder;
    }

    @Immutable
    public RecordType recordType() {
        if (recordType == null) {
            synchronized (this) {
                if (recordType == null) {
                    recordType = recorder.resolve(getClass());
                }
            }
        }
        return recordType;
    }

    public Map<String, @Nullable Object> asMap() {
        if (viewMap == null) {
            synchronized (this) {
                if (viewMap == null) {
                    viewMap = RecorderSupport.newRecordView(this, recordType().entryMap());
                }
            }
        }
        return viewMap;
    }

    @Immutable
    public Map<String, @Nullable Object> toMap() {
        return MapOps.immutable(asMap());
    }

    public void set(Map<String, @Nullable Object> values) {
        recorder.set(this, values);
    }

    public void set(Map<String, @Nullable Object> values, Converter converter) {
        recorder.set(this, values, converter);
    }

    public T copy() {
        return As.notNull(recorder.copy(this));
    }

    public void copyEntries(Object dest) {
        recorder.copyEntries(this, dest);
    }

    public void copyEntries(Object dest, Converter converter) {
        recorder.copyEntries(this, dest, converter);
    }

    public void copyEntriesIgnoreNull(Object dest) {
        recorder.copyEntriesIgnoreNull(this, dest);
    }

    public void copyEntriesIgnoreNull(Object dest, Converter converter) {
        recorder.copyEntriesIgnoreNull(this, dest, converter);
    }

    @Override
    public int hashCode() {
        return toMap().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object that) {
        if (that == null || !getClass().equals(that.getClass())) {
            return false;
        }
        return toMap().equals(((Record<?>) that).toMap());
    }

    @Override
    public String toString() {
        return toMap().toString();
    }
}
