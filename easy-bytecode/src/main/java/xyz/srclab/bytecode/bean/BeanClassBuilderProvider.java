package xyz.srclab.bytecode.bean;

import xyz.srclab.bytecode.provider.ByteCodeProviderManagement;

public interface BeanClassBuilderProvider {

    static BeanClassBuilderProvider getInstance() {
        return ByteCodeProviderManagement.getInstance().getProvider().getBeanClassBuilderProvider();
    }

    <T> BeanClass.Builder<T> newBuilder(Class<T> superClass);
}
