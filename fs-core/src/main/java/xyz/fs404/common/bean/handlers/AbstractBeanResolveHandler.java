package xyz.fs404.common.bean.handlers;

import xyz.fs404.annotations.Nullable;
import xyz.fs404.common.base.Fs;
import xyz.fs404.common.base.FsFinal;
import xyz.fs404.common.bean.FsBean;
import xyz.fs404.common.bean.FsBeanException;
import xyz.fs404.common.bean.FsBeanProperty;
import xyz.fs404.common.bean.FsBeanResolver;
import xyz.fs404.common.collect.FsCollect;
import xyz.fs404.common.reflect.FsInvoker;
import xyz.fs404.common.reflect.FsType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * Base abstract bean resolve handler, provides a skeletal implementation of the {@link FsBeanResolver.Handler}
 * to minimize the effort required to implement the interface backed by "method-based" getters/setters.
 * <p>
 * This method uses {@link Class#getMethods()} to find out all methods, then put each of them into
 * {@link #isGetter(Method)} and {@link #isSetter(Method)} method to determine whether it is a getter/setting.
 * If it is, it will be wrapped to a property.
 * <p>
 * {@link #buildMethodInvoker(Method)} can be overridden to build custom method invoker.
 * Default is {@link FsInvoker#reflectMethod(Method)}.
 *
 * @author fredsuvn
 */
public abstract class AbstractBeanResolveHandler implements FsBeanResolver.Handler {

    @Override
    public @Nullable Object resolve(FsBeanResolver.BeanBuilder builder) {
        Type type = builder.getType();
        Class<?> rawType = FsType.getRawType(type);
        if (rawType == null) {
            throw new IllegalArgumentException("The type to be resolved must be Class or ParameterizedType.");
        }
        Method[] methods = rawType.getMethods();
        Map<TypeVariable<?>, Type> typeParameterMapping = FsType.getTypeParameterMapping(type);
        Map<String, Method> getters = new LinkedHashMap<>();
        Map<String, Method> setters = new LinkedHashMap<>();
        for (Method method : methods) {
            if (method.isBridge()) {
                continue;
            }
            String propertyName = isGetter(method);
            if (propertyName != null) {
                getters.put(propertyName, method);
                continue;
            }
            propertyName = isSetter(method);
            if (propertyName != null) {
                setters.put(propertyName, method);
            }
        }
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
                    return;
                }
            }
            Field field = findField(name, rawType);
            builder.getProperties().put(name, new DefaultFsBeanPropertyImpl(builder, name, getter, setter, field, returnType));
            setters.remove(name);
        });
        setters.forEach((name, setter) -> {
            Field field = findField(name, rawType);
            Type setType = setter.getGenericParameterTypes()[0];
            setType = getActualType(setType, typeParameterMapping, stack);
            builder.getProperties().put(name, new DefaultFsBeanPropertyImpl(builder, name, null, setter, field, setType));
        });
        return Fs.CONTINUE;
    }

    /**
     * Whether given method is a getter.
     * If it is, return name of this property, or null if it is not
     *
     * @param method given method
     */
    @Nullable
    protected abstract String isGetter(Method method);

    /**
     * Whether given method is a setter.
     * If it is, return name of this property, or null if it is not
     *
     * @param method given method
     */
    @Nullable
    protected abstract String isSetter(Method method);

    /**
     * Builds invoker of given getter/setter.
     */
    protected FsInvoker buildMethodInvoker(Method method) {
        return FsInvoker.reflectMethod(method);
    }

    private Type getActualType(Type type, Map<TypeVariable<?>, Type> typeParameterMapping, Set<Type> stack) {
        if (type instanceof Class) {
            return type;
        }
        stack.clear();
        Type result = FsCollect.getNested(typeParameterMapping, type, stack);
        if (result == null) {
            return type;
        }
        return result;
    }

    @Nullable
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
        return null;
    }

    private final class DefaultFsBeanPropertyImpl extends FsFinal implements FsBeanProperty {

        private final FsBean owner;
        private final String name;
        private final Method getter;
        private final Method setter;
        private final Field field;
        private final Type type;
        private final FsInvoker getterInvoker;
        private final FsInvoker setterInvoker;
        private final List<Annotation> getterAnnotations;
        private final List<Annotation> setterAnnotations;
        private final List<Annotation> fieldAnnotations;
        private final List<Annotation> allAnnotations;

        private DefaultFsBeanPropertyImpl(
            FsBean owner, String name,
            @Nullable Method getter, @Nullable Method setter,
            @Nullable Field field, Type type
        ) {
            this.owner = owner;
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.field = field;
            this.type = type;
            if (getter != null) {
                getterInvoker = buildMethodInvoker(getter);
            } else {
                getterInvoker = null;
            }
            if (setter != null) {
                setterInvoker = buildMethodInvoker(setter);
            } else {
                setterInvoker = null;
            }
            getterAnnotations = getter == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(getter.getAnnotations()));
            setterAnnotations = setter == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(setter.getAnnotations()));
            fieldAnnotations = field == null ? Collections.emptyList() :
                Collections.unmodifiableList(Arrays.asList(field.getAnnotations()));
            List<Annotation> annotations = new ArrayList<>(
                getterAnnotations.size() + setterAnnotations.size() + fieldAnnotations.size());
            annotations.addAll(getterAnnotations);
            annotations.addAll(setterAnnotations);
            annotations.addAll(fieldAnnotations);
            allAnnotations = Collections.unmodifiableList(annotations);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public @Nullable Object get(Object bean) {
            if (getterInvoker == null) {
                throw new FsBeanException("Property is not readable: " + name + ".");
            }
            return getterInvoker.invoke(bean);
        }

        @Override
        public void set(Object bean, @Nullable Object value) {
            if (setterInvoker == null) {
                throw new FsBeanException("Property is not writeable: " + name + ".");
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
            return field;
        }

        @Override
        public List<Annotation> getGetterAnnotations() {
            return getterAnnotations;
        }

        @Override
        public List<Annotation> getSetterAnnotations() {
            return setterAnnotations;
        }

        @Override
        public List<Annotation> getFieldAnnotations() {
            return fieldAnnotations;
        }

        @Override
        public List<Annotation> getAnnotations() {
            return allAnnotations;
        }

        @Override
        public FsBean getOwner() {
            return owner;
        }

        @Override
        public boolean isReadable() {
            return getterInvoker != null;
        }

        @Override
        public boolean isWriteable() {
            return setterInvoker != null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DefaultFsBeanPropertyImpl)) {
                return false;
            }
            DefaultFsBeanPropertyImpl other = (DefaultFsBeanPropertyImpl) o;
            return Objects.equals(owner.getType(), other.owner.getType())
                && Objects.equals(name, other.name)
                && Objects.equals(type, other.type);
        }

        @Override
        protected int computeHashCode() {
            return Objects.hash(owner.getType(), name, type);
        }

        @Override
        protected String computeToString() {
            return "BeanProperty(name=" +
                name +
                ", type=" +
                type +
                ", ownerType=" +
                owner.getType() +
                ")";
        }
    }
}
