package xyz.srclab.common.bytecode.spring;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.asm.Type;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.proxy.*;
import xyz.srclab.common.bytecode.ClassBuilder;
import xyz.srclab.common.bytecode.ClassConstructor;
import xyz.srclab.common.bytecode.MethodBody;
import xyz.srclab.common.bytecode.MethodInvoker;
import xyz.srclab.common.collection.CollectionHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.ReflectHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class SpringClassBuilder<T> implements ClassBuilder<T> {

    private final Class<T> superClass;
    private final List<Class<?>> interfaces = new LinkedList<>();
    private final Map<Object, MethodInfo> methodBodyInfoMap = new HashMap<>();

    public SpringClassBuilder(Class<T> superClass) {
        this.superClass = superClass;
    }

    @Override
    public ClassBuilder<T> setInterfaces(Iterable<Class<?>> interfaces) {
        this.interfaces.addAll(CollectionHelper.castCollection(interfaces));
        return this;
    }

    @Override
    public ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, MethodBody<Void> methodBody) {
        methodBodyInfoMap.put(
                buildMethodKey(name, parameterTypes),
                new MethodInfo(name, parameterTypes, null, methodBody)
        );
        return this;
    }

    @Override
    public <R> ClassBuilder<T> addMethod(
            String name, Class<?>[] parameterTypes, Class<R> returnType, MethodBody<R> methodBody) {
        methodBodyInfoMap.put(
                buildMethodKey(name, parameterTypes),
                new MethodInfo(name, parameterTypes, returnType, methodBody)
        );
        return this;
    }

    private Object buildMethodKey(String name, Class<?>[] parameterTypes) {
        return name + "(" + StringUtils.join(parameterTypes, ", ") + ")";
    }

    @Override
    public ClassConstructor<T> build() {
        return new ClassConstructorImpl<>(buildEnhancer());
    }

    private Enhancer buildEnhancer() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(superClass);

        Map<Object, MethodInfo> shouldGenerate = new HashMap<>(methodBodyInfoMap);
        List<Class<?>> classes = new LinkedList<>();
        classes.add(superClass);
        classes.addAll(interfaces);
        List<Method> methods = ReflectHelper.getAllMethods(classes);
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) ||
                    !(Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))) {
                continue;
            }
            Object key = buildMethodKey(method.getName(), method.getParameterTypes());
            MethodInfo methodInfo = methodBodyInfoMap.get(key);
            if (methodInfo != null) {
                shouldGenerate.remove(key);
            }
        }
        InterfaceMaker interfaceMaker = new InterfaceMaker();
        for (MethodInfo methodInfo : shouldGenerate.values()) {
            interfaceMaker.add(
                    new Signature(
                            methodInfo.getName(),
                            methodInfo.getReturnType() == null ?
                                    Type.VOID_TYPE : Type.getType(methodInfo.getReturnType()),
                            classesToTypes(methodInfo.getParameterTypes())
                    ), new Type[0]);
        }
        Class<?> interfaceClass = interfaceMaker.create();
        interfaces.add(interfaceClass);
        if (!interfaces.isEmpty()) {
            enhancer.setInterfaces(interfaces.toArray(ArrayUtils.EMPTY_CLASS_ARRAY));
        }

        overrideMethod(enhancer, methodBodyInfoMap.values().toArray(new MethodInfo[0]));

        return enhancer;
    }

    private Type[] classesToTypes(Class<?>[] classes) {
        Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++) {
            types[i] = Type.getType(classes[i]);
        }
        return types;
    }

    private void overrideMethod(Enhancer enhancer, MethodInfo[] methodInfos) {
        List<Callback> callbacks = new LinkedList<>();
        callbacks.add(NoOp.INSTANCE);
        for (MethodInfo methodInfo : methodInfos) {
            callbacks.add((MethodInterceptor) (object, method, args, proxy) ->
                    methodInfo.getMethodBody().invoke(object, method, args, new MethodInvoker() {
                        @Override
                        public Object invoke() {
                            try {
                                return proxy.invokeSuper(object, args);
                            } catch (Throwable throwable) {
                                throw new ExceptionWrapper(throwable);
                            }
                        }

                        @Override
                        public Object invoke(Object[] args) {
                            try {
                                return proxy.invokeSuper(object, args);
                            } catch (Throwable throwable) {
                                throw new ExceptionWrapper(throwable);
                            }
                        }
                    }));
        }
        Callback[] callbackArray = callbacks.toArray(new Callback[0]);
        CallbackFilter callbackFilter = new CallbackFilter() {
            @Override
            public int accept(Method method) {
                for (int i = 0; i < methodInfos.length; i++) {
                    MethodInfo methodInfo = methodInfos[i];
                    if (method.getName().equals(methodInfo.getName())
                            && Arrays.deepEquals(method.getParameterTypes(), methodInfo.getParameterTypes())) {
                        return i + 1;
                    }
                }
                return 0;
            }
        };
        enhancer.setCallbacks(callbackArray);
        enhancer.setCallbackFilter(callbackFilter);
    }

    private static class ClassConstructorImpl<T> implements ClassConstructor<T> {

        private final Enhancer enhancer;

        private ClassConstructorImpl(Enhancer enhancer) {
            this.enhancer = enhancer;
        }

        @Override
        public T create() {
            return (T) enhancer.create();
        }

        @Override
        public T create(Class<?>[] parameterTypes, Object[] args) {
            return (T) enhancer.create(parameterTypes, args);
        }
    }

    private static class MethodInfo {
        private final String name;
        private final Class<?>[] parameterTypes;
        @Nullable
        private final Class<?> returnType;
        private final MethodBody<?> methodBody;

        private MethodInfo(
                String name,
                Class<?>[] parameterTypes,
                @Nullable Class<?> returnType,
                MethodBody<?> methodBody
        ) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.methodBody = methodBody;
        }

        public String getName() {
            return name;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        @Nullable
        public Class<?> getReturnType() {
            return returnType;
        }

        public MethodBody<?> getMethodBody() {
            return methodBody;
        }
    }
}
