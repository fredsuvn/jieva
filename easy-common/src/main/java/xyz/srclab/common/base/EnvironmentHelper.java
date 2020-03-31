package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

public class EnvironmentHelper {

    public static boolean hasPackage(String packageName) {
        return findPackage(packageName) != null;
    }

    @Nullable
    public static Package findPackage(String packageName) {
        return Package.getPackage(packageName);
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
