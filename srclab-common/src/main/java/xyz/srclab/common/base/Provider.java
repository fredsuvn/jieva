package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.reflect.ClassKit;

/**
 * @author sunqian
 */
public interface Provider<T> {

    static <T> Provider<T> of(Class<?> providerClass) {
        if (!Provider.class.isAssignableFrom(providerClass)) {
            throw new IllegalArgumentException("Class is not a Provider: " + providerClass);
        }
        return ClassKit.newInstance(providerClass);
    }

    static <T> Provider<T> of(String className) {
        return of(className, Loader.currentClassLoader());
    }

    static <T> Provider<T> of(String className, ClassLoader classLoader) {
        @Nullable Class<?> providerClass = Loader.findClass(className, classLoader);
        if (providerClass == null) {
            throw new IllegalArgumentException("Class not found: " + className);
        }
        return of(providerClass);
    }

    static <T> T get(String candidateClasses) {
        String[] classNames = candidateClasses.split(",");
        for (String className : classNames) {
            Provider<T> provider = of(className);
            @Nullable T product = provider.provide();
            if (product != null) {
                return product;
            }
        }
        throw new IllegalArgumentException("No available provider: " + candidateClasses);
    }

    @Nullable
    T provide();
}
