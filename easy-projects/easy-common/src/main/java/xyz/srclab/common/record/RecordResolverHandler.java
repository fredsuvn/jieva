package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Immutable
public interface RecordResolverHandler {

    static RecordResolverHandler getFieldHandler() {
        return RecordResolverSupport.getFieldHandler();
    }

    static RecordResolverHandler getBeanPatternHandler() {
        return RecordResolverSupport.getBeanPatternHandler();
    }

    static RecordResolverHandler getNamingPatternHandler() {
        return RecordResolverSupport.getNamingPatternHandler();
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
