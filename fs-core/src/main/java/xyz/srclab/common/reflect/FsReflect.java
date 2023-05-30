package xyz.srclab.common.reflect;

import xyz.srclab.common.base.FsString;

/**
 * Reflection utilities.
 *
 * @author fredsuvn
 */
public class FsReflect {

    /**
     * Returns last name of given class. The last name is sub-string after last dot, for example:
     * <p>
     * {@code String} is last name of {@code java.lang.String}.
     *
     * @param cls given class
     */
    public static String getLastName(Class<?> cls) {
        String name = cls.getName();
        int index = FsString.lastIndexOf(name, ".");
        if (index < 0 || index >= name.length() - 1) {
            return name;
        }
        return name.substring(index + 1);
    }
}
