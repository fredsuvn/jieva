package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

import static xyz.srclab.common.convert.FsConverter.Options;

/**
 * Convert handler implementation which is used to check type compatibility and reusability, it follows in order:
 * <ul>
 *     <li>
 *         If source object is null, return {@link Fs#CONTINUE};
 *     </li>
 *     <li>
 *         If target type and source type are equal:
 *         <ul>
 *             <li>
 *                 If {@link Options#reusePolicy()} is not {@link Options#NO_REUSE},
 *                 return source object, else return {@link Fs#CONTINUE};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is assignable from source type by {@link FsType#isAssignableFrom(Type, Type)}:
 *         <ul>
 *             <li>
 *                 If {@link Options#reusePolicy()} is {@link Options#REUSE_ASSIGNABLE},
 *                 return source object, else return {@link Fs#CONTINUE};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is {@link TypeVariable}, return {@link Fs#BREAK};
 *     </li>
 *     <li>
 *         If source type is {@link WildcardType}:
 *         <ul>
 *             <li>
 *                 If source type has an upper bound (? extends), return:
 *                 <pre>
 *                     //sourceUpper is upper bound of source type
 *                     converter.convertObject(source, sourceUpper, targetType, options);
 *                 </pre>
 *             </li>
 *             <li>
 *                 If source type has a lower bound (? super), return:
 *                 <pre>
 *                     converter.convertObject(source, Object.class, targetType, options);
 *                 </pre>
 *             </li>
 *         </ul>
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
 *         Else return {@link Fs#CONTINUE};
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
            return Fs.CONTINUE;
        }
        int reusePolicy = options.reusePolicy();
        if (Objects.equals(targetType, sourceType)) {
            if (reusePolicy != Options.NO_REUSE) {
                return source;
            }
            return Fs.CONTINUE;
        }
        if (FsType.isAssignableFrom(targetType, sourceType)) {
            if (reusePolicy == Options.REUSE_ASSIGNABLE) {
                return source;
            }
            return Fs.CONTINUE;
        }
        if (targetType instanceof TypeVariable<?>) {
            return Fs.BREAK;
        }
        if (sourceType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) sourceType;
            Type sourceUpper = FsType.getUpperBound(wildcardType);
            if (sourceUpper != null) {
                return converter.convertObject(source, sourceUpper, targetType, options);
            } else {
                Type sourceLower = FsType.getLowerBound(wildcardType);
                if (sourceLower != null) {
                    return converter.convertObject(source, Object.class, targetType, options);
                }
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) targetType;
            Type targetUpper = FsType.getUpperBound(wildcardType);
            if (targetUpper != null) {
                return converter.convertObject(source, sourceType, targetUpper, options);
            } else {
                Type targetLower = FsType.getLowerBound(wildcardType);
                if (targetLower != null) {
                    return converter.convertObject(source, sourceType, targetLower, options.replaceReusePolicy(Options.REUSE_EQUAL));
                }
            }
        }
        return Fs.CONTINUE;
    }
}
