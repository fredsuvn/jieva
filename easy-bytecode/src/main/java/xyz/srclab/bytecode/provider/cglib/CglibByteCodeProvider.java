package xyz.srclab.bytecode.provider.cglib;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.bytecode.bean.BeanClass;
import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.proxy.ProxyClass;

@ThreadSafe
public class CglibByteCodeProvider implements ByteCodeProvider {

    public static CglibByteCodeProvider getInstance() {
        return CglibByteCodeProvider.INSTANCE;
    }

    private static final CglibByteCodeProvider INSTANCE = new CglibByteCodeProvider();

    private final OriginalCglibAdaptor originalCglibAdaptor = new OriginalCglibAdaptor();

    @Override
    public <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return new CglibBeanClassBuilder<>(originalCglibAdaptor, superClass);
    }

    @Override
    public <T> ProxyClass.Builder<T> newProxyClassBuilder(Class<T> superClass) {
        return new CglibProxyClassBuilder<>(originalCglibAdaptor, superClass);
    }
}
