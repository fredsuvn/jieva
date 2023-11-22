package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.bean.GekBeanCopier;
import xyz.fsgek.common.convert.GekConverter;
import xyz.fsgek.common.reflect.TypeRef;

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
public class Gek {

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
     * @param <T>          type of value
     * @return default value if given object is null, or given object itself if it is not null
     */
    public static <T> T notNull(@Nullable T obj, T defaultValue) {
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
     * @param <T>           type of value
     * @return computed value if given object is null, or given object itself if it is not null
     */
    public static <T> T notNull(@Nullable T obj, Supplier<? extends T> computedValue) {
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
    public static <K, V> V notNull(@Nullable K obj, V defaultValue, Function<? super K, ? extends V> function) {
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
     * <li>returns Objects.hashCode for given object if it is not an array;</li>
     * <li>if given object is primitive array, returns Arrays.hashCode for it;</li>
     * <li>if given object is Object[], returns Arrays.deepHashCode for it;</li>
     * <li>else returns Objects.hashCode for given object</li>
     * </ul>
     * <p>
     * This method is same as: hash(obj, true, true)
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
     * @return the hash code
     */
    public static int hashWith(@Nullable Object obj, boolean arrayCheck, boolean deepHash) {
        if (obj == null || !arrayCheck) {
            return Objects.hashCode(obj);
        }
        Class<?> type = obj.getClass();
        if (!type.isArray()) {
            return obj.hashCode();
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
     * @return the result of equaling
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return equalsWith(a, b, true, true);
    }

    /**
     * Returns deep-equals for given objects.
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
        for (int i = 0; i < objs.length - 2; i++) {
            if (!equals(objs[i], objs[i + 1])) {
                return false;
            }
        }
        return true;
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
            return deepEquals ? Arrays.deepEquals((Object[]) a, (Object[]) b) : Arrays.equals((Object[]) a, (Object[]) b);
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
        GekCheck.checkArgument(enumClass.isEnum(), "Not an enum class.");
        if (!ignoreCase) {
            try {
                return Enum.valueOf((Class<T>) enumClass, name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        Object[] enums = enumClass.getEnumConstants();
        GekCheck.checkArgument(enums != null, "Not an enum class.");
        for (Object anEnum : enums) {
            if (name.equalsIgnoreCase(anEnum.toString())) {
                return (T) anEnum;
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
        GekCheck.checkArgument(enumClass.isEnum(), enumClass + " is not an enum.");
        GekCheck.checkArgument(index >= 0, "index must >= 0.");
        Object[] enums = enumClass.getEnumConstants();
        GekCheck.checkArgument(enums != null, enumClass + " is not an enum.");
        if (index >= enums.length) {
            return null;
        }
        return (T) enums[index];
    }

    /**
     * Finds resource of given resource path (starts with "/").
     *
     * @param resPath given resource
     * @return url of resource of given resource path
     */
    public static URL findRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(GekString.removeStart(resPath, "/"));
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
            Enumeration<URL> urls = classLoader.getResources(GekString.removeStart(resPath, "/"));
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
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see GekConverter#convert(Object, Class)
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        return GekConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see GekConverter#convert(Object, TypeRef)
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, TypeRef<T> targetType) {
        return GekConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object from source type to target type,
     * return null if conversion is unsupported or the result itself is null.
     *
     * @param source     source object
     * @param targetType target type
     * @param <T>        target type
     * @return converted object or null
     * @see GekConverter#convert(Object, Type)
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Type targetType) {
        return GekConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object from source type to target type.
     * The result of this method in 3 types: {@code null}, {@link GekWrapper} and others:
     * <ul>
     *     <li>
     *         {@code null}: means this converter can not do this conversion;
     *     </li>
     *     <li>
     *         {@link GekWrapper}: wrapped object (from {@link GekWrapper#get()}) is actual result of conversion,
     *         including {@code null} and {@link GekWrapper} itself;
     *     </li>
     *     <li>
     *         Others: any other type of returned object is the actual result of conversion.
     *     </li>
     * </ul>
     * Using {@link GekConverter#getResult(Object)} can get actual result object from wrapper.
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @return converted object or null
     * @see GekConverter#convertType(Object, Type, Type)
     */
    @Nullable
    public static Object convertType(@Nullable Object source, Type sourceType, Type targetType) {
        return GekConverter.defaultConverter().convertType(source, sourceType, targetType);
    }

    /**
     * Copies properties from source object to dest object by {@link GekBeanCopier#defaultCopier()}.
     *
     * @param source source object
     * @param dest   dest object
     * @param <T>    dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest) {
        return GekBeanCopier.defaultCopier().copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object by {@link GekBeanCopier}.
     * The properties with null value will not be copied if {@code copyNull} is true.
     * Otherwise, the null will not be copied.
     *
     * @param source   source object
     * @param dest     dest object
     * @param copyNull whether copy null properties
     * @param <T>      dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, boolean copyNull) {
        if (copyNull) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .build()
            .copyProperties(source, dest);

    }

    /**
     * Copies properties from source object to dest object by {@link GekBeanCopier#defaultCopier()}.
     * The properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param dest              dest object
     * @param ignoredProperties property names that are specified to ignore
     * @param <T>               dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, String... ignoredProperties) {
        if (GekArray.isEmpty(ignoredProperties)) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .propertyNameMapper(name -> GekArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object by {@link GekBeanCopier#defaultCopier()}.
     * The properties with null value will not be copied if {@code copyNull} is true,
     * and the properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param dest              dest object
     * @param copyNull          whether copy null properties
     * @param ignoredProperties property names that are specified to ignore
     * @param <T>               dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, boolean copyNull, String... ignoredProperties) {
        if (copyNull && GekArray.isEmpty(ignoredProperties)) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .propertyNameMapper(name -> GekArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link GekBeanCopier#defaultCopier()}.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param <T>        dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
        return GekBeanCopier.defaultCopier().copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link GekBeanCopier}.
     * The properties with null value will not be copied if {@code copyNull} is true.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param copyNull   whether copy null properties
     * @param <T>        dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, boolean copyNull) {
        if (copyNull) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .build()
            .copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link GekBeanCopier#defaultCopier()}.
     * The properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param sourceType        specified type of source object
     * @param dest              dest object
     * @param destType          specified type of dest type
     * @param ignoredProperties property names that are specified to ignore
     * @param <T>               dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, String... ignoredProperties) {
        if (GekArray.isEmpty(ignoredProperties)) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .propertyNameMapper(name -> GekArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link GekBeanCopier#defaultCopier()}.
     * The properties with null value will not be copied if {@code copyNull} is true,
     * and the properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param sourceType        specified type of source object
     * @param dest              dest object
     * @param destType          specified type of dest type
     * @param copyNull          whether copy null properties
     * @param ignoredProperties property names that are specified to ignore
     * @param <T>               dest type
     * @return dest object
     * @see GekBeanCopier
     */
    public static <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, boolean copyNull, String... ignoredProperties) {
        if (copyNull && GekArray.isEmpty(ignoredProperties)) {
            return GekBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return GekBeanCopier.defaultCopier().toBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .propertyNameMapper(name -> GekArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, sourceType, dest, destType);
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
     * Returns a new {@link Process} configurer to start a sub-process.
     *
     * @return a new {@link Process} configurer to start a sub-process
     */
    public static GekProcess process() {
        return GekProcess.newInstance();
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
     * Returns a new {@link Thread} configurer to build or start a thread.
     *
     * @return a new {@link Thread} configurer to build or start a thread
     */
    public static GekThread thread() {
        return GekThread.newInstance();
    }

    /**
     * Returns a new {@link ExecutorService} configurer to build a thread pool.
     *
     * @return a new {@link ExecutorService} configurer to build a thread pool
     */
    public static GekThreadPool threadPool() {
        return GekThreadPool.newInstance();
    }

    /**
     * Returns a new {@link ScheduledExecutorService} configurer to build a scheduled thread pool.
     *
     * @return a new {@link ScheduledExecutorService} configurer to build a scheduled thread pool
     */
    public static GekScheduledPool scheduledPool() {
        return GekScheduledPool.newInstance();
    }
}
