package xyz.srclab.common.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectHelper {

    private static final ThreadLocal<WeakHashMap<Class<?>, List<Method>>> classAllMethodsCache =
            ThreadLocal.withInitial(WeakHashMap::new);

    private static final ThreadLocal<WeakHashMap<Class<?>, List<Method>>> classOverrideableMethodsCache =
            ThreadLocal.withInitial(WeakHashMap::new);

    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<Method> getAllMethods(Class<?> cls) {
        return classAllMethodsCache.get().computeIfAbsent(cls, ReflectHelper::getAllMethods0);
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
        return classOverrideableMethodsCache.get().computeIfAbsent(cls, ReflectHelper::getOverrideableMethods0);
    }

    public static List<Method> getOverrideableMethods0(Class<?> cls) {
        Map<String, Method> returned = new LinkedHashMap<>();
        Class<?> current = cls;
        do {
            Method[] methods = current.getDeclaredMethods();
            for (Method method : methods) {
                if (!canOverride(method)) {
                    continue;
                }
                String signature = SignatureHelper.signatureMethodWithMethodName(method);
                if (returned.containsKey(signature)) {
                    continue;
                }
                returned.put(signature, method);
            }
            current = current.getSuperclass();
        } while (current != null);
        return new LinkedList<>(returned.values());
    }

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !Modifier.isStatic(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }
}
