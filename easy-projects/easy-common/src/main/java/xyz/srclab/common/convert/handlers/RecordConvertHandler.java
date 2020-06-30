package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.record.RecordEntry;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.reflect.TypeKit;

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
        Object result = ClassKit.newInstance(to);
        recorder.copyEntries(from, result, converter);
        return result;
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        Object result = ClassKit.newInstance(TypeKit.getRawType(to));
        Map<String, RecordEntry> entryMap = recorder.resolve(to);
        Map<?, ?> map = from instanceof Map ? (Map<?, ?>) from : recorder.toMap(from);
        map.forEach((k, v) -> {
            @Nullable RecordEntry entry = entryMap.get(k);
            if (entry == null) {
                return;
            }
            entry.setValue(result, v, converter);
        });
        return result;
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
