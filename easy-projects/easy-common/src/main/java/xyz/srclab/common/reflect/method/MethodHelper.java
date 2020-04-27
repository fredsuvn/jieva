package xyz.srclab.common.reflect.method;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.collection.list.ListHelper;
import xyz.srclab.common.reflect.signature.SignatureHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodHelper {

    public static final Object[] EMPTY_ARGUMENTS = ArrayUtils.EMPTY_OBJECT_ARRAY;

    private static final Cache<String, Method> methodCache = new ThreadLocalCache<>();

    private static final Cache<String, List<Method>> methodsCache = new ThreadLocalCache<>();

    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        return methodCache.getNonNull(
                generateMethodCacheKey(cls, methodName, parameterTypes),
                k -> getMethod0(cls, methodName, parameterTypes)
        );
    }

    private static Method getMethod0(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Immutable
    public static List<Method> getAllMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                generateMethodsCacheKey(cls, "getAllMethods"),
                k -> ListHelper.immutable(getAllMethods0(cls))
        );
    }

    private static List<Method> getAllMethods0(Class<?> cls) {
        List<Method> result = new LinkedList<>();
        Class<?> current = cls;
        do {
            result.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        } while (current != null);
        return result;
    }

    @Immutable
    public static List<Method> getOverrideableMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                generateMethodsCacheKey(cls, "getOverrideableMethods"),
                k -> ListHelper.immutable(getOverrideableMethods0(cls))
        );
    }

    private static List<Method> getOverrideableMethods0(Class<?> cls) {
        List<Method> result = ListHelper.concat(
                Arrays.asList(cls.getMethods()),
                Arrays.asList(cls.getDeclaredMethods())
        );
        return result.stream()
                .distinct()
                .filter(MethodHelper::canOverride)
                .collect(Collectors.toList());
    }

    @Immutable
    public static List<Method> getPublicStaticMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                generateMethodsCacheKey(cls, "getPublicStaticMethods"),
                k -> ListHelper.immutable(getPublicStaticMethods0(cls))
        );
    }

    private static List<Method> getPublicStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    @Immutable
    public static List<Method> getPublicNonStaticMethods(Class<?> cls) {
        return methodsCache.getNonNull(
                generateMethodsCacheKey(cls, "getPublicNonStaticMethods"),
                kc -> ListHelper.immutable(getPublicNonStaticMethods0(cls))
        );
    }

    private static List<Method> getPublicNonStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }

    private static String generateMethodCacheKey(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        return cls + "." + SignatureHelper.signMethod(methodName, parameterTypes);
    }

    private static String generateMethodsCacheKey(Class<?> cls, String scope) {
        return cls + "(" + scope + ")";
    }
}
