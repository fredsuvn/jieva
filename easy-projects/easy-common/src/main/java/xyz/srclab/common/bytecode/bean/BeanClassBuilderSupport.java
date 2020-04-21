package xyz.srclab.common.bytecode.bean;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagerBase;

class BeanClassBuilderSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManagerBase.INSTANCE.getProvider();

    static <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newBeanClassBuilder(superClass);
    }
}
