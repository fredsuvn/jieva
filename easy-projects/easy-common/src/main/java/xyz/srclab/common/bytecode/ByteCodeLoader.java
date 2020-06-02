package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public class ByteCodeLoader {

    public static Class<?> loadClass(byte[] byteCode) {
        return ClassLoaderHelper.loadFromBytes(byteCode);
    }
}
