package xyz.srclab.bytecode.provider.cglib;

public interface CglibAdaptor {

    static CglibAdaptor getInstance() {
        return CglibEnvironmentHelper.findCglibAdaptor();
    }

    Enhancer newEnhancer();

    BeanGenerator newBeanGenerator();
}
