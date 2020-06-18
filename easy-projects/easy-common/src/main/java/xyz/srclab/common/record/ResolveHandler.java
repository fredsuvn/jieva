package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Immutable
public interface ResolveHandler {

    static ResolveHandler getFieldHandler() {
        return Resolve0.getFieldHandler();
    }

    static ResolveHandler getBeanPatternHandler() {
        return Resolve0.getBeanPatternHandler();
    }

    static ResolveHandler getNamingPatternHandler() {
        return Resolve0.getNamingPatternHandler();
    }

    void resolve(Class<?> recordClass, @Out Context context);

    interface Context {

        Map<String, RecordEntry> entryMap();

        @Immutable
        List<Field> fields();

        @Immutable
        List<Method> methods();

        void unsupport();

        void terminate();
    }
}
