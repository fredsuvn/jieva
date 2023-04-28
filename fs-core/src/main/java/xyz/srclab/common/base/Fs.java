package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Common utilities, collected from
 * {@link FsObject}, {@link FsCheck}, {@link FsString} and etc..
 *
 * @author fredsuvn
 */
public class Fs {

    // Methods from FsArray:

    /**
     * Returns given arguments as an array.
     *
     * @param args given arguments
     */
    @SafeVarargs
    public static <T> T[] array(T... args) {
        return FsArray.array(args);
    }

    /**
     * Maps source array of type T[] to dest array of typeR[].
     * If the dest array's length equals to source array, the mapped elements will be put into the dest array,
     * else create and put into a new array.
     *
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @param source the source array
     * @param dest   the dest array
     * @param mapper given mapper
     */
    public static <T, R> R[] mapArray(T[] source, R[] dest, Function<T, R> mapper) {
        return FsArray.map(source, dest, mapper);
    }

    /**
     * Creates a new array of given component type and length.
     *
     * @param componentType given component type
     * @param length        given length
     */
    public static <T> T[] newArray(Class<?> componentType, int length) {
        return FsArray.newArray(componentType, length);
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return null.
     *
     * @param array given array
     * @param index specified index
     */
    public static <T> T get(@Nullable T[] array, int index) {
        return FsArray.get(array, index);
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     */
    public static <T> T get(@Nullable T[] array, int index, @Nullable T defaultValue) {
        return FsArray.get(array, index, defaultValue);
    }

    // Methods from FsCheck:

    /**
     * Checks whether given object is null, if it is, throw a {@link NullPointerException}.
     *
     * @param obj given object
     */
    public static void checkNull(@Nullable Object obj) throws NullPointerException {
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
     * Checks whether given obj is null, if it is, throw a {@link NullPointerException} with message
     * concatenated from given message arguments.
     *
     * @param obj         given object
     * @param messageArgs given message arguments
     */
    public static void checkNull(@Nullable Object obj, Object... messageArgs) throws NullPointerException {
        FsCheck.checkNull(obj, messageArgs);
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

    // Methods from FsObject:

    /**
     * Returns default value if given object is null, or given object itself if it is not null.
     *
     * @param obj          given object
     * @param defaultValue default value
     */
    public static <T> T notNull(@Nullable T obj, T defaultValue) {
        return FsObject.notNull(obj, defaultValue);
    }

    /**
     * Returns hash code follows:
     * <ul>
     * <li>returns Objects.hashCode for given object if it is not an array;</li>
     * <li>if given object is primitive array, returns Arrays.hashCode for it;</li>
     * <li>if given object is Object[], returns Arrays.deepHashCode for it;</li>
     * <li>else returns Objects.hashCode for given object</li>
     * </ul>
     * <p>
     * This method is same as: hash(obj, true, true)
     *
     * @param obj given object
     */
    public static int hash(@Nullable Object obj) {
        return FsObject.hash(obj);
    }

    /**
     * Returns deep-hash-code for given objects.
     *
     * @param objs given objects
     */
    public static int hash(Object... objs) {
        return FsObject.hash(objs);
    }

    /**
     * Returns hash code follows:
     * <ul>
     * <li>if given object is primitive array and array-check is true, returns Arrays.hashCode for it;</li>
     * <li>if given object is Object[] and both array-check and deep-to-string are true,
     * returns Arrays.deepHashCode for it;</li>
     * <li>if given object is Object[] and array-check is true and deep-to-string is false,
     * returns Arrays.hashCode for it;</li>
     * <li>else returns Objects.hashCode for given object</li>
     * </ul>
     *
     * @param obj        given object
     * @param arrayCheck the array-check
     * @param deepHash   whether deep-hash
     */
    public static int hash(@Nullable Object obj, boolean arrayCheck, boolean deepHash) {
        return FsObject.hash(obj, arrayCheck, deepHash);
    }

    /**
     * Returns identity hash code for given object, same as {@link System#identityHashCode(Object)}.
     *
     * @param obj given object
     */
    public static int systemHash(@Nullable Object obj) {
        return FsObject.systemHash(obj);
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     * <li>returns Objects.equals for given objects if they are not arrays;</li>
     * <li>if given objects are arrays of which types are same primitive type, returns Arrays.equals for them;</li>
     * <li>if given objects are object array, returns Arrays.deepEquals for them;</li>
     * <li>else returns Objects.equals for given objects</li>
     * </ul>
     * <p>
     * This method is same as: equals(a, b, true, true)
     *
     * @param a given object a
     * @param b given object b
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return FsObject.equals(a, b);
    }

    /**
     * Returns deep-equals for given objects.
     *
     * @param objs given objects
     */
    public static boolean equals(Object... objs) {
        return FsObject.equals(objs);
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     * <li>if given objects are arrays of which types are same primitive type and array-check is true,
     * returns Arrays.equals for them;</li>
     * <li>if given objects are object array and both array-check and deep-equals are true,
     * returns Arrays.deepEquals for them;</li>
     * <li>if given objects are object array and array-check is true and deep-equals is false,
     * returns Arrays.equals for them;</li>
     * <li>else returns Objects.equals for given objects</li>
     * </ul>
     *
     * @param a          given object a
     * @param b          given object b
     * @param arrayCheck the array-check
     * @param deepEquals whether deep-equals
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b, boolean arrayCheck, boolean deepEquals) {
        return FsObject.equals(a, b, arrayCheck, deepEquals);
    }

    // Methods from FsString:

    /**
     * Returns a string follows:
     * <ul>
     * <li>returns String.valueOf for given object if it is not an array;</li>
     * <li>if given object is primitive array, returns Arrays.toString for it;</li>
     * <li>if given object is Object[], returns Arrays.deepToString for it;</li>
     * <li>else returns String.valueOf for given object</li>
     * </ul>
     * <p>
     * This method is same as: toString(obj, true, true)
     *
     * @param obj given object
     */
    public static String toString(@Nullable Object obj) {
        return FsString.toString(obj);
    }

    /**
     * Returns deep-array-to-string for given objects.
     *
     * @param objs given objects
     */
    public static String toString(Object... objs) {
        return FsString.toString(objs);
    }

    /**
     * Returns a string follows:
     * <ul>
     * <li>if given object is primitive array and array-check is true, returns Arrays.toString for it;</li>
     * <li>if given object is Object[] and both array-check and deep-to-string are true,
     * returns Arrays.deepToString for it;</li>
     * <li>if given object is Object[] and array-check is true and deep-to-string is false,
     * returns Arrays.toString for it;</li>
     * <li>else returns String.valueOf for given object</li>
     * </ul>
     *
     * @param obj          given object
     * @param arrayCheck   the array-check
     * @param deepToString whether deep-to-string
     */
    public static String toString(@Nullable Object obj, boolean arrayCheck, boolean deepToString) {
        return FsString.toString(obj, arrayCheck, deepToString);
    }

    /**
     * Concatenates given strings.
     *
     * @param strings given strings
     */
    public static String concat(String... strings) {
        return FsString.concat(strings);
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Object... args) {
        return FsString.concat(args);
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Iterable<?> args) {
        return FsString.concat(args);
    }

    /**
     * Joins given strings with given separator.
     *
     * @param separator given separator
     * @param strings   given strings
     */
    public static String join(String separator, String... strings) {
        return FsString.join(separator, strings);
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Object... args) {
        return FsString.join(separator, args);
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Iterable<?> args) {
        return FsString.join(separator, args);
    }

    /**
     * Returns an object which is lazy for executing method {@link Object#toString()},
     * the executing was provided by given supplier.
     * <p>
     * Note returned {@link CharSequence}'s other methods (such as {@link CharSequence#length()})
     * were based on its toString() (and the toString() is lazy).
     *
     * @param supplier given supplier
     */
    public static CharSequence lazyString(Supplier<CharSequence> supplier) {
        return FsString.lazyString(supplier);
    }

    // Methods from FsCollect:

    /**
     * Puts all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     */
    @SafeVarargs
    public static <T, C extends Collection<? super T>> C toCollection(C dest, T... elements) {
        return FsCollect.toCollection(dest, elements);
    }

    /**
     * Puts all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     */
    public static <T, C extends Collection<? super T>> C toCollection(C dest, Iterable<T> elements) {
        return FsCollect.toCollection(dest, elements);
    }

    /**
     * Puts all given key-values into dest map, and returns the dest map.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param dest      dest collection
     * @param keyValues given key-values
     */
    public static <K, V, M extends Map<? super K, ? super V>> M toMap(M dest, Object... keyValues) {
        return FsCollect.toMap(dest, keyValues);
    }

    /**
     * Returns an array contains all elements from given iterable in its order.
     *
     * @param iterable given iterable
     * @param type     array's component type
     */
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        return FsCollect.toArray(iterable, type);
    }

    /**
     * Creates an {@link ArrayList} and put all given elements into it.
     *
     * @param elements given elements
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return FsCollect.arrayList(elements);
    }

    /**
     * Creates an {@link LinkedList} and put all given elements into it.
     *
     * @param elements given elements
     */
    @SafeVarargs
    public static <T> LinkedList<T> linkedList(T... elements) {
        return FsCollect.linkedList(elements);
    }

    /**
     * Creates an {@link HashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> HashMap<K, V> hashMap(Object... keyValues) {
        return FsCollect.hashMap(keyValues);
    }

    /**
     * Creates an {@link LinkedHashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Object... keyValues) {
        return FsCollect.linkedHashMap(keyValues);
    }

    /**
     * Creates an {@link ConcurrentHashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(Object... keyValues) {
        return FsCollect.concurrentHashMap(keyValues);
    }

    /**
     * Returns whether given iterable is null or empty.
     *
     * @param iterable given iterable
     */
    public static boolean isEmpty(@Nullable Iterable<?> iterable) {
        return FsCollect.isEmpty(iterable);
    }

    /**
     * Returns whether given map is null or empty.
     *
     * @param map given map
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return FsCollect.isEmpty(map);
    }

    /**
     * Returns value from given iterable at specified index, if failed to obtain, return null.
     *
     * @param iterable given iterable
     * @param index    specified index
     */
    public static <T> T get(@Nullable Iterable<T> iterable, int index) {
        return FsCollect.get(iterable, index);
    }

    /**
     * Returns value from given iterable at specified index, if failed to obtain, return default value.
     *
     * @param iterable     given iterable
     * @param index        specified index
     * @param defaultValue default value
     */
    public static <T> T get(@Nullable Iterable<T> iterable, int index, @Nullable T defaultValue) {
        return FsCollect.get(iterable, index, defaultValue);
    }

    /**
     * Returns value from given map at specified key, if failed to obtain, return null.
     *
     * @param map given map
     * @param key specified key
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key) {
        return FsCollect.get(map, key);
    }

    /**
     * Returns value from given map at specified key, if failed to obtain, return default value.
     *
     * @param map          given map
     * @param key          specified key
     * @param defaultValue default value
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key, @Nullable V defaultValue) {
        return FsCollect.get(map, key, defaultValue);
    }
}
