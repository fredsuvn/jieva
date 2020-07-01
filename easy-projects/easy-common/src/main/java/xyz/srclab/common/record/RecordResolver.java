package xyz.srclab.common.record;

import java.lang.reflect.Type;

/**
 * @author sunqian
 */
public interface RecordResolver {

    static RecordResolver defaultResolver() {
        return RecordResolverSupport.defaultResolver();
    }

    static RecordResolverBuilder newBuilder() {
        return RecordResolverBuilder.newBuilder();
    }

    RecordType resolve(Type type);
}
