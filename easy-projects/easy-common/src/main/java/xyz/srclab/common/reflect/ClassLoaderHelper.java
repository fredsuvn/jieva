package xyz.srclab.common.reflect;

public class ClassLoaderHelper {

    private static final BytesClassLoader classLoader = BytesClassLoader.INSTANCE;

    public static Class<?> loadFromBytes(byte[] bytes) {
        return classLoader.loadFromBytes(bytes);
    }

    private static final class BytesClassLoader extends ClassLoader {

        private static final BytesClassLoader INSTANCE = new BytesClassLoader();

        private BytesClassLoader() {
        }

        public Class<?> loadFromBytes(byte[] bytes) {
            return super.defineClass(null, bytes, 0, bytes.length);
        }
    }
}
