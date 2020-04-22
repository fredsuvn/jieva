package xyz.srclab.common.bytecode.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.bytecode.BeanClass;
import xyz.srclab.common.bytecode.EnhancedClass;
import xyz.srclab.common.bytecode.InvokerClass;

@Immutable
public interface ByteCodeProvider {

    <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass);

    <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass);

    <T> InvokerClass<T> getInvokerClass(Class<T> type);
}
