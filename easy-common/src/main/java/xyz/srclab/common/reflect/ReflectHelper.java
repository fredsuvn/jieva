package xyz.srclab.common.reflect;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectHelper {

    private static final ThreadLocal<WeakHashMap<Object, List<Method>>> classMethodsCache =
            ThreadLocal.withInitial(WeakHashMap::new);

    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<Method> getAllMethods(Class<?> cls) {
        return classMethodsCache.get().computeIfAbsent(
                buildAllMethodsKey(cls),
                c -> getAllMethods0(cls)
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
        return classMethodsCache.get().computeIfAbsent(
                buildOverrideableMethodsKey(cls),
                c -> getOverrideableMethods0(cls)
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
                String signature = SignatureHelper.signatureMethodWithMethodName(method);
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
        return classMethodsCache.get().computeIfAbsent(
                buildPublicStaticMethodsKey(cls),
                c -> getPublicStaticMethods0(cls)
        );
    }

    private static List<Method> getPublicStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    public static List<Method> getPublicNonStaticMethods(Class<?> cls) {
        return classMethodsCache.get().computeIfAbsent(
                buildPublicNonStaticMethodsKey(cls),
                c -> getPublicNonStaticMethods0(cls)
        );
    }

    private static List<Method> getPublicNonStaticMethods0(Class<?> cls) {
        return Arrays.stream(cls.getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
                .collect(Collectors.toList());
    }

    private static Object buildAllMethodsKey(Class<?> cls) {
        return buildClassMethodsKey(cls, "all");
    }

    private static Object buildOverrideableMethodsKey(Class<?> cls) {
        return buildClassMethodsKey(cls, "overrideable");
    }

    private static Object buildPublicStaticMethodsKey(Class<?> cls) {
        return buildClassMethodsKey(cls, "public static");
    }

    private static Object buildPublicNonStaticMethodsKey(Class<?> cls) {
        return buildClassMethodsKey(cls, "public non-static");
    }

    private static Object buildClassMethodsKey(Class<?> cls, String methodsScope) {
        return SignatureHelper.signature(cls) + ":" + methodsScope;
    }

    public static boolean canOverride(Method method) {
        int modifiers = method.getModifiers();
        return !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }

    public static boolean isAssignable(Object from, Class<?> to) {
        Class<?> fromType = from instanceof Class<?> ? (Class<?>) from : from.getClass();
        return ClassUtils.isAssignable(fromType, to);
    }
}
