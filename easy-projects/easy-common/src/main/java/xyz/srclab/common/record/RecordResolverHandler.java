package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@Immutable
public interface RecordResolverHandler {

    static RecordResolverHandler getBeanPatternHandler() {
        return RecordResolverSupport.getBeanPatternHandler();
    }

    static RecordResolverHandler getNamingPatternHandler() {
        return RecordResolverSupport.getNamingPatternHandler();
    }

    void resolve(Type recordType, @Out Context context);

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
