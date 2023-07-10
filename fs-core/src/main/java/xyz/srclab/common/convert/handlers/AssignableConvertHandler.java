package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;

/**
 * Convert handler implementation which checks whether {@code targetType} can be assigned from {@code fromType}
 * (by {@link FsType#isAssignableFrom(Type, Type)}),
 * if the {@code obj} is null or it can not be assigned, return {@link #NOT_SUPPORTED},
 * else return the {@code obj} it self.
 *
 * @author fredsuvn
 */
public class AssignableConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return NOT_SUPPORTED;
        }
        if (FsType.isAssignableFrom(targetType, fromType)) {
            return obj;
        }
        return NOT_SUPPORTED;
    }
}
