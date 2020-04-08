package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.reflect.SignatureHelper;

import java.util.Map;
import java.util.Optional;

@Immutable
public interface BeanClass {

    Class<?> getType();

    default boolean canReadProperty(String propertyName) {
        Optional<BeanProperty> optional = getProperty(propertyName);
        return optional.isPresent() && optional.get().isReadable();
    }

    default boolean canWriteProperty(String propertyName) {
        Optional<BeanProperty> optional = getProperty(propertyName);
        return optional.isPresent() && optional.get().isWriteable();
    }

    Optional<BeanProperty> getProperty(String propertyName);

    @Immutable
    Map<String, BeanProperty> getAllProperties();

    @Immutable
    Map<String, BeanProperty> getReadableProperties();

    @Immutable
    Map<String, BeanProperty> getWriteableProperties();

    default Optional<BeanMethod> getMethod(String methodName, Class<?>... parameterTypes) {
        return getMethodBySignature(SignatureHelper.signMethod(methodName, parameterTypes));
    }

    Optional<BeanMethod> getMethodBySignature(String methodSignature);

    /**
     * Keys are method signatures.
     *
     * @return Immutable map contains method signature and bean method
     */
    @Immutable
    Map<String, BeanMethod> getAllMethods();
}
