package xyz.srclab.bytecode.provider.cglib;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.bytecode.bean.BeanClass;
import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.proxy.ProxyClass;

@ThreadSafe
public class SpringCglibByteCodeProvider implements ByteCodeProvider {

    public static final SpringCglibByteCodeProvider INSTANCE = new SpringCglibByteCodeProvider();

    private final SpringCglibAdaptor springCglibAdaptor = new SpringCglibAdaptor();

    @Override
    public <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return new CglibBeanClassBuilder<>(springCglibAdaptor, superClass);
    }

    @Override
    public <T> ProxyClass.Builder<T> newProxyClassBuilder(Class<T> superClass) {
        return new CglibProxyClassBuilder<>(springCglibAdaptor, superClass);
    }
}
