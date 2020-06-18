package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Immutable
public interface ResolverHandler {

    static ResolverHandler getFieldHandler() {
        return Resolver0.getFieldHandler();
    }

    static ResolverHandler getBeanPatternHandler() {
        return Resolver0.getBeanPatternHandler();
    }

    static ResolverHandler getNamingPatternHandler() {
        return Resolver0.getNamingPatternHandler();
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
