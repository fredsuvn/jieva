package xyz.fsgek.common.convert.handlers;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.reflect.FsReflect;
import xyz.fsgek.common.base.FsFlag;
import xyz.fsgek.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Convert handler implementation which is used to check type compatibility and reusability, it follows in order:
 * <ul>
 *     <li>
 *         If source object is null, return {@code null};
 *     </li>
 *     <li>
 *         If target type and source type are equal:
 *         <ul>
 *             <li>
 *                 If {@link FsConverter.Options#reusePolicy()} is not {@link FsConverter.Options#NO_REUSE},
 *                 return source object, else return {@code null};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is assignable from source type by {@link FsReflect#isAssignableFrom(Type, Type)}:
 *         <ul>
 *             <li>
 *                 If {@link FsConverter.Options#reusePolicy()} is {@link FsConverter.Options#REUSE_ASSIGNABLE},
 *                 return source object, else return {@code null};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is {@link TypeVariable}, return {@link FsFlag#BREAK};
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
 *         Else return null;
 *     </li>
 * </ul>
 * This handler is system default prefix handler.
 *
 * @author fredsuvn
 */
public class ReuseConvertHandler implements FsConverter.Handler {

    /**
     * An instance.
     */
    public static final ReuseConvertHandler INSTANCE = new ReuseConvertHandler();

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        if (source == null) {
            return null;
        }
        int reusePolicy = converter.getOptions().reusePolicy();
        if (Objects.equals(targetType, sourceType)) {
            if (reusePolicy != FsConverter.Options.NO_REUSE) {
                return source;
            }
            return null;
        }
        if (FsReflect.isAssignableFrom(targetType, sourceType)) {
            if (reusePolicy == FsConverter.Options.REUSE_ASSIGNABLE) {
                return source;
            }
            return null;
        }
        if (targetType instanceof TypeVariable<?>) {
            return FsFlag.BREAK;
        }
        if (sourceType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) sourceType;
            Type sourceUpper = FsReflect.getUpperBound(wildcardType);
            if (sourceUpper != null) {
                return converter.asHandler().convert(source, sourceUpper, targetType, null);
            } else {
                Type sourceLower = FsReflect.getLowerBound(wildcardType);
                if (sourceLower != null) {
                    return converter.asHandler().convert(source, Object.class, targetType, null);
                }
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) targetType;
            Type targetUpper = FsReflect.getUpperBound(wildcardType);
            if (targetUpper != null) {
                return converter.asHandler().convert(source, sourceType, targetUpper, null);
            } else {
                Type targetLower = FsReflect.getLowerBound(wildcardType);
                if (targetLower != null) {
                    return converter
                        .withOptions(converter.getOptions().replaceReusePolicy(FsConverter.Options.REUSE_EQUAL))
                        .asHandler()
                        .convert(source, sourceType, targetLower, null);
                }
            }
        }
        return null;
    }
}
