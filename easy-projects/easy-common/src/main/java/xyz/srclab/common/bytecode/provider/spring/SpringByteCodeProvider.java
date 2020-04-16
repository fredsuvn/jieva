package xyz.srclab.common.bytecode.provider.spring;

import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.bytecode.invoke.InvokerClass;
import xyz.srclab.common.bytecode.provider.ByteCodeProvider;

/**
 * @author sunqian
 */
public class SpringByteCodeProvider implements ByteCodeProvider {

    public static final SpringByteCodeProvider INSTANCE = new SpringByteCodeProvider();

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
