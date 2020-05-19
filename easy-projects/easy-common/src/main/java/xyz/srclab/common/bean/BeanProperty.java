package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

@Immutable
public interface BeanProperty extends BeanMember {

    Class<?> getOwnerType();

    Class<?> getType();

    Type getGenericType();

    boolean isReadable();

    @Nullable
    Object getValue(Object bean) throws UnsupportedOperationException;

    boolean isWriteable();

    void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException;

    @Nullable
    Field getField();

    @Immutable
    List<Annotation> getFieldAnnotations() throws UnsupportedOperationException;
}
