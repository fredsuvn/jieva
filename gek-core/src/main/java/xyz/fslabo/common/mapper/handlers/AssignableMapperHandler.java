package xyz.fslabo.common.mapper.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.bean.PropertyInfo;
import xyz.fslabo.common.mapper.Mapper;
import xyz.fslabo.common.mapper.MappingOptions;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * Default first {@link Mapper.Handler} of {@link Mapper#getHandlers()}, to check assignable relationship between source
 * and target types.
 * <p>
 * In this handler, if value of {@link MappingOptions#getCopyLevel()} equals to {@link MappingOptions#COPY_LEVEL_EQUAL}
 * and the source type equals to target type, return source object wrapped by {@link #wrapResult(Object)}
 * ({@code return wrapResult(source)}).
 * <p>
 * Else if target type is {@link WildcardType} and has a lower bound (represents {@code ? super T}), return
 * {@code new Object()}.
 * <p>
 * Else if target type is {@link WildcardType} and has an upper bound (represents {@code ? extends T}), let the upper
 * bound be {@code upperBound}, return {@code mapper.asHandler().map(source, sourceType, upperBound, mapper, options)}
 * or {@code mapper.asHandler().mapProperty(source, sourceType, upper, targetProperty, mapper, options)}.
 * <p>
 * Else if target type is {@link TypeVariable} and has only one bound (represents {@code T extends X}, excludes
 * {@code T extends X & Y}), let the bound be {@code bound}, return
 * {@code mapper.asHandler().map(source, sourceType, bound, mapper, options)} or
 * {@code mapper.asHandler().mapProperty(source, sourceType, bound, targetProperty, mapper, options)}.
 * <p>
 * Otherwise, return {@link Flag#CONTINUE}.
 *
 * @author fredsuvn
 */
public class AssignableMapperHandler implements Mapper.Handler {

    public AssignableMapperHandler() {
    }

    @Override
    public Object map(
        @Nullable Object source, Type sourceType, Type targetType, Mapper mapper, MappingOptions options) {
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_ASSIGNABLE
            && JieReflect.isAssignable(targetType, sourceType)) {
            return wrapResult(source);
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = JieReflect.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = JieReflect.getUpperBound(targetWildcard);
            return mapper.asHandler().map(source, sourceType, upper, mapper, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return mapper.asHandler().map(source, sourceType, uppers[0], mapper, options);
            }
        }
        return Flag.CONTINUE;
    }

    @Override
    public Object mapProperty(
        @Nullable Object source, Type sourceType, Type targetType, PropertyInfo targetProperty, Mapper mapper, MappingOptions options) {
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_EQUAL && Objects.equals(source, targetType)) {
            return wrapResult(source);
        }
        if (options.getCopyLevel() == MappingOptions.COPY_LEVEL_ASSIGNABLE
            && JieReflect.isAssignable(targetType, sourceType)) {
            return wrapResult(source);
        }
        if (targetType instanceof WildcardType) {
            WildcardType targetWildcard = (WildcardType) targetType;
            Type lower = JieReflect.getLowerBound(targetWildcard);
            // ? super T
            if (lower != null) {
                return new Object();
            }
            // ? extends T
            Type upper = JieReflect.getUpperBound(targetWildcard);
            return mapper.asHandler().mapProperty(source, sourceType, upper, targetProperty, mapper, options);
        }
        if (targetType instanceof TypeVariable<?>) {
            TypeVariable<?> targetTypeVariable = (TypeVariable<?>) targetType;
            // T extends
            Type[] uppers = targetTypeVariable.getBounds();
            if (uppers.length == 1) {
                return mapper.asHandler().mapProperty(source, sourceType, uppers[0], targetProperty, mapper, options);
            }
        }
        return Flag.CONTINUE;
    }
}
