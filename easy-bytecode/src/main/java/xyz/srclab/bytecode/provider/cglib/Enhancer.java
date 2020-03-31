package xyz.srclab.bytecode.provider.cglib;

public interface Enhancer {

    void setSuperclass(Class<?> superclass);

    void setInterfaces(Class<?>[] interfaces);

    void setCallbacks(Callback[] callbacks);

    void setCallbackFilter(CallbackFilter callbackFilter);

    Object create();

    Object create(Class<?>[] parameterTypes, Object[] args);
}
