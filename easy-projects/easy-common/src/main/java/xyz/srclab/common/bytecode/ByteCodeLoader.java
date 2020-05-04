package xyz.srclab.common.bytecode;

import xyz.srclab.common.reflect.ClassLoaderHelper;

/**
 * @author sunqian
 */
public class ByteCodeLoader {

    public static Class<?> loadClass(byte[] byteCode) {
        return ClassLoaderHelper.loadFromBytes(byteCode);
    }
}
