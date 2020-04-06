package xyz.srclab.common.reflect.method;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.common.base.KeyHelper;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.reflect.SignatureHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class MethodHelper {

    public static final Class<?>[] EMPTY_PARAMETER_TYPES = ArrayUtils.EMPTY_CLASS_ARRAY;

    private static final Cache<Object, Method> METHOD_CACHE = new ThreadLocalCache<>();

    private static final Cache<Object, List<Method>> METHODS_CACHE = new ThreadLocalCache<>();

    public static Method getMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
        return METHOD_CACHE.getNonNull(
                KeyHelper.buildKey(cls, methodName, parameterTypes),
                k -> getMethod0(cls, methodName, parameterTypes)
        );
    }

    private static Method getMethod0(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
        try {
            return cls.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Method> getAllMethods(Class<?> cls) {
        return METHODS_CACHE.getNonNull(
                KeyHelper.buildKey(cls, "getAllMethods"),
                k -> getAllMethods0(cls)
        );
    }

    private static List<Method> getAllMethods0(Class<?> cls) {
        List<Method> returned = new LinkedList<>();
        Class<?> current = cls;
        do {
            returned.addAll(Arrays.asList(current.getDeclaredMethods()));
            current = current.getSuperclass();
        } while (current != null);
        return returned;
    }

    public static List<Method> getOverrideableMethods(Class<?> cls) {
        return METHODS_CACHE.getNonNull(
                KeyHelper.buildKey(cls, "getOverrideableMethods"),
                k -> getOverrideableMethods0(cls)
        );
    }

    private static List<Method> getOverrideableMethods0(Class<?> cls) {
        Map<String, Method> returned = new LinkedHashMap<>();
        Class<?> current = cls;
        do {
            Method[] methods = current.getDeclaredMethods();
            for (Method method : methods) {
                if (!canOverride(method)) {
                    continue;
                }
                String signature = SignatureHelper.signMethod(method);
                if (returned.containsKey(signature)) {
                    continue;
                }
                returned.put(signature, method);
            }
            current = current.getSuperclass();
        } while (current != null);
        return Collections.unmodifiableList(new ArrayList<>(returned.values()));
    }

    public static List<Method> getPublicStaticMethods(Class<?> cls) {
        return METHODS_CACHE.getNonNull(
                KeyHelper.buildKey(cls, "getPublicStaticMethods"),
                k -> getPublicStaticMethods0(cls)
        );
    }

    private static List<Method> getPublicStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    public static List<Method> getPublicNonStaticMethods(Class<?> cls) {
        return METHODS_CACHE.getNonNull(
                KeyHelper.buildKey(cls, "getPublicNonStaticMethods"),
                kc -> getPublicNonStaticMethods0(cls)
        );
    }

    private static List<Method> getPublicNonStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }
}
