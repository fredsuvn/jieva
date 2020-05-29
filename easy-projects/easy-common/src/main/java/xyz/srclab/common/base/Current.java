package xyz.srclab.common.base;

/**
 * @author sunqian
 */
public class Current {

    public static long mills() {
        return System.currentTimeMillis();
    }

    public static long nanos() {
        return System.nanoTime();
    }

    public static Thread thread() {
        return Thread.currentThread();
    }
}
