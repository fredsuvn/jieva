package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

import static xyz.srclab.common.convert.FsConverter.*;

/**
 * Convert handler implementation which is used to check type compatibility, it follows:
 * <ul>
 *     <li>
 *         If the {@code obj} is null, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If {@code fromType} is equals to {@code targetType}, return {@code obj};
 *     </li>
 *     <li>
 *         If {@code targetType} is assignable from {@code fromType} with {@link FsType#isAssignableFrom(Type, Type)},
 *         return {@code obj};
 *     </li>
 *     <li>
 *         If {@code targetType} is {@link TypeVariable}, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If {@code targetType} is {@link WildcardType} and has an upper bound (? extends),
 *         return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If {@code targetType} is {@link WildcardType} and has an lower bound (? super), return {@code obj};
 *     </li>
 *     <li>
 *         If {@code fromType} is {@link TypeVariable} or {@link WildcardType}, the {@code fromType} will be replaced by
 *         {@link Object#getClass()} of {@code obj};
 *     </li>
 * </ul>
 * This handler is system default prefix handler.
 *
 * @author fredsuvn
 */
public class AssignableConvertHandler implements FsConverter.Handler {

    @Override
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        if (obj == null) {
            return BREAK;
        }
        if (Objects.equals(targetType, fromType)) {
            return obj;
        }
        if (FsType.isAssignableFrom(targetType, fromType)) {
            return obj;
        }
        if (targetType instanceof TypeVariable<?>) {
            return BREAK;
        }
        if (targetType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) targetType;
            if (FsType.getUpperBound(wildcardType) != null) {
                return BREAK;
            } else {
                return obj;
            }
        }
        Type fromBound = fromBound(obj, fromType);
        if (fromBound != fromType) {
            Object value = converter.convert(obj, fromBound, targetType);
            if (value == UNSUPPORTED) {
                return BREAK;
            }
            return value;
        }
        return CONTINUE;
    }

    private Type fromBound(Object obj, Type fromType) {
        if (fromType instanceof TypeVariable || fromType instanceof WildcardType) {
            return obj.getClass();
        }
        return fromType;
    }
}
