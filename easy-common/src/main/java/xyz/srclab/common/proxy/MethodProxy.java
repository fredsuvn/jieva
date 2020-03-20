package xyz.srclab.common.proxy;

public interface MethodProxy {

    Object invoke();

    Object invoke(Object[] args);
}
