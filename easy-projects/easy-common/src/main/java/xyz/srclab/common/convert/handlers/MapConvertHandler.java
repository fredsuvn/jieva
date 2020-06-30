package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
public class MapConvertHandler implements ConvertHandler {

    private final Recorder recorder;

    public MapConvertHandler() {
        this(Recorder.defaultRecorder());
    }

    public MapConvertHandler(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.equals(Map.class) || to.equals(LinkedHashMap.class)) {
            return toMap(from, Object.class, Object.class, converter, LinkedHashMap::new);
        }
        if (to.equals(HashMap.class)) {
            return toMap(from, Object.class, Object.class, converter, HashMap::new);
        }
        if (to.equals(TreeMap.class)) {
            return toMap(from, Object.class, Object.class, converter, TreeMap::new);
        }
        return null;
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        if (!(to instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) to;
        Class<?> rawTo = TypeKit.getRawType(to);
        if (rawTo.equals(Map.class)
                || rawTo.equals(LinkedHashMap.class)) {
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            return toMap(from, actualTypes[0], actualTypes[1], converter, LinkedHashMap::new);
        }
        if (rawTo.equals(HashMap.class)) {
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            return toMap(from, actualTypes[0], actualTypes[1], converter, HashMap::new);
        }
        if (rawTo.equals(TreeMap.class)) {
            Type[] actualTypes = parameterizedType.getActualTypeArguments();
            return toMap(from, actualTypes[0], actualTypes[1], converter, TreeMap::new);
        }
        return null;
    }

    private Object toMap(
            Object from, Type keyType, Type valueType, Converter converter, Supplier<Map<Object, Object>> mapSupplier) {
        if (from instanceof Map) {
            return mapToMap((Map<?, ?>) from, keyType, valueType, converter, mapSupplier);
        } else {
            Map<String, Object> map = recorder.toMap(from);
            return mapToMap(map, keyType, valueType, converter, mapSupplier);
        }
    }

    private Object mapToMap(
            Map<?, ?> from,
            Type keyType,
            Type valueType,
            Converter converter,
            Supplier<Map<Object, Object>> mapSupplier
    ) {
        Map<Object, Object> result = mapSupplier.get();
        from.forEach((k, v) -> {
            Object key = converter.convert(k, keyType);
            @Nullable Object value = v == null ? null : converter.convert(v, valueType);
            result.put(key, value);
        });
        return result;
    }
}
