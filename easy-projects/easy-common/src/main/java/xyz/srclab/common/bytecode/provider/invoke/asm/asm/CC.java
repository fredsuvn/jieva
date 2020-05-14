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
        s2(args[0], args[1], args[2]);
        return new String(args[0].toString());
    }

    public void s(Object... args) {
        System.out.println(args[0]);
        System.out.println(args[1]);
        System.out.println(args[2]);
    }

    public void s2(Object o1, Object o2, Object o3) {
        System.out.println(o1);
        System.out.println(o2);
        System.out.println(o3);
    }
}
