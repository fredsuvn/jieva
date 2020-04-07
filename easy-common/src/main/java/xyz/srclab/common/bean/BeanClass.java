package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.SignatureHelper;

import java.util.Map;

@Immutable
public interface BeanClass {

    Class<?> getType();

    boolean containsProperty(String propertyName);

    default boolean canReadProperty(String propertyName) {
        if (!containsProperty(propertyName)) {
            return false;
        }
        return getProperty(propertyName).isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        if (!containsProperty(propertyName)) {
            return false;
        }
        return getProperty(propertyName).isWriteable();
    }

    BeanProperty getProperty(String propertyName) throws BeanPropertyNotFoundException;

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    default boolean containsMethod(String methodName, Class<?>... parameterTypes) {
        return containsMethodBySignature(SignatureHelper.signMethod(methodName, parameterTypes));
    }

    boolean containsMethodBySignature(String methodSignature);

    default BeanMethod getMethod(String methodName, Class<?>... parameterTypes) throws BeanMethodNotFoundException {
        return getMethodBySignature(SignatureHelper.signMethod(methodName, parameterTypes));
    }

    BeanMethod getMethodBySignature(String methodSignature) throws BeanMethodNotFoundException;

    /**
     * Keys are method signatures.
     *
     * @return
     */
    @Immutable
    Map<String, BeanMethod> getAllMethods();
}
