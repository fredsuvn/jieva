package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author sunqian
 */
public class RecordConvertHandler implements ConvertHandler {

    private final Recorder recorder;

    public RecordConvertHandler() {
        this(Recorder.defaultRecorder());
    }

    public RecordConvertHandler(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (from)
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        Class<?> rawTo = TypeKit.getRawType(to);
        if (rawTo.equals(Map.class) && to instanceof ParameterizedType) {
            Type[] actualTypes = ((ParameterizedType) to).getActualTypeArguments();
            return toMap(from, actualTypes[0], actualTypes[1], converter);
        }
        return null;
    }

    private Object toMap(Object from, Type keyType, Type valueType, Converter converter) {
        if (from instanceof Map) {
            return mapToMap((Map<?, ?>) from, keyType, valueType, converter);
        } else {
            Map<String, Object> map = recorder.toMap(from);
            return mapToMap(map, keyType, valueType, converter);
        }
    }

    private Object mapToMap(Map<?, ?> from, Type keyType, Type valueType, Converter converter) {
        Map<Object, Object> result = new LinkedHashMap<>();
        from.forEach((k, v) -> {
            Object key = converter.convert(k, keyType);
            @Nullable Object value = v == null ? null : converter.convert(v, valueType);
            result.put(key, value);
        });
        return result;
    }
}
