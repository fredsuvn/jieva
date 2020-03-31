package xyz.srclab.bytecode.provider;

import xyz.srclab.bytecode.bean.BeanClassBuilderProvider;
import xyz.srclab.bytecode.proxy.ProxyClassBuilderProvider;

public interface ByteCodeProvider {

    BeanClassBuilderProvider getBeanClassBuilderProvider();

    ProxyClassBuilderProvider getProxyClassBuilderProvider();
}
