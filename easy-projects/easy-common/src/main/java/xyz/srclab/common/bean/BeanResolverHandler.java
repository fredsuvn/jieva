package xyz.srclab.common.bean;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Immutable
public interface BeanResolverHandler {

    static BeanResolverHandler getFieldHandler() {
        return BeanResolver0.getFieldHandler();
    }

    static BeanResolverHandler getGetterSetterHandler() {
        return BeanResolver0.getGetterSetterHandler();
    }

    static BeanResolverHandler getNamingMethodHandler() {
        return BeanResolver0.getNamingMethodHandler();
    }

    void resolve(Class<?> beanClass, @Out Context context);

    interface Context {

        @Immutable
        List<Field> fields();

        @Immutable
        List<Method> methods();

        Map<String, BeanProperty> properties();

        void nonsupport();

        void terminate();
    }
}
