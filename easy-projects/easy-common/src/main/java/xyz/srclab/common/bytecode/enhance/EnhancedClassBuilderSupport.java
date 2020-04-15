package xyz.srclab.common.bytecode.enhance;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;

class EnhancedClassBuilderSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

    static <T> EnhancedClass.Builder<T> newBuilder(Class<T> superClass) {
        return byteCodeProvider.newEnhancedClassBuilder(superClass);
    }
}
