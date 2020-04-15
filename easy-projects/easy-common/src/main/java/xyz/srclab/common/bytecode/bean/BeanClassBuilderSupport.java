package xyz.srclab.common.bytecode.bean;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;

class BeanClassBuilderSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

    static <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newBeanClassBuilder(superClass);
    }
}
