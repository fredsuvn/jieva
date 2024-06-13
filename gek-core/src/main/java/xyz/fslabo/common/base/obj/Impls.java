package xyz.fslabo.common.base.obj;

import lombok.EqualsAndHashCode;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Gek;
import xyz.fslabo.common.reflect.GekReflect;

import java.lang.reflect.*;
import java.util.List;

final class Impls {

    @EqualsAndHashCode
    static class BaseImpl<T> implements GekObj<T> {

        protected final T obj;
        protected final Type type;

        BaseImpl(T obj, Type type) {
            this.obj = obj;
            this.type = type;
        }

        @Override
        public T getObject() {
            return obj;
        }

        @Override
        public Type getType() {
            return type;
        }
    }

    static final class ClassImpl<T> extends BaseImpl<T> implements ClassObj<T> {

        ClassImpl(T obj, Class<?> type) {
            super(obj, type);
        }

        @Override
        public Class<T> getType() {
            return Gek.as(type);
        }
    }

    static final class GenericArrayImpl<T> extends BaseImpl<T> implements GenericArrayObj<T> {

        GenericArrayImpl(T obj, GenericArrayType type) {
            super(obj, type);
        }

        @Override
        public GenericArrayType getType() {
            return Gek.as(type);
        }
    }

    static final class ParameterizedImpl<T> extends BaseImpl<T> implements ParameterizedObj<T> {

        private List<Type> actualTypeArguments;

        ParameterizedImpl(T obj, ParameterizedType type) {
            super(obj, type);
        }

        @Override
        public ParameterizedType getType() {
            return Gek.as(type);
        }

        public List<Type> getActualTypeArguments() {
            if (actualTypeArguments == null) {
                actualTypeArguments = Gek.asList(getType().getActualTypeArguments());
            }
            return actualTypeArguments;
        }
    }

    static final class TypeVariableImpl<T> extends BaseImpl<T> implements TypeVariableObj<T> {

        private List<Type> bounds;
        private List<AnnotatedType> annotatedBounds;

        TypeVariableImpl(T obj, TypeVariable<?> type) {
            super(obj, type);
        }

        @Override
        public TypeVariable<?> getType() {
            return Gek.as(type);
        }

        public List<Type> getBounds() {
            if (bounds == null) {
                bounds = Gek.asList(getType().getBounds());
            }
            return bounds;
        }

        public List<AnnotatedType> getAnnotatedBounds() {
            if (annotatedBounds == null) {
                annotatedBounds = Gek.asList(getType().getAnnotatedBounds());
            }
            return annotatedBounds;
        }
    }

    static final class WildcardImpl<T> extends BaseImpl<T> implements WildcardObj<T> {

        private Type upperBound;
        private Type lowerBound;

        WildcardImpl(T obj, WildcardType type) {
            super(obj, type);
        }

        @Override
        public WildcardType getType() {
            return Gek.as(type);
        }

        @Nullable
        public Type getUpperBound() {
            if (upperBound == null) {
                upperBound = GekReflect.getUpperBound(getType());
            }
            return upperBound;
        }

        @Nullable
        public Type getLowerBound() {
            if (lowerBound == null) {
                lowerBound = GekReflect.getLowerBound(getType());
            }
            return lowerBound;
        }
    }
}
