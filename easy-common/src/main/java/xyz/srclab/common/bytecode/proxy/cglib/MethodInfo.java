package xyz.srclab.common.bytecode.proxy.cglib;

import xyz.srclab.common.reflect.MethodBody;

class MethodInfo {

    private final String name;
    private final Class<?>[] parameterTypes;
    private final MethodBody<?> body;

    MethodInfo(String name, Class<?>[] parameterTypes, MethodBody<?> body) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public MethodBody<?> getBody() {
        return body;
    }
}
