package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.bytecode.provider.ByteCodeProvider;

@ThreadSafe
public class SpringCglibByteCodeProvider implements ByteCodeProvider {

    public static final SpringCglibByteCodeProvider INSTANCE = new SpringCglibByteCodeProvider();

    private final SpringCglibAdaptor springCglibAdaptor = new SpringCglibAdaptor();

    @Override
    public <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return new CglibBeanClassBuilder<>(springCglibAdaptor, superClass);
    }

    @Override
    public <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass) {
        return new CglibProxyClassBuilder<>(springCglibAdaptor, superClass);
    }
}
