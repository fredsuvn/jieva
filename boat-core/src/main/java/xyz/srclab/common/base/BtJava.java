package xyz.srclab.common.base;

/**
 * Java utilities.
 */
public class BtJava {

    /**
     * Gets enum of given class with enum name.
     *
     * @param cls  given class
     * @param name enum name
     * @return enum of given class with enum name
     */
    public static Object getEnum(Class<?> cls, String name) {
        return Enum.valueOf((Class<Enum>) cls, name);
    }
}
