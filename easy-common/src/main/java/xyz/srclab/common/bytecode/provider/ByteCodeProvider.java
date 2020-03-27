package xyz.srclab.common.bytecode.provider;

import xyz.srclab.common.bytecode.bean.BeanClassBuilderProvider;
import xyz.srclab.common.bytecode.proxy.ProxyClassBuilderProvider;

public interface ByteCodeProvider {

    BeanClassBuilderProvider getBeanClassBuilderProvider();

    ProxyClassBuilderProvider getProxyClassBuilderProvider();
}
