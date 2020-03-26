package xyz.srclab.common.bytecode.provider;

import xyz.srclab.common.bytecode.bean.BeanClassBuilderProvider;
import xyz.srclab.common.bytecode.provider.cglib.CglibByteCodeProviderManagement;
import xyz.srclab.common.bytecode.proxy.ProxyClassBuilderProvider;

public interface ByteCodeProviderManagement {

    static ByteCodeProviderManagement getInstance() {
        return CglibByteCodeProviderManagement.getInstance();
    }

    BeanClassBuilderProvider getBeanClassBuilderProvider();

    ProxyClassBuilderProvider getProxyClassBuilderProvider();
}
