package xyz.srclab.common.bean;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                properties.put(field.getName(), BeanClass0.newBeanPropertyOnField(beanClass, field));
            }
        }
    }

    private static abstract class MethodHandler implements BeanResolverHandler {

        @Override
        public void resolve(Class<?> beanClass, Context context) {
            Map<String, BeanProperty> properties = context.properties();
            Map<String, Method> getters = new LinkedHashMap<>();
            Map<String, Method> setters = new LinkedHashMap<>();
            List<Method> methods = context.methods();
            sortGetterSetter(methods, getters, setters);
            getters.forEach((name, getter) -> {
                if (properties.containsKey(name)) {
                    return;
                }
                @Nullable Method setter = setters.get(name);
                if (setter == null) {
                    properties.put(name, BeanClass0.newBeanPropertyOnMethods(
                            beanClass,
                            name,
                            getter,
                            null
                    ));
                    return;
                }
                if (!getter.getReturnType().equals(setter.getParameterTypes()[0])) {
                    return;
                }
                properties.put(name, BeanClass0.newBeanPropertyOnMethods(
                        beanClass,
                        name,
                        getter,
                        setter
                ));
                setters.remove(name);
            });
            setters.forEach((name, setter) -> {
                if (properties.containsKey(name)) {
                    return;
                }
                properties.put(name, BeanClass0.newBeanPropertyOnMethods(
                        beanClass,
                        name,
                        null,
                        setter
                ));
            });
        }

        protected abstract void sortGetterSetter(
                List<Method> methods, @Out Map<String, Method> getters, @Out Map<String, Method> setters);
    }

    private static final class GetterSetterHandler extends MethodHandler {

        private static final GetterSetterHandler SINGLETON = new GetterSetterHandler();

        private GetterSetterHandler() {
        }

        @Override
        protected void sortGetterSetter(
                List<Method> methods, Map<String, Method> getters, Map<String, Method> setters) {
            for (Method method : methods) {
                String name = method.getName();
                if (method.getParameterCount() == 0) {
                    if (name.startsWith("get") && name.length() > 3) {
                        getters.put(StringUtils.uncapitalize(name.substring(3)), method);
                        continue;
                    }
                    if (name.startsWith("is") && name.length() > 2) {
                        getters.put(StringUtils.uncapitalize(name.substring(2)), method);
                        continue;
                    }
                }
                if (method.getParameterCount() == 1) {
                    if (name.startsWith("set") && name.length() > 3) {
                        setters.put(StringUtils.uncapitalize(name.substring(3)), method);
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
                List<Method> methods, Map<String, Method> getters, Map<String, Method> setters) {
            for (Method method : methods) {
                String name = method.getName();
                if (method.getParameterCount() == 0) {
                    getters.put(StringUtils.uncapitalize(name), method);
                    continue;
                }
                if (method.getParameterCount() == 1) {
                    setters.put(StringUtils.uncapitalize(name), method);
                }
            }
        }
    }
}
