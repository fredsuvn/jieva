package xyz.srclab.test.mark;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunqian
 */
public class MarkSupport {

    private static final Map<Object, Map<Object, Object>> marks = new ConcurrentHashMap<>();

    static Map<Object, Object> getMarks(Object marker) {
        return marks.computeIfAbsent(marker, k -> new ConcurrentHashMap<>());
    }
}
