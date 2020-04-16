package xyz.srclab.common.bytecode.invoke;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;

class InvokerClassSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManager.INSTANCE.getProvider();

    static <T> InvokerClass<T> getInvokerClass(Class<T> type) {
        return byteCodeProvider.getInvokerClass(type);
    }
}
