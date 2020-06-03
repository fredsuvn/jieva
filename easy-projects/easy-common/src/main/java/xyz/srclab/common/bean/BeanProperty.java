package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

@Immutable
public interface BeanProperty extends BeanMember {

    Class<?> type();

    Type genericType();

    boolean readable();

    boolean writeable();

    @Nullable
    Field field();

    @Immutable
    List<Annotation> fieldAnnotations();

    @Nullable
    Object getValue(Object bean) throws UnsupportedOperationException;

    void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException;

    Class<?> ownerType();
}
