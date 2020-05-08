package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;

/**
 * @author sunqian
 */
public class CC implements ConstructorInvoker<String> {

    private final Constructor<String> constructor;

    public CC(Constructor<String> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Constructor<String> getConstructor() {
        return constructor;
    }

    @Override
    public String invoke(Object... args) {
        return new String();
    }

}
