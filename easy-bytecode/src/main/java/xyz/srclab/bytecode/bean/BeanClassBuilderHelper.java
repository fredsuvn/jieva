package xyz.srclab.bytecode.bean;

import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.provider.ByteCodeProviderManager;

class BeanClassBuilderHelper {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.getInstance().getProvider();

    static <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newBeanClassBuilder(superClass);
    }
}
