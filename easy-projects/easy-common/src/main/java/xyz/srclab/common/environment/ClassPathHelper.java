package xyz.srclab.common.environment;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.set.SetHelper;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathHelper {

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

    @Nullable
    public static <T> Class<T> getClass(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean hasResource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName) != null;
    }

    @Nullable
    public static URL getResource(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName);
    }

    @Immutable
    public static Set<URL> getResources(String resourceName) {
        try {
            Enumeration<URL> urlEnumeration = Thread.currentThread().getContextClassLoader().getResources(resourceName);
            Set<URL> result = new LinkedHashSet<>();
            while (urlEnumeration.hasMoreElements()) {
                result.add(urlEnumeration.nextElement());
            }
            return SetHelper.immutable(result);
        } catch (Exception e) {
            throw new ExceptionWrapper(e);
        }
    }
}
