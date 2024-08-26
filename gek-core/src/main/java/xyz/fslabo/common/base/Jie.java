package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.coll.CollBuilder;
import xyz.fslabo.common.coll.JieArray;
import xyz.fslabo.common.coll.JieColl;
import xyz.fslabo.common.mapping.BeanMapper;
import xyz.fslabo.common.mapping.Mapper;
import xyz.fslabo.common.mapping.MappingOptions;
import xyz.fslabo.common.reflect.TypeRef;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utilities for object and common/base operations.
 *
 * @author fredsuvn
 */
public class Jie {

    /**
     * Casts given object as specified type T.
     *
     * @param obj given object
     * @param <T> specified type T
     * @return given obj as specified type T
     */
    public static <T> T as(@Nullable Object obj) {
        return (T) obj;
    }

    /**
     * Returns default value if given object is null, or given object itself if it is not null:
     * <pre>
     *     return obj == null ? defaultValue : obj;
     * </pre>
     *
     * @param obj          given object
     * @param defaultValue default value
     * @param <T>          type of values
     * @return default value if given object is null, or given object itself if it is not null
     */
    public static <T> T orDefault(@Nullable T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    /**
     * Returns computed value if given object is null, or given object itself if it is not null:
     * <pre>
     *     return obj == null ? computedValue.get() : obj;
     * </pre>
     *
     * @param obj           given object
     * @param computedValue computed value
     * @param <T>           type of values
     * @return computed value if given object is null, or given object itself if it is not null
     */
    public static <T> T orDefault(@Nullable T obj, Supplier<? extends T> computedValue) {
        return obj == null ? computedValue.get() : obj;
    }

    /**
     * Returns default value if given object is null, or the value computed by given function if it is not null:
     * <pre>
     *     return obj == null ? defaultValue : function.apply(obj);
     * </pre>
     *
     * @param obj          given object
     * @param defaultValue default value
     * @param function     given function
     * @param <K>          type of given object
     * @param <V>          type of result
     * @return default value if given object is null, or the value computed by given function if it is not null
     */
    public static <K, V> V orDefault(@Nullable K obj, V defaultValue, Function<? super K, ? extends V> function) {
        return obj == null ? defaultValue : function.apply(obj);
    }

    /**
     * Returns the value computed by given function if it is not null, or null if it is null:
     * <pre>
     *     return obj == null ? null : function.apply(obj);
     * </pre>
     *
     * @param obj      given object
     * @param function given function
     * @param <K>      type of given object
     * @param <V>      type of result
     * @return the value computed by given function if it is not null, or null if it is null
     */
    @Nullable
    public static <K, V> V orNull(@Nullable K obj, Function<? super K, ? extends V> function) {
        return obj == null ? null : function.apply(obj);
    }

    /**
     * Returns hash code follows:
     * <ul>
     *     <li>
     *         returns {@code Objects.hashCode} for given object if it is not an array;
     *     </li>
     *     <li>
     *         if given object is primitive array, returns {@code Arrays.hashCode} for it;
     *     </li>
     *     <li>
     *         if given object is Object[], returns {@code Arrays.deepHashCode} for it;
     *     </li>
     *     <li>
     *         else returns {@code Objects.hashCode} for given object;
     *     </li>
     * </ul>
     * This method is equivalent to: {@code hash(obj, true, true)}.
     *
     * @param obj given object
     * @return the hash code
     */
    public static int hash(@Nullable Object obj) {
        return hashWith(obj, true, true);
    }

    /**
     * Returns deep-hash-code for given objects.
     *
     * @param objs given objects
     * @return the hash code
     */
    public static int hash(Object... objs) {
        return Arrays.deepHashCode(objs);
    }

    /**
     * Returns hash code follows:
     * <ul>
     *     <li>
     *         if given object is primitive array and {@code arrayCheck} is {@code true}, returns
     *         {@code Arrays.hashCode} for it;
     *     </li>
     *     <li>
     *         if given object is Object[] and both {@code arrayCheck} and {@code deepHash} are {@code true},
     *         returns {@code Arrays.deepHashCode} for it;
     *     </li>
     *     <li>
     *         if given object is Object[] and {@code arrayCheck} is {@code true} and {@code deepHash} is {@code false},
     *         returns {@code Arrays.hashCode} for it;
     *     </li>
     *     <li>
     *         else returns {@code Objects.hashCode} for given object;
     *     </li>
     * </ul>
     *
     * @param obj        given object
     * @param arrayCheck the array-check
     * @param deepHash   whether deep-hash
     * @return the hash code
     */
    public static int hashWith(@Nullable Object obj, boolean arrayCheck, boolean deepHash) {
        if (obj == null || !arrayCheck) {
            return Objects.hashCode(obj);
        }
        if (obj instanceof Object[]) {
            return deepHash ? Arrays.deepHashCode((Object[]) obj) : Arrays.hashCode((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.hashCode((byte[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.hashCode((short[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.hashCode((char[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.hashCode((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.hashCode((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.hashCode((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.hashCode((double[]) obj);
        }
        return obj.hashCode();
    }

    /**
     * Returns identity hash code for given object, same as {@link System#identityHashCode(Object)}.
     *
     * @param obj given object
     * @return the system hash code
     */
    public static int systemHash(@Nullable Object obj) {
        return System.identityHashCode(obj);
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     *     <li>
     *         returns {@code Objects.equals} for given objects if they are not arrays;
     *     </li>
     *     <li>
     *         if given objects are arrays of which types are same primitive type, returns {@code Arrays.equals} for them;
     *     </li>
     *     <li>
     *         if given objects are object array, returns {@code Arrays.deepEquals} for them;
     *     </li>
     *     <li>
     *         else returns {@code Objects.equals} for given objects;
     *     </li>
     * </ul>
     * This method is same as: {@code equals(a, b, true, true)}.
     *
     * @param a given object a
     * @param b given object b
     * @return the result of equaling
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return equalsWith(a, b, true, true);
    }

    /**
     * Returns whether given objects are equals each other by {@link #equals(Object, Object)}. It is equivalent to:
     * <pre>
     *     for (int i = 0; i < objs.length - 2; i++) {
     *         if (!equals(objs[i], objs[i + 1])) {
     *             return false;
     *         }
     *     }
     *     return true;
     * </pre>
     *
     * @param objs given objects
     * @return the result of equaling
     */
    public static boolean equals(Object... objs) {
        if (objs.length <= 1) {
            return true;
        }
        if (objs.length == 2) {
            return equals(objs[0], objs[1]);
        }
        for (int i = 0; i < objs.length - 1; i++) {
            if (!equals(objs[i], objs[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     *     <li>
     *         if given objects are arrays of which types are same primitive type and {@code arrayCheck} is {@code true},
     *         returns {@code Arrays.equals} for them;
     *     </li>
     *     <li>if given objects are object array and both {@code arrayCheck} and {@code deepEquals} are {@code true},
     *     returns {@code Arrays.deepEquals} for them;
     *     </li>
     *     <li>
     *         if given objects are object array and {@code arrayCheck} is {@code true} and {@code deepEquals} is
     *         {@code false}, returns {@code Arrays.equals} for them;
     *     </li>
     *     <li>
     *         else returns {@code Objects.equals} for given objects,
     *     </li>
     * </ul>
     *
     * @param a          given object a
     * @param b          given object b
     * @param arrayCheck the array-check
     * @param deepEquals whether deep-equals
     * @return the result of equaling
     */
    public static boolean equalsWith(@Nullable Object a, @Nullable Object b, boolean arrayCheck, boolean deepEquals) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!arrayCheck) {
            return Objects.equals(a, b);
        }
        Class<?> typeA = a.getClass();
        Class<?> typeB = b.getClass();
        if (typeA.isArray() && typeB.isArray()) {
            if (a instanceof Object[] && b instanceof Object[]) {
                return deepEquals ? Arrays.deepEquals((Object[]) a, (Object[]) b) : Arrays.equals((Object[]) a, (Object[]) b);
            }
            if (a instanceof boolean[] && b instanceof boolean[]) {
                return Arrays.equals((boolean[]) a, (boolean[]) b);
            }
            if (a instanceof byte[] && b instanceof byte[]) {
                return Arrays.equals((byte[]) a, (byte[]) b);
            }
            if (a instanceof short[] && b instanceof short[]) {
                return Arrays.equals((short[]) a, (short[]) b);
            }
            if (a instanceof char[] && b instanceof char[]) {
                return Arrays.equals((char[]) a, (char[]) b);
            }
            if (a instanceof int[] && b instanceof int[]) {
                return Arrays.equals((int[]) a, (int[]) b);
            }
            if (a instanceof long[] && b instanceof long[]) {
                return Arrays.equals((long[]) a, (long[]) b);
            }
            if (a instanceof float[] && b instanceof float[]) {
                return Arrays.equals((float[]) a, (float[]) b);
            }
            if (a instanceof double[] && b instanceof double[]) {
                return Arrays.equals((double[]) a, (double[]) b);
            }
        }
        return Objects.equals(a, b);
    }

    /**
     * Returns enum object of specified name from given enum class, may be null if not found.
     *
     * @param enumClass given enum class
     * @param name      specified name
     * @param <T>       type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, String name) {
        return findEnum(enumClass, name, false);
    }

    /**
     * Returns enum object of specified name from given enum class, may be null if not found.
     *
     * @param enumClass  given enum class
     * @param name       specified name
     * @param ignoreCase whether ignore case for specified name
     * @param <T>        type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, String name, boolean ignoreCase) {
        JieCheck.checkArgument(enumClass.isEnum(), "Not an enum class.");
        if (!ignoreCase) {
            try {
                return Enum.valueOf(as(enumClass), name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        Object[] enums = enumClass.getEnumConstants();
        if (JieArray.isEmpty(enums)) {
            return null;
        }
        for (Object anEnum : enums) {
            if (name.equalsIgnoreCase(anEnum.toString())) {
                return as(anEnum);
            }
        }
        return null;
    }

    /**
     * Returns enum object at specified index from given enum class, may be null if not found.
     *
     * @param enumClass given enum class
     * @param index     specified index
     * @param <T>       type of enum
     * @return the enum object or null
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, int index) {
        JieCheck.checkArgument(enumClass.isEnum(), enumClass + " is not an enum.");
        JieCheck.checkArgument(index >= 0, "index must >= 0.");
        Object[] enums = enumClass.getEnumConstants();
        if (JieArray.isEmpty(enums) || index >= enums.length) {
            return null;
        }
        return as(enums[index]);
    }

    /**
     * Finds resource of given resource path (starts with "/").
     *
     * @param resPath given resource
     * @return url of resource of given resource path
     */
    public static URL findRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(JieString.removeStart(resPath, "/"));
    }

    /**
     * Finds all resources of given resource path (starts with "/").
     *
     * @param resPath given resource
     * @return url set of resource of given resource path
     */
    public static Set<URL> findAllRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Enumeration<URL> urls = classLoader.getResources(JieString.removeStart(resPath, "/"));
            if (!urls.hasMoreElements()) {
                return Collections.emptySet();
            }
            Set<URL> result = new LinkedHashSet<>();
            while (urls.hasMoreElements()) {
                result.add(urls.nextElement());
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to:
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, Class, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, Class<T> targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to:
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, Type, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, Type targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Maps source object from source type to target type, return null if mapping is unsupported or the result itself is
     * null. This method is equivalent to:
     * <pre>
     *     return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
     * </pre>
     * Using {@link Mapper} for more mapping operations.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see Mapper#defaultMapper()
     * @see Mapper#map(Object, TypeRef, MappingOptions)
     */
    @Nullable
    public static <T> T map(@Nullable Object source, TypeRef<T> targetType) {
        return Mapper.defaultMapper().map(source, targetType, MappingOptions.defaultOptions());
    }

    /**
     * Copies properties from source object to dest object, return the dest object. This method is equivalent to:
     * <pre>
     *     BeanMapper.defaultMapper().copyProperties(source, dest, MappingOptions.defaultOptions());
     *     return dest;
     * </pre>
     * Using {@link BeanMapper} for more mapping operations.
     *
     * @param source source object
     * @param dest   dest object
     * @param <T>    dest type
     * @return dest object
     * @see BeanMapper#defaultMapper()
     * @see BeanMapper#copyProperties(Object, Object, MappingOptions)
     */
    public static <T> T copyProperties(Object source, T dest) {
        BeanMapper.defaultMapper().copyProperties(source, dest, MappingOptions.defaultOptions());
        return dest;
    }

    /**
     * Copies properties from source object to dest object (specified ignored properties will be excluded), return the
     * dest object. This method is equivalent to:
     * <pre>
     *     BeanMapper.defaultMapper().copyProperties(source, dest,
     *         MappingOptions.builder().ignored(JieArray.asList(ignoredProperties)).build());
     *     return dest;
     * </pre>
     * Using {@link BeanMapper} for more mapping operations.
     *
     * @param source            source object
     * @param dest              dest object
     * @param ignoredProperties ignored properties
     * @param <T>               dest type
     * @return dest object
     * @see BeanMapper#defaultMapper()
     * @see BeanMapper#copyProperties(Object, Object, MappingOptions)
     */
    public static <T> T copyProperties(Object source, T dest, Object... ignoredProperties) {
        BeanMapper.defaultMapper().copyProperties(source, dest,
            MappingOptions.builder().ignored(JieArray.asList(ignoredProperties)).build());
        return dest;
    }

    /**
     * Returns block count of total value and block size:
     * <pre>
     *     return totalSize % blockSize == 0 ? totalSize / blockSize : totalSize / blockSize + 1;
     * </pre>
     *
     * @param totalSize total size
     * @param blockSize block size
     * @return block count
     */
    public static int countBlock(int totalSize, int blockSize) {
        return totalSize % blockSize == 0 ? totalSize / blockSize : totalSize / blockSize + 1;
    }

    /**
     * Returns block count of total value and block size:
     * <pre>
     *     return totalSize % blockSize == 0 ? totalSize / blockSize : totalSize / blockSize + 1;
     * </pre>
     *
     * @param totalSize total size
     * @param blockSize block size
     * @return block count
     */
    public static long countBlock(long totalSize, long blockSize) {
        return totalSize % blockSize == 0 ? totalSize / blockSize : totalSize / blockSize + 1;
    }

    /**
     * Sleeps current thread for specified milliseconds.
     *
     * @param millis specified milliseconds
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sleeps current thread for specified duration.
     *
     * @param duration specified duration
     */
    public static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis(), duration.getNano());
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns a new starter to build and start a {@link Thread}.
     *
     * @return a new starter to build and start a {@link Thread}
     */
    public static ThreadStarter threadStarter() {
        return ThreadStarter.newInstance();
    }

    /**
     * Returns a new starter to build and start a {@link Process}.
     *
     * @return a new starter to build and start a {@link Process}
     */
    public static ProcessStarter processStarter() {
        return ProcessStarter.newInstance();
    }

    /**
     * Returns a new builder to build a {@link ExecutorService}.
     *
     * @return a new builder to build a {@link ExecutorService}
     */
    public static ExecutorBuilder executorBuilder() {
        return ExecutorBuilder.newInstance();
    }

    /**
     * Returns a new builder to build a {@link ScheduledExecutorService}.
     *
     * @return a new builder to build a {@link ScheduledExecutorService}
     */
    public static ScheduledBuilder scheduledBuilder() {
        return ScheduledBuilder.newInstance();
    }

    /**
     * Returns a new {@link RandomBuilder} to build a Random {@link Supplier}.
     *
     * @param <T> type of random object to be built
     * @return a new {@link RandomBuilder} to build a Random {@link Supplier}
     */
    public static <T> RandomBuilder<T> randomBuilder() {
        return RandomBuilder.newInstance();
    }

    /**
     * Logs message with level of {@link GekLog.Level#INFO} with {@link GekLog#getInstance()}.
     *
     * @param message message
     */
    public static void log(Object... message) {
        GekLog.getInstance().logOffset(GekLog.Level.INFO, 2, message);
    }

    /**
     * Returns given elements itself as array.
     * <p>
     * This method directly invokes {@link JieArray#asArray(Object[])}.
     *
     * @param elements given elements
     * @param <T>      component type
     * @return given elements itself as array
     */
    @SafeVarargs
    public static <T> T[] asArray(T... elements) {
        return JieArray.asArray(elements);
    }

    /**
     * Wraps and returns given array as a mutable and size-fixed list, the elements directly come from given array, any
     * modification will reflect each other.
     * <p>
     * This method directly invokes {@link JieArray#asList(Object[])}.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a mutable and size-fixed list
     * @see JieArray#asList(Object[])
     */
    @SafeVarargs
    public static <T> List<T> asList(T... elements) {
        return JieArray.asList(elements);
    }

    /**
     * Wraps and returns given array as an immutable list, the elements directly come from given array, any modification
     * will reflect returned list.
     * <p>
     * This method directly invokes {@link JieArray#listOf(Object[])}.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return an immutable list
     * @see JieArray#listOf(Object[])
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return JieArray.listOf(elements);
    }

    /**
     * Returns a new {@link ArrayList} and add all given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a new {@link ArrayList} and add all given elements
     */
    @SafeVarargs
    public static <T> ArrayList<T> newList(T... elements) {
        return JieColl.addAll(new ArrayList<>(elements.length), elements);
    }

    /**
     * Returns a new {@link LinkedHashSet} and add all given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return a new {@link LinkedHashSet} and add all given elements
     */
    @SafeVarargs
    public static <T> LinkedHashSet<T> newSet(T... elements) {
        return JieColl.addAll(new LinkedHashSet<>(elements.length), elements);
    }

    /**
     * Returns a new {@link LinkedHashMap} and add all given elements.
     * <p>
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <T>      type of element
     * @return a new {@link LinkedHashMap} and add all given elements
     * @see JieColl#toMap(Object...)
     */
    @SafeVarargs
    public static <K, V, T> LinkedHashMap<K, V> newMap(T... elements) {
        return JieColl.addAll(new LinkedHashMap<>(), elements);
    }

    /**
     * Returns a new collection builder.
     *
     * @return a new collection builder
     * @see CollBuilder#newBuilder()
     */
    public static CollBuilder collBuilder() {
        return CollBuilder.newBuilder();
    }
}
