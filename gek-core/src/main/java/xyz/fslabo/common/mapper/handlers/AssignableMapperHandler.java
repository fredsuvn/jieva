package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MapperOptions;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * Default first handler.
 * <p>
 * If target type is assignable from source type, and the {@code deepCopy} option is false, return source object.
 * Otherwise, if target type is {@link WildcardType} or {@link TypeVariable}, this handler will try to find an explicit
 * type then invoke given {@code mapper} again, else return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class AssignableMapperHandler implements Mapper.Handler {

    /**
     * An instance.
     */
    public static final AssignableMapperHandler INSTANCE = new AssignableMapperHandler();

    @Override
    public Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MapperOptions options) {
        if (JieReflect.isAssignable(targetType, sourceType)) {
            if (!options.isDeepCopy()) {
                return wrapResult(source);
            }
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = JieReflect.getLowerBound(targetWildcard);
            // ? super
            if (lower != null) {
                if (lower instanceof Class<?>) {
                    Class<?> lowerClass = (Class<?>) lower;
                    if (lowerClass.isInterface()) {
                        return new Object();
                    }
                    Object tryLower = mapper.map(source, sourceType, lowerClass, options);
                    if (tryLower == null) {
                        return new Object();
                    }
                    return tryLower;
                }
            }
            return Flag.CONTINUE;
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = JieReflect.getUpperBounds(targetTypeVariable);
            if (uppers.length == 1) {
                Object tryUpper = mapper.map(source, sourceType, uppers[0], options);
                if (tryUpper == null) {
                    return Flag.BREAK;
                }
                return tryUpper;
            }
            return Flag.CONTINUE;
        }
        return Flag.CONTINUE;
    }
}
