package xyz.srclab.bytecode.provider.cglib;

interface CglibAdaptor {

    Enhancer newEnhancer();

    BeanGenerator newBeanGenerator();
}
