package xyz.srclab.bytecode.bean;

import xyz.srclab.bytecode.provider.ByteCodeProvider;
import xyz.srclab.bytecode.provider.ByteCodeProviderManagement;

class BeanClassBuilderHelper {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManagement.getInstance().getProvider();

    static <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newBeanClassBuilder(superClass);
    }
}
