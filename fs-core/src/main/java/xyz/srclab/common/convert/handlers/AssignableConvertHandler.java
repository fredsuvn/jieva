package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Convert handler implementation which is used to check type compatibility, it follows:
 * <ul>
 *     <li>
 *         If the {@code obj} is null, return {@link #CONTINUE};
 *     </li>
 *     <li>
 *         If {@code fromType} is equals to {@code targetType}, return {@code obj};
 *     </li>
 *     <li>
 *         If {@code targetType} is assignable from {@code fromType} with {@link FsType#isAssignableFrom(Type, Type)},
 *         return {@code obj};
 *     </li>
 * </ul>
 *
 * @author fredsuvn
 */
public class AssignableConvertHandler implements FsConvertHandler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return CONTINUE;
        }
        if (FsType.isAssignableFrom(targetType, fromType)) {
            return obj;
        }
        if (targetType instanceof TypeVariable<?>) {

        }
        return CONTINUE;
    }
}
