package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.reflect.JieReflect;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.mapper.JieMapper;

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
 *                 If {@link JieMapper.Options#reusePolicy()} is not {@link JieMapper.Options#NO_REUSE},
 *                 return source object, else return {@code null};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is assignable from source type by {@link JieReflect#isAssignableFrom(Type, Type)}:
 *         <ul>
 *             <li>
 *                 If {@link JieMapper.Options#reusePolicy()} is {@link JieMapper.Options#REUSE_ASSIGNABLE},
 *                 return source object, else return {@code null};
 *             </li>
 *         </ul>
 *     </li>
 *     <li>
 *         If target type is {@link TypeVariable}, return {@link Flag#BREAK};
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
public class ReuseConvertHandler implements JieMapper.Handler {

    /**
     * An instance.
     */
    public static final ReuseConvertHandler INSTANCE = new ReuseConvertHandler();

    @Override
    public @Nullable Object map(@Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
        if (source == null) {
            return null;
        }
        int reusePolicy = mapper.getOptions().reusePolicy();
        if (Objects.equals(targetType, sourceType)) {
            if (reusePolicy != JieMapper.Options.NO_REUSE) {
                return source;
            }
            return null;
        }
        if (JieReflect.isAssignableFrom(targetType, sourceType)) {
            if (reusePolicy == JieMapper.Options.REUSE_ASSIGNABLE) {
                return source;
            }
            return null;
        }
        if (targetType instanceof TypeVariable<?>) {
            return Flag.BREAK;
        }
        if (sourceType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) sourceType;
            Type sourceUpper = JieReflect.getUpperBound(wildcardType);
            if (sourceUpper != null) {
                return mapper.asHandler().map(source, sourceUpper, targetType, null);
            } else {
                Type sourceLower = JieReflect.getLowerBound(wildcardType);
                if (sourceLower != null) {
                    return mapper.asHandler().map(source, Object.class, targetType, null);
                }
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) targetType;
            Type targetUpper = JieReflect.getUpperBound(wildcardType);
            if (targetUpper != null) {
                return mapper.asHandler().map(source, sourceType, targetUpper, null);
            } else {
                Type targetLower = JieReflect.getLowerBound(wildcardType);
                if (targetLower != null) {
                    return mapper
                        .withOptions(mapper.getOptions().replaceReusePolicy(JieMapper.Options.REUSE_EQUAL))
                        .asHandler()
                        .map(source, sourceType, targetLower, null);
                }
            }
        }
        return null;
    }
}
