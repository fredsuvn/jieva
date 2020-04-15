package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.bytecode.provider.ByteCodeProvider;

@ThreadSafe
public class CglibByteCodeProvider implements ByteCodeProvider {

    public static final CglibByteCodeProvider INSTANCE = new CglibByteCodeProvider();

    private final OriginalCglibAdaptor originalCglibAdaptor = new OriginalCglibAdaptor();

    @Override
    public <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return new CglibBeanClassBuilder<>(originalCglibAdaptor, superClass);
    }

    @Override
    public <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass) {
        return new CglibProxyClassBuilder<>(originalCglibAdaptor, superClass);
    }
}
