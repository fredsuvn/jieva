package xyz.srclab.common.bytecode;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;

final class ByteCodeSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

    static <T> BeanClass.Builder<T> newBeanClassBuilder(Class<T> superClass) {
        return byteCodeProvider.newBeanClassBuilder(superClass);
    }

    static <T> EnhancedClass.Builder<T> newEnhancedClassBuilder(Class<T> superClass) {
        return byteCodeProvider.newEnhancedClassBuilder(superClass);
    }

    static <T> InvokerClass<T> getInvokerClass(Class<T> type) {
        return byteCodeProvider.getInvokerClass(type);
    }
}
