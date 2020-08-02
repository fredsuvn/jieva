package xyz.srclab.common.record;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
@Immutable
public interface RecordType {

    static RecordType newRecordType(Type type, Map<String, RecordEntry> entryMap) {
        return RecordResolverSupport.newRecordType(type, entryMap);
    }

    Class<?> type();

    Type genericType();

    @Immutable
    Map<String, RecordEntry> entryMap();
}
