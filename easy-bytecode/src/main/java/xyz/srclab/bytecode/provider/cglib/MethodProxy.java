package xyz.srclab.bytecode.provider.cglib;

public interface MethodProxy {

    Object invoke(Object object, Object[] args) throws Throwable;

    Object invokeSuper(Object object, Object[] args) throws Throwable;
}
