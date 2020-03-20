package xyz.srclab.common.bytecode;

public interface MethodInvoker {

    Object invoke();

    Object invoke(Object[] args);
}
