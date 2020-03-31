package xyz.srclab.bytecode.provider.cglib;

import xyz.srclab.common.base.EnvironmentHelper;
import xyz.srclab.bytecode.provider.cglib.original.OriginalCglibAdaptor;
import xyz.srclab.bytecode.provider.cglib.spring.SpringCglibAdaptor;

class CglibEnvironmentHelper {

    private static final CglibAdaptor INSTANCE;

    static {
        INSTANCE = EnvironmentHelper.hasPackage("org.springframework.cglib.proxy") ?
                new SpringCglibAdaptor() : new OriginalCglibAdaptor();
    }

    static CglibAdaptor findCglibAdaptor() {
        return INSTANCE;
    }
}
