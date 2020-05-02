package xyz.srclab.common.bytecode;

/**
 * @author sunqian
 */
public class ByteCodeLoader {

    private static final ByteCodeClassLoader loader = ByteCodeClassLoader.INSTANCE;

    public static Class<?> loadClass(byte[] byteCode) {
        return loader.loadByteCode(byteCode);
    }

    private static final class ByteCodeClassLoader extends ClassLoader {

        static final ByteCodeClassLoader INSTANCE = new ByteCodeClassLoader();

        public Class<?> loadByteCode(byte[] byteCode) {
            return super.defineClass(null, byteCode, 0, byteCode.length);
        }
    }
}
