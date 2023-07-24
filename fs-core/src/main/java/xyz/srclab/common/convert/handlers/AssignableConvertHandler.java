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
 * Convert handler implementation which is used to check type compatibility, it follows in order:
 * <ul>
 *     <li>
 *         If source object is null, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If object generation policy is {@link Options#ALWAYS_NEW}, return {@link FsConverter#CONTINUE};
 *     </li>
 *     <li>
 *         If target type is equal to source type, return source object;
 *     </li>
 *     <li>
 *         If object generation policy is {@link Options#NEED_ASSIGNABLE}
 *         and target type is assignable from source type, return source object;
 *     </li>
 *     <li>
 *         If target type is {@link TypeVariable}, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If target type is {@link WildcardType} and has an upper bound (? extends),
 *         return
 *         <pre>
 *             converter.convert(source, sourceType, targetUpper, options);
 *         </pre>
 *     </li>
 *     <li>
 *         If target type is {@link WildcardType} and has a lower bound (? super),
 *         return
 *         <pre>
 *             converter.convert(
 *                     source, sourceType, targetLower, options.replaceObjectGenerationPolicy(Options.NEED_EQUAL));
 *         </pre>
 *     </li>
 *     <li>
 *         Else return {@link FsConverter#CONTINUE};
 *     </li>
 * </ul>
 * This handler is system default prefix handler.
 *
 * @author fredsuvn
 */
public class AssignableConvertHandler implements FsConverter.Handler {

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, Options options, FsConverter converter) {
        if (source == null) {
            return BREAK;
        }
        if (options.objectGenerationPolicy() == Options.ALWAYS_NEW) {
            return CONTINUE;
        }
        if (Objects.equals(targetType, sourceType)) {
            return source;
        }
        if (options.objectGenerationPolicy() == Options.NEED_ASSIGNABLE
            && FsType.isAssignableFrom(targetType, sourceType)) {
            return source;
        }
        if (targetType instanceof TypeVariable<?>) {
            return BREAK;
        }
        if (targetType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) targetType;
            Type targetUpper = FsType.getUpperBound(wildcardType);
            if (targetUpper != null) {
                return converter.convert(source, sourceType, targetUpper, options);
            }
            Type targetLower = FsType.getLowerBound(wildcardType);
            if (targetLower != null) {
                return converter.convert(
                    source, sourceType, targetLower, options.replaceObjectGenerationPolicy(Options.NEED_EQUAL));
            }
        }
        return CONTINUE;
    }
}
