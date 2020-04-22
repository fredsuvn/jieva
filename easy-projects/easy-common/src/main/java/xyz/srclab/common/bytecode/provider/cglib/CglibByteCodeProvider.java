package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.common.bytecode.BeanClass;
import xyz.srclab.common.bytecode.EnhancedClass;
import xyz.srclab.common.bytecode.InvokerClass;
import xyz.srclab.common.bytecode.provider.ByteCodeProvider;

/**
 * @author sunqian
 */
public class CglibByteCodeProvider implements ByteCodeProvider {

    public static final CglibByteCodeProvider INSTANCE = new CglibByteCodeProvider();

    @Override
    public <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return new BeanClassBuilderImpl<>(superClass);
    }

    @Override
    public <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass) {
        return new EnhancedClassBuilderImpl<>(superClass);
    }

    @Override
    public <T> InvokerClass<T> getInvokerClass(Class<T> type) {
        return InvokerClassImpl.getInvokerClassImpl(type);
    }
}
