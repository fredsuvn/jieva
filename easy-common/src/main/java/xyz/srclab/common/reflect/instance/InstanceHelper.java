package xyz.srclab.common.reflect.instance;

public class InstanceHelper {

    public static <T> T newInstance(Class<?> cls) {
        try {
            return (T) cls.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
