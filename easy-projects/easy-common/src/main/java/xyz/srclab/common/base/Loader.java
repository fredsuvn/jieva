package xyz.srclab.common.base;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.SetHelper;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

public class Loader {

    public static ClassLoader currentClassLoader() {
        return Current.thread().getContextClassLoader();
    }

    @Nullable
    public static <T> Class<T> loadClass(String className) {
        try {
            return Cast.as(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Class<T> loadClass(String className, ClassLoader classLoader) {
        try {
            return Cast.as(Class.forName(className, true, classLoader));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static URL loadResource(String resourceName) {
        return loadResource(resourceName, currentClassLoader());
    }

    @Nullable
    public static URL loadResource(String resourceName, ClassLoader classLoader) {
        return classLoader.getResource(resourceName);
    }

    @Immutable
    public static Set<URL> loadResources(String resourceName) {
        return loadResources(resourceName, currentClassLoader());
    }

    @Immutable
    public static Set<URL> loadResources(String resourceName, ClassLoader classLoader) {
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(resourceName);
            return SetHelper.enumerationToSet(urlEnumeration);
        } catch (Exception e) {
            throw new ExceptionWrapper(e);
        }
    }
}
