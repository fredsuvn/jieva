package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

/**
 * Thread utilities.
 *
 * @author fredsuvn
 */
public class FsThread {

    /**
     * Returns caller stack trace element of given class name and method name, or null if failed.
     * This method is equivalent to {@link #findStackTraceCaller(String, String, int)}:
     * <pre>
     *     findStackTraceCaller(className, methodName, 0);
     * </pre>
     * <p>
     * Note return element is <b>caller of given method</b>, not given method.
     *
     * @param className  given class name
     * @param methodName given method name
     */
    @Nullable
    public static StackTraceElement findStackTraceCaller(String className, String methodName) {
        return findStackTraceCaller(className, methodName, 0);
    }

    /**
     * Returns caller stack trace element of given class name and method name, or null if failed.
     * <p>
     * This method search the result of calling of {@link Thread#getStackTrace()} of current thread.
     * It first finds the {@link StackTraceElement} which class name and method name are match given names,
     * then obtain the index of next element (not matched element), then add given offset into the index.
     * Finally return the element at index after adding.
     * <p>
     * If stack trace element is null or empty, or the final index is out of bound, return null.
     *
     * @param className  given class name
     * @param methodName given method name
     * @param offset     given offset
     */
    @Nullable
    public static StackTraceElement findStackTraceCaller(String className, String methodName, int offset) {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (FsArray.isEmpty(stackTraces)) {
            return null;
        }
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTraceElement = stackTraces[i];
            if (Fs.equals(stackTraceElement.getClassName(), className)
                && Fs.equals(stackTraceElement.getMethodName(), methodName)) {
                int targetIndex = i + 1 + offset;
                if (FsCheck.isInBounds(targetIndex, 0, stackTraces.length)) {
                    return stackTraces[targetIndex];
                }
                return null;
            }
        }
        return null;
    }
}
