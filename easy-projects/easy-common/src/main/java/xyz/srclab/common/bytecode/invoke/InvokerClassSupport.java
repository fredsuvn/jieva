package xyz.srclab.common.bytecode.invoke;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagerBase;

class InvokerClassSupport {

    private static final ByteCodeProvider byteCodeProvider = ByteCodeProviderManagerBase.INSTANCE.getProvider();

    static <T> InvokerClass<T> getInvokerClass(Class<T> type) {
        return byteCodeProvider.getInvokerClass(type);
    }
}
