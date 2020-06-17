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
public class RecordKit {

    private static final Recorder RECORDER = Recorder.getDefault();

    @Immutable
    public static Map<String, Object> toMap(Object record, Map<String, RecordEntry> entries) {
        return MapKit.immutable(asMap(record, entries));
    }

    public static Map<String, Object> asMap(Object record, Map<String, RecordEntry> entries) {
        return new RecordViewMap(record, entries);
    }

    public static void copyEntries(Object source, Object dest) {

    }


}
