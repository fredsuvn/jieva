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
import java.util.function.Predicate;

/**
 * @author sunqian
 */
public class RecordConvertHandler implements ConvertHandler {

    private static final Predicate<Type> DEFAULT_RECORD_TYPE_PREDICATE = o -> true;

    private final Recorder recorder;
    private final Predicate<Type> recordTypePredicate;

    public RecordConvertHandler() {
        this(Recorder.defaultRecorder());
    }

    public RecordConvertHandler(Recorder recorder) {
        this(recorder, DEFAULT_RECORD_TYPE_PREDICATE);
    }

    public RecordConvertHandler(Predicate<Type> recordTypePredicate) {
        this(Recorder.defaultRecorder(), recordTypePredicate);
    }

    public RecordConvertHandler(Recorder recorder, Predicate<Type> recordTypePredicate) {
        this.recorder = recorder;
        this.recordTypePredicate = recordTypePredicate;
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (!(from instanceof Map || recordTypePredicate.test(from.getClass()))) {
            return null;
        }
        @Nullable Object result = newInstance(to);
        if (result == null) {
            return null;
        }
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
        if (!(from instanceof Map || recordTypePredicate.test(from.getClass()))) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) to;
        Class<?> rawType = TypeKit.getRawType(parameterizedType.getRawType());
        @Nullable Object result = newInstance(rawType);
        if (result == null) {
            return null;
        }
        recorder.copyEntries(from, result, to, converter);
        return result;
    }

    @Nullable
    private Object newInstance(Class<?> type) {
        if (!recordTypePredicate.test(type)) {
            return null;
        }
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
