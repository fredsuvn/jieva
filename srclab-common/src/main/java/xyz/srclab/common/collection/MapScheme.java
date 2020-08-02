package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author sunqian
 */
@Immutable
public interface MapScheme {

    static MapScheme getMapScheme(Type type) {
        return SchemeSupport.getMapScheme(type);
    }

    Type mapType();

    Class<? extends Map<?, ?>> rawMapType();

    Type keyType();

    Type valueType();
}
