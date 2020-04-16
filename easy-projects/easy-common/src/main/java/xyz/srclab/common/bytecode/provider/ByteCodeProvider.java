package xyz.srclab.common.bytecode.provider;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.bytecode.invoke.InvokerClass;

@Immutable
public interface ByteCodeProvider {

    <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass);

    <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass);

    <T> InvokerClass<T> getInvokerClass(Class<T> type);
}
