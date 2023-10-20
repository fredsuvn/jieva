package xyz.fsgek.common.base.obj;

import lombok.EqualsAndHashCode;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.FsCollect;
import xyz.fsgek.common.reflect.FsReflect;
import xyz.fsgek.common.base.Fs;

import java.lang.reflect.*;
import java.util.List;

final class Impls {

    @EqualsAndHashCode
    static class BaseImpl<T> implements FsObj<T> {

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
            return Fs.as(type);
        }
    }

    static final class GenericArrayImpl<T> extends BaseImpl<T> implements GenericArrayObj<T> {

        GenericArrayImpl(T obj, GenericArrayType type) {
            super(obj, type);
        }

        @Override
        public GenericArrayType getType() {
            return Fs.as(type);
        }
    }

    static final class ParameterizedImpl<T> extends BaseImpl<T> implements ParameterizedObj<T> {

        private List<Type> actualTypeArguments;

        ParameterizedImpl(T obj, ParameterizedType type) {
            super(obj, type);
        }

        @Override
        public ParameterizedType getType() {
            return Fs.as(type);
        }

        public List<Type> getActualTypeArguments() {
            if (actualTypeArguments == null) {
                actualTypeArguments = FsCollect.immutableList(getType().getActualTypeArguments());
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
            return Fs.as(type);
        }

        public List<Type> getBounds() {
            if (bounds == null) {
                bounds = FsCollect.immutableList(getType().getBounds());
            }
            return bounds;
        }

        public List<AnnotatedType> getAnnotatedBounds() {
            if (annotatedBounds == null) {
                annotatedBounds = FsCollect.immutableList(getType().getAnnotatedBounds());
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
            return Fs.as(type);
        }

        @Nullable
        public Type getUpperBound() {
            if (upperBound == null) {
                upperBound = FsReflect.getUpperBound(getType());
            }
            return upperBound;
        }

        @Nullable
        public Type getLowerBound() {
            if (lowerBound == null) {
                lowerBound = FsReflect.getLowerBound(getType());
            }
            return lowerBound;
        }
    }
}
