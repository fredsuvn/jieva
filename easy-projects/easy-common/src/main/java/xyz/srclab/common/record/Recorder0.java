package xyz.srclab.common.record;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

import java.util.Map;

/**
 * @author sunqian
 */
final class Recorder0 {

    static Map<Object, @Nullable Object> anyAsMap(Recorder _this, Object recordOrMap) {
        return Cast.as(recordOrMap instanceof Map ? recordOrMap : _this.asMap(recordOrMap));
    }
}
