package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

public class EnvironmentHelper {

    public static boolean hasPackage(String packageName) {
        return findPackage(packageName) != null;
    }

    @Nullable
    public static Package findPackage(String packageName) {
        @Nullable Package pkg = Package.getPackage(packageName);
        return pkg;
    }

    public static boolean hasClass(String className) {
        return findClass(className) != null;
    }

    @Nullable
    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
