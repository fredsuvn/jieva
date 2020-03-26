package xyz.srclab.common.bytecode.provider.cglib;

public interface CglibAdaptor {

    static CglibAdaptor getInstance() {
        return CglibEnvironmentHelper.findCglibAdaptor();
    }

    Enhancer newEnhancer();

    BeanGenerator newBeanGenerator();
}
