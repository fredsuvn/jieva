package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.Optional;

public class EnvironmentHelper {

    public static Optional<Package> findPackage(String packageName) {
        @Nullable Package pkg = Package.getPackage(packageName);
        return Optional.ofNullable(pkg);
    }

    public static Optional<Class<?>> findClass(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
