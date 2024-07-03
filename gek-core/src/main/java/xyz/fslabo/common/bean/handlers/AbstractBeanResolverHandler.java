package xyz.fslabo.common.bean.handlers;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.GekRuntimeException;
import xyz.fslabo.common.bean.*;
import xyz.fslabo.common.collect.JieColl;
import xyz.fslabo.common.invoke.GekInvoker;
import xyz.fslabo.common.reflect.JieReflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Base abstract gek bean resolving handler, provides a skeletal implementation of the {@link BeanResolver.Handler}
 * to minimize the effort required to implement the interface backed by "method-based" getters/setters.
 * <p>
 * This method uses {@link Class#getMethods()} to find out all methods, then put each of them into
 * {@link #resolveGetter(Method)} and {@link #resolveSetter(Method)} method to determine whether it is a getter/setter.
 * If it is, it will be resolved to a property descriptor, else it will be resolved to be a {@link MethodInfo}.
 * Subtypes can use {@link #buildGetter(String, Method)} and {@link #buildSetter(String, Method)} to create
 * getter/setter.
 *
 * @author fredsuvn
 */
public abstract class AbstractBeanResolverHandler implements BeanResolver.Handler {

    @Override
    public @Nullable Flag resolve(BeanResolver.Context context) throws BeanResolvingException {
        Type type = context.getType();
        try {
            Class<?> rawType = JieReflect.getRawType(type);
            if (rawType == null) {
                throw new BeanResolvingException("The type to be resolved must be Class or ParameterizedType: " + type + ".");
            }
            Method[] methods = rawType.getMethods();
            Map<String, Method> getters = new LinkedHashMap<>();
            Map<String, Method> setters = new LinkedHashMap<>();
            for (Method method : methods) {
                if (method.isBridge()) {
                    continue;
                }
                Getter getter = resolveGetter(method);
                if (getter != null) {
                    getters.put(getter.getName(), method);
                    continue;
                }
                Setter setter = resolveSetter(method);
                if (setter != null) {
                    setters.put(setter.getName(), method);
                    continue;
                }
                context.getMethods().add(new BaseMethodInfoImpl(method));
            }
            if (JieColl.isNotEmpty(getters) || JieColl.isNotEmpty(setters)) {
                mergeAccessors(context, getters, setters, rawType);
            }
            return null;
        } catch (BeanResolvingException e) {
            throw e;
        } catch (Exception e) {
            throw new BeanResolvingException(type, e);
        }
    }

    private void mergeAccessors(
        BeanResolver.Context context, Map<String, Method> getters, Map<String, Method> setters, Class<?> rawType) {
        Map<TypeVariable<?>, Type> typeParameterMapping = JieReflect.getTypeParameterMapping(context.getType());
        Set<Type> stack = new HashSet<>();
        getters.forEach((name, getter) -> {
            Method setter = setters.get(name);
            // Type of getter and setter must be same.
            // But field's type will not be checked,
            // because field is only a name holder for adding Annotations conveniently.
            Type returnType = getter.getGenericReturnType();
            returnType = getActualType(returnType, typeParameterMapping, stack);
            if (setter != null) {
                Type setType = setter.getGenericParameterTypes()[0];
                setType = getActualType(setType, typeParameterMapping, stack);
                if (!Objects.equals(returnType, setType)) {
                    // Different returned type and set type.
                    return;
                }
            }
            context.getProperties().put(name, new BasePropertyInfoImpl(name, getter, setter, returnType, rawType));
            setters.remove(name);
        });
        setters.forEach((name, setter) -> {
            Type setType = setter.getGenericParameterTypes()[0];
            setType = getActualType(setType, typeParameterMapping, stack);
            context.getProperties().put(name, new BasePropertyInfoImpl(name, null, setter, setType, rawType));
        });
    }

    /**
     * Resolves and returns given method as a getter, or null if given method is not a getter.
     *
     * @param method given method
     * @return given method as a getter
     */
    @Nullable
    protected abstract Getter resolveGetter(Method method);

    /**
     * Resolves and returns given method as a setter, or null if given method is not a setter.
     *
     * @param method given method
     * @return given method as a setter
     */
    @Nullable
    protected abstract Setter resolveSetter(Method method);

    /**
     * Builds a getter.
     *
     * @param name   name of getter
     * @param method source method
     * @return a getter
     */
    protected Getter buildGetter(String name, Method method) {
        return () -> name;
    }

    /**
     * Builds a setter.
     *
     * @param name   name of setter
     * @param method source method
     * @return a setter
     */
    protected Setter buildSetter(String name, Method method) {
        return () -> name;
    }

    private Type getActualType(Type type, Map<TypeVariable<?>, Type> typeParameterMapping, Set<Type> stack) {
        if (type instanceof Class) {
            return type;
        }
        stack.clear();
        Type result = JieColl.getNested(typeParameterMapping, type, stack);
        if (result == null) {
            return type;
        }
        return result;
    }

    /**
     * Getter info.
     */
    public interface Getter {
        /**
         * Returns name of this getter.
         *
         * @return name of this getter
         */
        String getName();
    }

    /**
     * Setter info.
     */
    public interface Setter {
        /**
         * Returns name of this setter.
         *
         * @return name of this setter
         */
        String getName();
    }

    private static final class BasePropertyInfoImpl implements BasePropertyInfo {

        private static final Field EMPTY_FIELD;
        private static final GekInvoker EMPTY_INVOKER;

        static {
            try {
                EMPTY_FIELD = BasePropertyInfoImpl.class.getDeclaredField("EMPTY_FIELD");
                EMPTY_INVOKER = new GekInvoker() {
                    @Override
                    public @Nullable Object invoke(@Nullable Object inst, Object... args) {
                        return null;
                    }
                };
            } catch (NoSuchFieldException e) {
                throw new GekRuntimeException(e);
            }
        }

        private final String name;
        private final @Nullable Method getter;
        private final @Nullable Method setter;
        private final Type type;
        private final Class<?> rawType;
        private GekInvoker getterInvoker;
        private GekInvoker setterInvoker;
        private Field field;
        private List<Annotation> getterAnnotations;
        private List<Annotation> setterAnnotations;
        private List<Annotation> fieldAnnotations;
        private List<Annotation> allAnnotations;

        private BasePropertyInfoImpl(
            String name, @Nullable Method getter, @Nullable Method setter, Type type, Class<?> rawType) {
            this.name = name;
            this.getter = getter;
            this.getterInvoker = getter == null ? EMPTY_INVOKER : null;
            this.setter = setter;
            this.setterInvoker = setter == null ? EMPTY_INVOKER : null;
            this.type = type;
            this.rawType = rawType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object getValue(Object bean) {
            if (getterInvoker == EMPTY_INVOKER) {
                throw new BeanException("PropertyInfo is not readable: " + name + ".");
            }
            if (getterInvoker == null) {
                getterInvoker = buildMethodInvoker(getter);
            }
            return getterInvoker.invoke(bean);
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) {
            if (setterInvoker == EMPTY_INVOKER) {
                throw new BeanException("PropertyInfo is not writeable: " + name + ".");
            }
            if (setterInvoker == null) {
                setterInvoker = buildMethodInvoker(getter);
            }
            setterInvoker.invoke(bean, value);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public @Nullable Method getGetter() {
            return getter;
        }

        @Override
        public @Nullable Method getSetter() {
            return setter;
        }

        @Override
        public @Nullable Field getField() {
            if (field == null) {
                field = findField(name, rawType);
            }
            return field == EMPTY_FIELD ? null : field;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            if (getterAnnotations == null) {
                getterAnnotations = getter == null ? Collections.emptyList() : JieColl.listOf(getter.getAnnotations());
            }
            return getterAnnotations;
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            if (setterAnnotations == null) {
                setterAnnotations = setter == null ? Collections.emptyList() : JieColl.listOf(setter.getAnnotations());
            }
            return setterAnnotations;
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            if (fieldAnnotations == null) {
                Field field = getField();
                fieldAnnotations = field == null ? Collections.emptyList() : JieColl.listOf(field.getAnnotations());
            }
            return fieldAnnotations;
        }

        @Override
        public List<Annotation> getAnnotations() {
            if (allAnnotations == null) {
                List<Annotation> annotations = new ArrayList<>(
                    getGetterAnnotations().size() + getSetterAnnotations().size() + getFieldAnnotations().size());
                annotations.addAll(getGetterAnnotations());
                annotations.addAll(getSetterAnnotations());
                annotations.addAll(getFieldAnnotations());
                allAnnotations = Collections.unmodifiableList(annotations);
            }
            return allAnnotations;
        }

        @Override
        public boolean isReadable() {
            return getterInvoker != null;
        }

        @Override
        public boolean isWriteable() {
            return setterInvoker != null;
        }

        private Field findField(String name, Class<?> type) {
            try {
                return type.getField(name);
            } catch (NoSuchFieldException e) {
                Class<?> cur = type;
                while (cur != null) {
                    try {
                        return cur.getDeclaredField(name);
                    } catch (NoSuchFieldException ex) {
                        cur = cur.getSuperclass();
                    }
                }
            }
            return EMPTY_FIELD;
        }
    }

    private static final class BaseMethodInfoImpl implements BaseMethodInfo {

        private final Method method;
        private List<Annotation> annotations;
        private GekInvoker invoker;

        private BaseMethodInfoImpl(Method method) {
            this.method = method;
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public Object invoke(Object bean, Object... args) {
            if (invoker == null) {
                invoker = buildMethodInvoker(method);
            }
            return invoker.invoke(bean, args);
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public List<Annotation> getAnnotations() {
            if (annotations == null) {
                annotations = JieColl.listOf(method.getAnnotations());
            }
            return annotations;
        }
    }

    private static GekInvoker buildMethodInvoker(Method method) {
        return GekInvoker.reflectMethod(method);
    }
}
