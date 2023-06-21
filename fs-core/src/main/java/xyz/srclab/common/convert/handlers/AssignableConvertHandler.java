package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Convert handler implementation supports converting between assignable types, such as:
 * <ul>
 *     <li>
 *         Same types;
 *     </li>
 *     <li>
 *         Target type is assignable from source type;
 *     </li>
 *     <li>
 *         If source object is null, return null.
 *     </li>
 * </ul>
 * This handler always return source object if the converting is supported.
 *
 * @author fredsuvn
 */
public class AssignableConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return null;
        }
        if (Objects.equals(fromType, targetType)) {
            return obj;
        }
        if (fromType instanceof Class && targetType instanceof Class<?>) {
            if (((Class<?>) targetType).isAssignableFrom((Class<?>) fromType)) {
                return obj;
            }
        }
        return FsConvertHandler.NOT_SUPPORTED;
    }
}
