package xyz.srclab.common.record;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author sunqian
 */
final class Recorder0 {



    static void copyEntries0(Map<Object, @Nullable Object> source, Map<Object, @Nullable Object> dest) {
        for (Map.Entry<Object, Object> destEntry : dest.entrySet()) {
            Object key = destEntry.getKey();
            if (!source.containsKey(key)) {
                continue;
            }
            @Nullable Object value = source.get(key);
            destEntry.setValue(value);
        }
    }

    static void copyEntriesIgnoreNull0(Map<Object, @Nullable Object> source, Map<Object, @Nullable Object> dest) {
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

    static Map<Object, @Nullable Object> anyAsMap(Recorder _this, Object recordOrMap) {
        return Cast.as(recordOrMap instanceof Map ? recordOrMap : _this.asMap(recordOrMap));
    }
}
