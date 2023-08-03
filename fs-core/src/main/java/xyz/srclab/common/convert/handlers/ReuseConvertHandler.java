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
 * Convert handler implementation which is used to check type compatibility and reusability, it follows in order:
 * <ul>
 *     <li>
 *         If source object is null, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If target type and source type are equal:
 *         <ul>
 *             <li>
 *                 If {@link Options#reusePolicy()} is not {@link Options#NO_REUSE},
 *                 return source object, else return {@link FsConverter#CONTINUE};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is assignable from source type by {@link FsType#isAssignableFrom(Type, Type)}:
 *         <ul>
 *             <li>
 *                 If {@link Options#reusePolicy()} is {@link Options#REUSE_ASSIGNABLE},
 *                 return source object, else return {@link FsConverter#CONTINUE};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is {@link TypeVariable}, return {@link FsConverter#BREAK};
 *     </li>
 *     <li>
 *         If target type is {@link WildcardType}:
 *         <ul>
 *             <li>
 *                 If target type has an upper bound (? extends), return:
 *                 <pre>
 *                     //targetUpper is upper bound of target type
 *                     converter.convert(source, sourceType, targetUpper, options);
 *                 </pre>
 *             </li>
 *             <li>
 *                 If target type has a lower bound (? super), return:
 *                 <pre>
 *                     //targetLower is lower bound of target type
 *                     converter.convert(source, sourceType, targetLower, options.replaceReusePolicy(Options.REUSE_EQUAL));
 *                 </pre>
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         Else return {@link FsConverter#CONTINUE};
 *     </li>
 * </ul>
 * This handler is system default prefix handler.
 *
 * @author fredsuvn
 */
public class ReuseConvertHandler implements FsConverter.Handler {

    @Override
    public @Nullable Object convert(
        @Nullable Object source, Type sourceType, Type targetType, Options options, FsConverter converter) {
        if (source == null) {
            return BREAK;
        }
        int reusePolicy = options.reusePolicy();
        if (Objects.equals(targetType, sourceType)) {
            if (reusePolicy != Options.NO_REUSE) {
                return source;
            }
            return CONTINUE;
        }
        if (FsType.isAssignableFrom(targetType, sourceType)) {
            if (reusePolicy == Options.REUSE_ASSIGNABLE) {
                return source;
            }
            return CONTINUE;
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
                return converter.convert(source, sourceType, targetLower, options.replaceReusePolicy(Options.REUSE_EQUAL));
            }
        }
        return CONTINUE;
    }
}
