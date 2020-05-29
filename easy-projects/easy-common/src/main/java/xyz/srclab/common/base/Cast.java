package xyz.srclab.common.base;

/**
 * @author sunqian
 */
public class Cast {

    public static <T, R> R as(T any) throws ClassCastException {
        return (R) any;
    }
}
