package xyz.srclab.common.reflect;

public class ClassLoaderHelper {

    private static final BytesClassLoader classLoader = new BytesClassLoader();

    public static Class<?> loadFromBytes(byte[] bytes) {
        return classLoader.loadFromBytes(bytes);
    }

    private static final class BytesClassLoader extends ClassLoader {

        public Class<?> loadFromBytes(byte[] bytes) {
            return super.defineClass(null, bytes, 0, bytes.length);
        }
    }
}
