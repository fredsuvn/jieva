package xyz.srclab.common.reflect.type;

/**
 * @author sunqian
 */
public class ClassLoaderHelper {

    private static final ClassLoaderSupport classLoaderSupport = ClassLoaderSupport.INSTANCE;

    public static Class<?> loadClass(byte[] byteCode) {
        return classLoaderSupport.loadClass(byteCode);
    }

    private static final class ClassLoaderSupport extends ClassLoader {

        static final ClassLoaderSupport INSTANCE = new ClassLoaderSupport();

        Class<?> loadClass(byte[] byteCode) {
            return super.defineClass(null, byteCode, 0, byteCode.length);
        }
    }
}
