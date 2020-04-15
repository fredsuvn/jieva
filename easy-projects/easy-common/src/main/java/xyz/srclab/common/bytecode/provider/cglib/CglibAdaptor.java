package xyz.srclab.common.bytecode.provider.cglib;

interface CglibAdaptor {

    Enhancer newEnhancer();

    BeanGenerator newBeanGenerator();
}
