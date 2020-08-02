package xyz.srclab.test.mark;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunqian
 */
final class MarkSupport {

    private static final Map<Object, Map<Object, Object>> marks = new ConcurrentHashMap<>();

    static Map<Object, Object> getMarks(Object marker) {
        return marks.computeIfAbsent(marker, m -> Collections.synchronizedMap(new LinkedHashMap<>()));
    }
}
