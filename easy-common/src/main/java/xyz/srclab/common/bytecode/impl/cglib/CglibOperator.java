package xyz.srclab.common.bytecode.impl.cglib;

public interface CglibOperator {

    static CglibOperator getInstance() {
        return CglibOperatorLoader.getInstance();
    }

    Enhancer newEnhancer();

    BeanGenerator newBeanGenerator();
}
