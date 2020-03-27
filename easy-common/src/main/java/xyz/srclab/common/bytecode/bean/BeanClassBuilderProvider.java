package xyz.srclab.common.bytecode.bean;

import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagement;

public interface BeanClassBuilderProvider {

    static BeanClassBuilderProvider getInstance() {
        return ByteCodeProviderManagement.getInstance().getProvider().getBeanClassBuilderProvider();
    }

    <T> BeanClass.Builder<T> newBuilder(Class<T> superClass);
}
