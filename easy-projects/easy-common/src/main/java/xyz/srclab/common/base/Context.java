package xyz.srclab.common.base;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.SetHelper;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

public class Context {

    public static long currentMillis() {
        return System.currentTimeMillis();
    }

    public static long currentNano() {
        return System.nanoTime();
    }

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static boolean hasPackage(String packageName) {
        return getPackage(packageName) != null;
    }

    @Nullable
    public static Package getPackage(String packageName) {
        @Nullable Package pkg = Package.getPackage(packageName);
        return pkg;
    }

    public static boolean hasClass(String className) {
        return getClass(className) != null;
    }

    public static boolean hasClass(String className, ClassLoader classLoader) {
        return getClass(className, classLoader) != null;
    }

    @Nullable
    public static <T> Class<T> getClass(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Class<T> getClass(String className, ClassLoader classLoader) {
        try {
            return (Class<T>) Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean hasResource(String resourceName) {
        return getResource(resourceName) != null;
    }

    @Nullable
    public static URL getResource(String resourceName) {
        return getClassLoader().getResource(resourceName);
    }

    @Immutable
    public static Set<URL> getResources(String resourceName) {
        try {
            Enumeration<URL> urlEnumeration = getClassLoader().getResources(resourceName);
            return SetHelper.enumerationToSet(urlEnumeration);
        } catch (Exception e) {
            throw new ExceptionWrapper(e);
        }
    }

    public static boolean hasResource(String resourceName, ClassLoader classLoader) {
        return getResource(resourceName, classLoader) != null;
    }

    @Nullable
    public static URL getResource(String resourceName, ClassLoader classLoader) {
        return classLoader.getResource(resourceName);
    }

    @Immutable
    public static Set<URL> getResources(String resourceName, ClassLoader classLoader) {
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(resourceName);
            return SetHelper.enumerationToSet(urlEnumeration);
        } catch (Exception e) {
            throw new ExceptionWrapper(e);
        }
    }
}
