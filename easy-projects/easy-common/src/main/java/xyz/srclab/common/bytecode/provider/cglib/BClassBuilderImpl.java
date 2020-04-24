package xyz.srclab.common.bytecode.provider.cglib;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.proxy.Enhancer;
import xyz.srclab.common.bytecode.BClass;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.util.pattern.builder.CachedBuilder;
import xyz.srclab.common.reflect.signature.SignatureHelper;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
final class BClassBuilderImpl<T> extends CachedBuilder<BClass<T>> implements BClass.Builder<T> {

    private final Class<?> baseType;
    private final List<Class<?>> interfaces = new LinkedList<>();
    private final Map<String, BConstructor<T>> constructorMap = new LinkedHashMap<>();
    private final Map<String, BMethod> methodMap = new LinkedHashMap<>();

    private final Map<String, BProperty> propertyMap = new LinkedHashMap<>();
    private boolean propertiesReadWrite = true;

    BClassBuilderImpl(Class<?> baseType) {
        this.baseType = baseType;
    }

    @Override
    public BClass.Builder<T> addInterface(Class<?> interfaceClass) {
        this.interfaces.add(interfaceClass);
        this.updateState();
        return this;
    }

    @Override
    public BClass.Builder<T> addInterfaces(Iterable<Class<?>> interfaces) {
        this.interfaces.addAll(IterableHelper.asCollection(interfaces));
        this.updateState();
        return this;
    }

    @Override
    public BClass.Builder<T> addConstructor(Class<?>[] parameterTypes, BClass.ConstructorBody<T> constructorBody) {
        this.constructorMap.put(
                SignatureHelper.signConstructor(getClass(), parameterTypes),
                new BConstructor<>(parameterTypes.clone(), constructorBody)
        );
        this.updateState();
        return this;
    }

    @Override
    public BClass.Builder<T> addProperty(String propertyName, Class<?> type, boolean readable, boolean writeable) {
        this.propertyMap.put(propertyName, new BProperty(propertyName, type, readable, writeable));
        if (!readable || !writeable) {
            propertiesReadWrite = false;
        }
        this.updateState();
        return this;
    }

    @Override
    public BClass.Builder<T> addMethod(
            String methodName, Class<?>[] parameterTypes, BClass.MethodBody methodBody) {
        this.methodMap.put(
                SignatureHelper.signMethod(methodName, parameterTypes),
                new BMethod(methodName, parameterTypes, methodBody)
        );
        this.updateState();
        return this;
    }

    @Override
    protected BClass<T> buildNew() {
        if (propertiesReadWrite && constructorMap.isEmpty() && methodMap.isEmpty()) {
            return new BeanClass<>(propertyMap);
        }

        Enhancer enhancer = new Enhancer();
        return null;
    }

    private static final class BeanClass<T> implements BClass<T> {

        private final BeanGenerator beanGenerator;

        private BeanClass(Map<String, BProperty> propertyMap) {
            this.beanGenerator = new BeanGenerator();
            propertyMap.forEach((k, p)->{
                beanGenerator.addProperty(p.getName(), p.getType());
            });
        }

        @Override
        public T newInstance() {
            return (T) beanGenerator.create();
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] args) {
            return newInstance();
        }
    }

    private static final class BConstructor<T> {
        private final Class<?>[] parameterTypes;
        private final BClass.ConstructorBody<T> body;

        private BConstructor(Class<?>[] parameterTypes, BClass.ConstructorBody<T> body) {
            this.parameterTypes = parameterTypes;
            this.body = body;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public BClass.ConstructorBody<T> getBody() {
            return body;
        }
    }

    private static final class BProperty {
        private final String name;
        private final Class<?> type;
        private final boolean readable;
        private final boolean writeable;

        private BProperty(String name, Class<?> type, boolean readable, boolean writeable) {
            this.name = name;
            this.type = type;
            this.readable = readable;
            this.writeable = writeable;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public boolean isReadable() {
            return readable;
        }

        public boolean isWriteable() {
            return writeable;
        }
    }

    private static final class BMethod {
        private final String name;
        private final Class<?>[] parameterTypes;
        private final BClass.MethodBody body;

        private BMethod(String name, Class<?>[] parameterTypes, BClass.MethodBody body) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.body = body;
        }

        public String getName() {
            return name;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public BClass.MethodBody getBody() {
            return body;
        }
    }
}
