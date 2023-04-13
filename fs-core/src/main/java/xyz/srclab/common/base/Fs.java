package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCheck.Dd;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Common utilities, collected from
 * {@link FsObject}, {@link FsCheck}, {@link FsString} and etc..
 *
 * @author fredsuvn
 */
public class Fs {

    // Methods from FsCheck:

    /**
     * Checks whether given object is null, if it is, throw a {@link NullPointerException}.
     *
     * @param obj given object
     */
    public static void zz2(@Nullable Object obj) throws NullPointerException {
        FsCheck.checkNull(obj);
    }

    /**
     * Checks whether given obj is null, if it is, throw a {@link NullPointerException} with given message.
     *
     * @param obj     given object
     * @param message given message
     */
    public static void checkNull(@Nullable Object obj, CharSequence message) throws NullPointerException {
        FsCheck.checkNull(obj, message);
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NullPointerException}.
     *
     * @param expr given expression
     */
    public static void checkNull(boolean expr) throws NullPointerException {
        FsCheck.checkNull(expr);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw a {@link NullPointerException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkNull(boolean expr, CharSequence message) throws NullPointerException {
        FsCheck.checkNull(expr, message);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw a {@link NullPointerException} with message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkNull(boolean expr, Object... messageArgs) throws NullPointerException {
        FsCheck.checkNull(expr, messageArgs);
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalArgumentException}.
     *
     * @param expr given expression
     */
    public static void checkArgument(boolean expr) throws IllegalArgumentException {
        FsCheck.checkArgument(expr);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link IllegalArgumentException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkArgument(boolean expr, CharSequence message) throws IllegalArgumentException {
        FsCheck.checkArgument(expr, message);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link IllegalArgumentException} with given message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkArgument(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        FsCheck.checkArgument(expr, messageArgs);
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link IllegalStateException}.
     *
     * @param expr given expression
     */
    public static void checkState(boolean expr) throws IllegalStateException {
        FsCheck.checkState(expr);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link IllegalStateException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkState(boolean expr, CharSequence message) throws IllegalStateException {
        FsCheck.checkState(expr, message);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link IllegalStateException} with given message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkState(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        FsCheck.checkState(expr, messageArgs);
    }

    /**
     * Checks whether given expression is true, if it is not, throw an {@link UnsupportedOperationException}.
     *
     * @param expr given expression
     */
    public static void checkSupported(boolean expr) throws UnsupportedOperationException {
        FsCheck.checkSupported(expr);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link UnsupportedOperationException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkSupported(boolean expr, CharSequence message) throws UnsupportedOperationException {
        FsCheck.checkSupported(expr, message);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw an {@link UnsupportedOperationException} with given message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkSupported(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        FsCheck.checkSupported(expr, messageArgs);
    }

    /**
     * Checks whether given expression is true, if it is not, throw a {@link NoSuchElementException}.
     *
     * @param expr given expression
     */
    public static void checkElement(boolean expr) throws NoSuchElementException {
        FsCheck.checkElement(expr);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw a {@link NoSuchElementException} with given message.
     *
     * @param expr    given expression
     * @param message given message
     */
    public static void checkElement(boolean expr, CharSequence message) throws NoSuchElementException {
        FsCheck.checkElement(expr, message);
    }

    /**
     * Checks whether given expression is true, if it is not,
     * throw a {@link IllegalArgumentException} with given message concatenated from given message arguments.
     *
     * @param expr        given expression
     * @param messageArgs given message arguments
     */
    public static void checkElement(boolean expr, Object... messageArgs) throws IllegalArgumentException {
        FsCheck.checkElement(expr, messageArgs);
    }

    /**
     * Returns whether given index is in bounds from start index (inclusive) to end index (exclusive).
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static boolean isInBounds(int index, int startIndex, int endIndex) {
        return FsCheck.isInBounds(index, startIndex, endIndex);
    }

    /**
     * Returns whether given index is in bounds from start index (inclusive) to end index (exclusive).
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static boolean isInBounds(long index, long startIndex, long endIndex) {
        return FsCheck.isInBounds(index, startIndex, endIndex);
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive),
     * if it is not, throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): index.
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkInBounds(int index, int startIndex, int endIndex) throws IndexOutOfBoundsException {
        FsCheck.checkInBounds(index, startIndex, endIndex);
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive),
     * if it is not, throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkInBounds(int index, int startIndex, int endIndex, CharSequence message) throws IndexOutOfBoundsException {
        FsCheck.checkInBounds(index, startIndex, endIndex, message);
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive),
     * if it is not, throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): index.
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkInBounds(long index, long startIndex, long endIndex) throws IndexOutOfBoundsException {
        FsCheck.checkInBounds(index, startIndex, endIndex);
    }

    /**
     * Checks whether given index is in bounds from start index (inclusive) to end index (exclusive),
     * if it is not, throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all indexed must >= 0;
     *
     * @param index      given index
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkInBounds(long index, long startIndex, long endIndex, CharSequence message) throws IndexOutOfBoundsException {
        FsCheck.checkInBounds(index, startIndex, endIndex, message);
    }

    /**
     * Returns whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive).
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static boolean isRangeInBounds(int startRange, int endRange, int startIndex, int endIndex) {
        return FsCheck.isRangeInBounds(startRange, endRange, startIndex, endIndex);
    }

    /**
     * Returns whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive).
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static boolean isRangeInBounds(long startRange, long endRange, long startIndex, long endIndex) {
        return FsCheck.isRangeInBounds(startRange, endRange, startIndex, endIndex);
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): [startRange, endRange).
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkRangeInBounds(int startRange, int endRange, int startIndex, int endIndex) throws IndexOutOfBoundsException {
        FsCheck.checkRangeInBounds(startRange, endRange, startIndex, endIndex);
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkRangeInBounds(int startRange, int endRange, int startIndex, int endIndex, CharSequence message) throws IndexOutOfBoundsException {
        FsCheck.checkRangeInBounds(startRange, endRange, startIndex, endIndex, message);
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with message pattern: [startIndex, endIndex): [startRange, endRange).
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     */
    public static void checkRangeInBounds(long startRange, long endRange, long startIndex, long endIndex) throws IndexOutOfBoundsException {
        FsCheck.checkRangeInBounds(startRange, endRange, startIndex, endIndex);
    }

    /**
     * Checks whether given range (from start range index inclusive to end range index exclusive) is in bounds from
     * start index (inclusive) to end index (exclusive), if it is not,
     * throw an {@link IndexOutOfBoundsException} with given message.
     * <p>
     * Note all ranges and indexed must >= 0;
     *
     * @param startRange start range index inclusive
     * @param endRange   end range index exclusive
     * @param startIndex start index (inclusive)
     * @param endIndex   end index (exclusive)
     * @param message    given message
     */
    public static void checkRangeInBounds(long startRange, long endRange, long startIndex, long endIndex, CharSequence message) throws IndexOutOfBoundsException {
        FsCheck.checkRangeInBounds(startRange, endRange, startIndex, endIndex, message);
    }

    public static <T, @Nullable R, U extends @Nullable String> void checkNull2(@Nullable @ThreadSafe Object obj, @Nullable T t, @Nullable @ThreadSafe List<? extends @Nullable U> lu, @Nullable R r) throws NullPointerException {
        FsCheck.checkNull2(obj, t, lu, r);
    }

    public static Dd ss() {
        return FsCheck.ss();
    }

    public static String ss2() {
        return FsCheck.ss2();
    }
}
