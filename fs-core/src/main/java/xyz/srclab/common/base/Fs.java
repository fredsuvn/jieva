package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanCopier;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.TypeRef;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for Object.
 *
 * @author fredsuvn
 */
public class Fs {

    /**
     * A flag object that represents 'continue' to continue a loop.
     */
    public static Object CONTINUE = new Object();

    /**
     * A flag object that represents 'break' to break out from a loop.
     */
    public static Object BREAK = new Object();

    /**
     * A flag object that represents 'return' to return from a process.
     */
    public static Object RETURN = new Object();

    /**
     * A flag object that represents 'goto' to jump to a process point.
     */
    public static Object GOTO = new Object();

    /**
     * Casts given object as given type T.
     *
     * @param obj given object
     * @param <T> given type T
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
     */
    public static <T> T notNull(@Nullable T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
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
     */
    public static int hash(@Nullable Object obj) {
        return hashWith(obj, true, true);
    }

    /**
     * Returns deep-hash-code for given objects.
     *
     * @param objs given objects
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
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return equalsWith(a, b, true, true);
    }

    /**
     * Returns deep-equals for given objects.
     *
     * @param objs given objects
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
     * @param enumClass  given enum class
     * @param name       specified name
     * @param ignoreCase whether ignore case for specified name
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, String name, boolean ignoreCase) {
        FsCheck.checkArgument(enumClass.isEnum(), "Not an enum class.");
        if (!ignoreCase) {
            try {
                return Enum.valueOf((Class<T>) enumClass, name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        Object[] enums = enumClass.getEnumConstants();
        FsCheck.checkArgument(enums != null, "Not an enum class.");
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
     */
    @Nullable
    public static <T extends Enum<T>> T findEnum(Class<?> enumClass, int index) {
        FsCheck.checkArgument(enumClass.isEnum(), enumClass + " is not an enum.");
        FsCheck.checkArgument(index >= 0, "index must >= 0.");
        Object[] enums = enumClass.getEnumConstants();
        FsCheck.checkArgument(enums != null, enumClass + " is not an enum.");
        if (index >= enums.length) {
            return null;
        }
        return (T) enums[index];
    }

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

    /**
     * Returns stack trace info of given throwable as string.
     *
     * @param throwable given throwable
     */
    public static String stackTraceToString(Throwable throwable) {
        return stackTraceToString(throwable, null);
    }

    /**
     * Returns stack trace info of given throwable as string,
     * the line separator of return string will be replaced by given line separator if given separator is not null.
     *
     * @param throwable     given throwable
     * @param lineSeparator given separator
     */
    public static String stackTraceToString(Throwable throwable, @Nullable String lineSeparator) {
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

    /**
     * Finds resource of given resource path (starts with "/").
     *
     * @param resPath given resource
     */
    public static URL findRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        return classLoader.getResource(FsString.removeStart(resPath, "/"));
    }

    /**
     * Finds all resources of given resource path (starts with "/").
     *
     * @param resPath given resource
     */
    public static Set<URL> findAllRes(String resPath) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Enumeration<URL> urls = classLoader.getResources(FsString.removeStart(resPath, "/"));
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
     * Runs a new thread.
     */
    public static Thread runThread(Runnable runnable) {
        return runThread(null, false, runnable);
    }

    /**
     * Runs a new thread with given thread name.
     *
     * @param threadName given thread name
     * @param runnable   run content
     */
    public static Thread runThread(@Nullable String threadName, Runnable runnable) {
        return runThread(threadName, false, runnable);
    }

    /**
     * Runs a new thread with given thread name, whether the thread is daemon.
     *
     * @param threadName given thread name
     * @param daemon     whether the thread is daemon
     * @param runnable   run content
     */
    public static Thread runThread(@Nullable String threadName, boolean daemon, Runnable runnable) {
        Thread thread = new Thread(runnable);
        if (threadName != null) {
            thread.setName(threadName);
        }
        if (daemon) {
            thread.setDaemon(daemon);
        }
        thread.start();
        return thread;
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
     * Starts a process with given command.
     *
     * @param cmd given command
     */
    public static Process runProcess(String cmd) {
        String[] splits = cmd.split(" ");
        List<String> actualCmd = Arrays.stream(splits)
            .filter(FsString::isNotBlank)
            .map(String::trim)
            .collect(Collectors.toList());
        return runProcess(false, actualCmd);
    }

    /**
     * Starts a process with given command.
     *
     * @param cmd given command
     */
    public static Process runProcess(String... cmd) {
        return runProcess(false, cmd);
    }

    /**
     * Starts a process with given command and whether redirect error stream.
     *
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     */
    public static Process runProcess(boolean redirectErrorStream, String... cmd) {
        return runProcess(redirectErrorStream, Arrays.asList(cmd));
    }

    /**
     * Starts a process with given command and whether redirect error stream.
     *
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     */
    public static Process runProcess(boolean redirectErrorStream, List<String> cmd) {
        return runProcess(null, null, redirectErrorStream, cmd);
    }

    /**
     * Starts a process with given command, given environment, given directory file, and whether redirect error stream.
     *
     * @param env                 given environment
     * @param dir                 given directory file
     * @param redirectErrorStream whether redirect error stream
     * @param cmd                 given command
     */
    public static Process runProcess(
        @Nullable Map<String, String> env,
        @Nullable File dir,
        boolean redirectErrorStream,
        List<String> cmd
    ) {
        ProcessBuilder builder = new ProcessBuilder();
        if (env != null) {
            builder.environment().putAll(env);
        }
        if (dir != null) {
            builder.directory(dir);
        }
        if (redirectErrorStream) {
            builder.redirectErrorStream(true);
        }
        builder.command(cmd);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns default class loader: {@link Thread#getContextClassLoader()}.
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Converts source object to target type by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Class<T> targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object to target type by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference of target type
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, TypeRef<T> targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType type reference target type
     * @param options    given options
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, TypeRef<T> targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object to target type by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Type targetType) {
        return FsConverter.defaultConverter().convert(source, targetType);
    }

    /**
     * Converts source object to target type with given options by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param targetType target type
     * @param options    given options
     * @see FsConverter
     */
    @Nullable
    public static <T> T convert(@Nullable Object source, Type targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convert(source, targetType, options);
    }

    /**
     * Converts source object from source type to target type by {@link FsConverter#defaultConverter()}.
     * If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @see FsConverter
     */
    @Nullable
    public static <T> T convertType(@Nullable Object source, Type sourceType, Type targetType) {
        return FsConverter.defaultConverter().convertType(source, sourceType, targetType);
    }

    /**
     * Converts source object from source type to target type with given options
     * by {@link FsConverter#defaultConverter()}. If the conversion is unsupported, return null.
     * <p>
     * <b>Note returned value after conversion itself may also be null.</b>
     *
     * @param source     source object
     * @param sourceType source type
     * @param targetType target type
     * @param options    given options
     * @see FsConverter
     */
    @Nullable
    public static <T> T convertType(
        @Nullable Object source, Type sourceType, Type targetType, FsConverter.Options options) {
        return FsConverter.defaultConverter().convertType(source, sourceType, targetType, options);
    }

    /**
     * Resolves given type to {@link FsBean}.
     *
     * @param type given type
     */
    public static FsBean resolveBean(Type type) {
        return FsBeanResolver.defaultResolver().resolve(type);
    }

    /**
     * Wraps given map as a {@link FsBean}, of which type will be seen as Map&lt;String, Object>.
     * This method is same with:
     * <pre>
     *     wrapMap(map, null);
     * </pre>
     *
     * @param map given map
     * @see #wrapBean(Map, Type)
     */
    public static FsBean wrapBean(Map<String, ?> map) {
        return wrapBean(map, null);
    }

    /**
     * Wraps given map as a {@link FsBean}, the key type of map type must be {@link String}.
     * If the given map type is null, the map type will be seen as Map&lt;String, Object>.
     * <p>
     * Result of {@link FsBean#getProperties()} is immutable, but content may be different for each time calling.
     * Because of the changes in given map, contents of return property map are also changed accordingly.
     *
     * @param map     given map
     * @param mapType given map type
     */
    public static FsBean wrapBean(Map<String, ?> map, @Nullable Type mapType) {
        return FsBeanResolver.defaultResolver().wrapMap(map, mapType);
    }

    /**
     * Copies properties from source object to dest object by {@link FsBeanCopier#defaultCopier()}.
     *
     * @param source source object
     * @param dest   dest object
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest) {
        return FsBeanCopier.defaultCopier().copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object by {@link FsBeanCopier}.
     * The properties with null value will not be copied if {@code copyNull} is true.
     *
     * @param source   source object
     * @param dest     dest object
     * @param copyNull whether copy null properties
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, boolean copyNull) {
        if (copyNull) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .build()
            .copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object by {@link FsBeanCopier#defaultCopier()}.
     * The properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param dest              dest object
     * @param ignoredProperties property names that are specified to ignore
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, String... ignoredProperties) {
        if (FsArray.isEmpty(ignoredProperties)) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .propertyNameMapper(name -> FsArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object by {@link FsBeanCopier#defaultCopier()}.
     * The properties with null value will not be copied if {@code copyNull} is true,
     * and the properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param dest              dest object
     * @param copyNull          whether copy null properties
     * @param ignoredProperties property names that are specified to ignore
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, T dest, boolean copyNull, String... ignoredProperties) {
        if (copyNull && FsArray.isEmpty(ignoredProperties)) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .propertyNameMapper(name -> FsArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, dest);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link FsBeanCopier#defaultCopier()}.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, Type sourceType, T dest, Type destType) {
        return FsBeanCopier.defaultCopier().copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link FsBeanCopier}.
     * The properties with null value will not be copied if {@code copyNull} is true.
     *
     * @param source     source object
     * @param sourceType specified type of source object
     * @param dest       dest object
     * @param destType   specified type of dest type
     * @param copyNull   whether copy null properties
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(Object source, Type sourceType, T dest, Type destType, boolean copyNull) {
        if (copyNull) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .build()
            .copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link FsBeanCopier#defaultCopier()}.
     * The properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param sourceType        specified type of source object
     * @param dest              dest object
     * @param destType          specified type of dest type
     * @param ignoredProperties property names that are specified to ignore
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, String... ignoredProperties) {
        if (FsArray.isEmpty(ignoredProperties)) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .propertyNameMapper(name -> FsArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Copies properties from source object to dest object with specified types by {@link FsBeanCopier#defaultCopier()}.
     * The properties with null value will not be copied if {@code copyNull} is true,
     * and the properties that are specified to ignore will not be copied.
     *
     * @param source            source object
     * @param sourceType        specified type of source object
     * @param dest              dest object
     * @param destType          specified type of dest type
     * @param copyNull          whether copy null properties
     * @param ignoredProperties property names that are specified to ignore
     * @see FsBeanCopier
     */
    public static <T> T copyProperties(
        Object source, Type sourceType, T dest, Type destType, boolean copyNull, String... ignoredProperties) {
        if (copyNull && FsArray.isEmpty(ignoredProperties)) {
            return FsBeanCopier.defaultCopier().copyProperties(source, dest);
        }
        return FsBeanCopier.newBuilder()
            .sourcePropertyFilter((name, value) -> value != null)
            .propertyNameMapper(name -> FsArray.indexOf(ignoredProperties, name) >= 0 ? null : name)
            .build()
            .copyProperties(source, sourceType, dest, destType);
    }

    /**
     * Returns chunk count of total value and chunk size:
     * <pre>
     *     return total % chunkSize == 0 ? total / chunkSize : total / chunkSize + 1;
     * </pre>
     *
     * @param total     total value
     * @param chunkSize chunk size
     */
    public static int chunkCount(int total, int chunkSize) {
        return total % chunkSize == 0 ? total / chunkSize : total / chunkSize + 1;
    }

    /**
     * Returns chunk count of total value and chunk size:
     * <pre>
     *     return total % chunkSize == 0 ? total / chunkSize : total / chunkSize + 1;
     * </pre>
     *
     * @param total     total value
     * @param chunkSize chunk size
     */
    public static long chunkCount(long total, long chunkSize) {
        return total % chunkSize == 0 ? total / chunkSize : total / chunkSize + 1;
    }
}
