package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.record.Recorder;
import xyz.srclab.common.reflect.ClassKit;
import xyz.srclab.common.reflect.TypeKit;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunqian
 */
public class ObjectConvertHandler implements ConvertHandler {

    private final Recorder recorder;

    public ObjectConvertHandler() {
        this(Recorder.defaultRecorder());
    }

    public ObjectConvertHandler(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        Object result = newInstance(to);
        recorder.copyEntries(from, result, converter);
        return result;
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
        Class<?> rawType = TypeKit.getRawType(parameterizedType.getRawType());
        Object result = newInstance(rawType);
        recorder.copyEntries(from, result, to, converter);
        return result;
    }

    private Object newInstance(Class<?> type) {
        Object result;
        if (type.equals(Map.class) || type.equals(LinkedHashMap.class)) {
            result = new LinkedHashMap<>();
        } else if (type.equals(HashMap.class)) {
            result = new HashMap<>();
        } else if (type.equals(TreeMap.class)) {
            result = new TreeMap<>();
        } else if (type.equals(ConcurrentHashMap.class)) {
            result = new ConcurrentHashMap<>();
        } else {
            result = ClassKit.newInstance(type);
        }
        return result;
    }
}
