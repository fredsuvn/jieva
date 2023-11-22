package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.GekArray;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utilities for trace operation.
 *
 * @author fredsuvn
 */
public class GekTrace {

    /**
     * Returns caller stack trace of given class name and method name, or null if failed.
     * This method is equivalent to {@link #findCallerTrace(String, String, int)}:
     * <pre>
     *     findCallerStackTrace(className, methodName, 0)
     * </pre>
     * <p>
     * This method searches the result of {@link Thread#getStackTrace()} of current thread,
     * to find first {@link StackTraceElement} of which class name and method name are match given names.
     * Let the next of found element be the {@code caller}, the {@code caller} will be returned.
     * <p>
     * If stack trace element is null or empty, or the final index is out of bound, return null.
     *
     * @param className  given class name
     * @param methodName given method name
     * @return caller stack trace
     */
    @Nullable
    public static StackTraceElement findCallerTrace(String className, String methodName) {
        return findCallerTrace(className, methodName, 0);
    }

    /**
     * Returns caller stack trace of given class name and method name, or null if failed.
     * <p>
     * This method searches the result of {@link Thread#getStackTrace()} of current thread,
     * to find first {@link StackTraceElement} of which class name and method name are match given names.
     * Let the next of found element be the {@code caller},
     * if given {@code offset} is 0, the {@code caller} will be returned.
     * Otherwise, the element at index of {@code (caller's index + offset)} will be returned.
     * <p>
     * If stack trace element is null or empty, or the final index is out of bound, return null.
     *
     * @param className  given class name
     * @param methodName given method name
     * @param offset     given offset
     * @return caller stack trace
     */
    @Nullable
    public static StackTraceElement findCallerTrace(String className, String methodName, int offset) {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        if (GekArray.isEmpty(stackTraces)) {
            return null;
        }
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTraceElement = stackTraces[i];
            if (Gek.equals(stackTraceElement.getClassName(), className)
                && Gek.equals(stackTraceElement.getMethodName(), methodName)) {
                int targetIndex = i + 1 + offset;
                if (GekCheck.isInBounds(targetIndex, 0, stackTraces.length)) {
                    return stackTraces[targetIndex];
                }
                return null;
            }
        }
        return null;
    }

    /**
     * Returns stack trace info of given throwable as string.
     *
     * @param throwable given throwable
     * @return stack trace info as string
     */
    public static String toString(Throwable throwable) {
        return toString(throwable, null);
    }

    /**
     * Returns stack trace info of given throwable as string,
     * the line separator of return string will be replaced by given line separator if given separator is not null.
     *
     * @param throwable     given throwable
     * @param lineSeparator given separator
     * @return stack trace info as string
     */
    public static String toString(Throwable throwable, @Nullable String lineSeparator) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        String stackTrace = sw.toString();
        if (lineSeparator == null) {
            return stackTrace;
        }
        String sysLineSeparator = System.lineSeparator();
        return stackTrace.replaceAll(sysLineSeparator, lineSeparator);
    }
}
