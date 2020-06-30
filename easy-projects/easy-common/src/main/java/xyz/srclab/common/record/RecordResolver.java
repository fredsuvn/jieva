package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;
import java.util.Map;

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

    @Immutable
    Map<String, RecordEntry> resolve(Type recordType);
}
