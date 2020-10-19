package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.object.UnitPredicate;
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
public class RecordConvertHandler implements ConvertHandler {

    private final Recorder recorder;
    private final UnitPredicate unitPredicate;

    public RecordConvertHandler() {
        this(Recorder.defaultRecorder());
    }

    public RecordConvertHandler(Recorder recorder) {
        this(recorder, UnitPredicate.defaultPredicate());
    }

    public RecordConvertHandler(UnitPredicate unitPredicate) {
        this(Recorder.defaultRecorder(), unitPredicate);
    }

    public RecordConvertHandler(Recorder recorder, UnitPredicate unitPredicate) {
        this.recorder = recorder;
        this.unitPredicate = unitPredicate;
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (unitPredicate.test(from.getClass()) || unitPredicate.test(to)) {
            return null;
        }
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
        if (unitPredicate.test(from.getClass()) || unitPredicate.test(to)) {
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
