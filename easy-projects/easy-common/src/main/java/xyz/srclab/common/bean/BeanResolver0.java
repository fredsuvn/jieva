package xyz.srclab.common.bean;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author sunqian
 */
final class BeanResolver0 {

    static BeanResolver getDefault() {
        return DefaultResolverHolder.SINGLETON;
    }

    static BeanResolverBuilder newResolverBuilder() {
        return new BeanResolverBuilder();
    }

    static BeanResolverHandler getFieldHandler() {
        return FieldHandler.SINGLETON;
    }

    static BeanResolverHandler getGetterSetterHandler() {
        return GetterSetterHandler.SINGLETON;
    }

    static BeanResolverHandler getNamingMethodHandler() {
        return NamingMethodHandler.SINGLETON;
    }

    private static final class DefaultResolverHolder {

        private static final BeanResolver SINGLETON = newResolverBuilder()
                .handler(getGetterSetterHandler())
                .build();
    }

    private static final class FieldHandler implements BeanResolverHandler {

        private static final FieldHandler SINGLETON = new FieldHandler();

        private FieldHandler() {
        }

        @Override
        public void resolve(Class<?> beanClass, Context context) {
            Map<String, BeanProperty> properties = context.properties();
            for (Field field : context.fields()) {
                if (properties.containsKey(field.getName())) {
                    continue;
                }
                properties.put(field.getName(), BeanProperty.newBeanPropertyOnField(beanClass, field));
            }
        }
    }

    private static abstract class MethodHandler implements BeanResolverHandler {

        @Override
        public void resolve(Class<?> beanClass, Context context) {

            final class PropertyBuilder {

                private final String name;
                private @Nullable Method readMethod;
                private @Nullable Method writeMethod;

                private PropertyBuilder(String name) {
                    this.name = name;
                }

                public String getName() {
                    return name;
                }

                @Nullable
                public Method getReadMethod() {
                    return readMethod;
                }

                public void setReadMethod(Method readMethod) {
                    this.readMethod = readMethod;
                }

                @Nullable
                public Method getWriteMethod() {
                    return writeMethod;
                }

                public void setWriteMethod(Method writeMethod) {
                    this.writeMethod = writeMethod;
                }

                public BeanProperty build() {
                    return BeanProperty.newBeanPropertyOnMethods(beanClass, name, readMethod, writeMethod);
                }
            }

            Map<String, BeanProperty> properties = context.properties();
            Set<PropertyOnMethod> getters = new LinkedHashSet<>();
            Set<PropertyOnMethod> setters = new LinkedHashSet<>();
            List<Method> methods = context.methods();

            sortGetterSetter(methods, getters, setters);
            Map<String, PropertyBuilder> builderMap = new LinkedHashMap<>();

            getters.forEach(propertyOnMethod -> {
                PropertyBuilder builder = builderMap.computeIfAbsent(
                        propertyOnMethod.getName(),
                        PropertyBuilder::new);
                builder.setReadMethod(propertyOnMethod.getMethod());
            });

            setters.forEach(propertyOnMethod -> {
                PropertyBuilder builder = builderMap.computeIfAbsent(
                        propertyOnMethod.getName(),
                        PropertyBuilder::new);
                if (builder.getWriteMethod() != null) {
                    return;
                }
                Method writeMethod = propertyOnMethod.getMethod();
                @Nullable Method readMethod = builder.getReadMethod();
                if (readMethod == null) {
                    builder.setWriteMethod(writeMethod);
                    return;
                }
                if (readMethod.getGenericReturnType().equals(writeMethod.getGenericParameterTypes()[0])) {
                    builder.setWriteMethod(writeMethod);
                }
            });

            builderMap.forEach((name, builder) -> properties.put(name, builder.build()));
        }

        protected abstract void sortGetterSetter(
                List<Method> methods, @Out Set<PropertyOnMethod> getters, @Out Set<PropertyOnMethod> setters);

        private static final class PropertyOnMethod {

            private final String name;
            private final Method method;

            private PropertyOnMethod(String name, Method method) {
                this.name = name;
                this.method = method;
            }

            public String getName() {
                return name;
            }

            public Method getMethod() {
                return method;
            }
        }
    }

    private static final class GetterSetterHandler extends MethodHandler {

        private static final GetterSetterHandler SINGLETON = new GetterSetterHandler();

        private GetterSetterHandler() {
        }

        @Override
        protected void sortGetterSetter(
                List<Method> methods, @Out Set<PropertyOnMethod> getters, @Out Set<PropertyOnMethod> setters) {
            for (Method method : methods) {
                String name = method.getName();
                if (method.getParameterCount() == 0) {
                    if (name.startsWith("get") && name.length() > 3) {
                        getters.add(new PropertyOnMethod(StringUtils.uncapitalize(name.substring(3)), method));
                        continue;
                    }
                    if (name.startsWith("is") && name.length() > 2) {
                        getters.add(new PropertyOnMethod(StringUtils.uncapitalize(name.substring(2)), method));
                        continue;
                    }
                }
                if (method.getParameterCount() == 1) {
                    if (name.startsWith("set") && name.length() > 3) {
                        getters.add(new PropertyOnMethod(StringUtils.uncapitalize(name.substring(3)), method));
                    }
                }
            }
        }
    }

    private static final class NamingMethodHandler extends MethodHandler {

        private static final NamingMethodHandler SINGLETON = new NamingMethodHandler();

        private NamingMethodHandler() {
        }

        @Override
        protected void sortGetterSetter(
                List<Method> methods, @Out Set<PropertyOnMethod> getters, @Out Set<PropertyOnMethod> setters) {
            for (Method method : methods) {
                if (method.getParameterCount() == 0) {
                    getters.add(new PropertyOnMethod(method.getName(), method));
                    continue;
                }
                if (method.getParameterCount() == 1) {
                    getters.add(new PropertyOnMethod(method.getName(), method));
                }
            }
        }
    }
}
