package xyz.srclab.common.system;

import xyz.srclab.annotation.Nullable;

public class ClassPathHelper {

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
