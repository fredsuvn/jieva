package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.common.bytecode.bean.BeanClass;
import xyz.srclab.common.bytecode.bean.BeanClassBuilderProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManagement;
import xyz.srclab.common.bytecode.proxy.ProxyClass;
import xyz.srclab.common.bytecode.proxy.ProxyClassBuilderProvider;

public class CglibByteCodeProviderManagement implements ByteCodeProviderManagement {

    public static CglibByteCodeProviderManagement getInstance() {
        return CglibByteCodeProviderManagement.INSTANCE;
    }

    private static final CglibByteCodeProviderManagement INSTANCE = new CglibByteCodeProviderManagement();

    @Override
    public BeanClassBuilderProvider getBeanClassBuilderProvider() {
        return BeanClassBuilderProviderImpl.getInstance();
    }

    @Override
    public ProxyClassBuilderProvider getProxyClassBuilderProvider() {
        return ProxyClassBuilderProviderImpl.getInstance();
    }

    private static final class BeanClassBuilderProviderImpl implements BeanClassBuilderProvider {

        static BeanClassBuilderProviderImpl getInstance() {
            return BeanClassBuilderProviderImpl.INSTANCE;
        }

        private static final BeanClassBuilderProviderImpl INSTANCE = new BeanClassBuilderProviderImpl();

        @Override
        public <T> BeanClass.Builder<T> newBuilder(Class<T> superClass) {
            return CglibBeanClassBuilder.newBuilder(superClass);
        }
    }

    private static final class ProxyClassBuilderProviderImpl implements ProxyClassBuilderProvider {

        static ProxyClassBuilderProviderImpl getInstance() {
            return ProxyClassBuilderProviderImpl.INSTANCE;
        }

        private static final ProxyClassBuilderProviderImpl INSTANCE = new ProxyClassBuilderProviderImpl();

        @Override
        public <T> ProxyClass.Builder<T> newBuilder(Class<T> superClass) {
            return CglibProxyClassBuilder.newBuilder(superClass);
        }
    }
}
