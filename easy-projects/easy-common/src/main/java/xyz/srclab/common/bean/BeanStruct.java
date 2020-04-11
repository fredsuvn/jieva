package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.SignatureHelper;

import java.util.Map;

@Immutable
public interface BeanStruct {

    Class<?> getType();

    default boolean canReadProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        @Nullable BeanProperty property = getProperty(propertyName);
        return property != null && property.isWriteable();
    }

    @Nullable
    BeanProperty getProperty(String propertyName);

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    @Immutable
    Map<String, BeanProperty> getReadableProperties();

    @Immutable
    Map<String, BeanProperty> getWriteableProperties();

    default @Nullable BeanMethod getMethod(String methodName, Class<?>... parameterTypes) {
        return getMethodBySignature(SignatureHelper.signMethod(methodName, parameterTypes));
    }

    @Nullable
    BeanMethod getMethodBySignature(String methodSignature);

    /**
     * Keys are method signatures.
     *
     * @return Immutable map contains method signature and bean method
     */
    @Immutable
    Map<String, BeanMethod> getAllMethods();
}
