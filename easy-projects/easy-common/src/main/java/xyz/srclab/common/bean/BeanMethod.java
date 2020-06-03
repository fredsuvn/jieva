package xyz.srclab.common.bean;

import xyz.srclab.annotation.Hide;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Hide
@Immutable
public interface BeanMethod extends BeanMember {

    int parameterCount();

    Class<?>[] parameterTypes();

    Type[] genericParameterTypes();

    Class<?> returnType();

    Type genericReturnType();

    Method method();

    MethodInvoker methodInvoker();

    @Nullable
    default Object invoke(Object bean, Object... args) {
        return methodInvoker().invoke(bean, args);
    }
}
