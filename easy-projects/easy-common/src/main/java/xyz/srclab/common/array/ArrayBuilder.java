package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;

import java.lang.reflect.Array;
import java.util.function.IntFunction;

/**
 * @author sunqian
 */
public class ArrayBuilder {

    public static ArrayBuilder newBuilder() {
        return new ArrayBuilder();
    }

    private @Nullable Class<?> componentType;
    private int length;
    private @Nullable IntFunction<?> elementFunction;

    private ArrayBuilder() {
    }

    public ArrayBuilder setComponentType(Class<?> componentType) {
        this.componentType = componentType;
        return this;
    }

    public ArrayBuilder setLength(int length) {
        Checker.checkArguments(length >= 0, "Length must >= 0");
        this.length = length;
        return this;
    }

    public ArrayBuilder setElementFunction(IntFunction<?> elementFunction) {
        this.elementFunction = elementFunction;
        return this;
    }

    public <E> E[] build() {
        if (componentType == null) {
            componentType = Object.class;
        }
        E[] result = (E[]) Array.newInstance(componentType, length);
        if (elementFunction != null) {
            for (int i = 0; i < result.length; i++) {
                result[i] = (E) elementFunction.apply(i);
            }
        }
        return result;
    }
}
