/**
 * Bt includes common and misc utilities.
 */
@file:JvmName("Bt")

package xyz.srclab.common.base

import xyz.srclab.common.Boat
import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import xyz.srclab.common.io.readString
import xyz.srclab.common.reflect.defaultClassLoader
import java.io.*
import java.net.URL
import java.nio.charset.Charset
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.stackTraceToString as stackTraceToStringKt
import kotlin.toString as toStringKt

/**
 * Represents `less than` in [Comparable] and [Comparator].
 */
const val COMPARE_LT = -1

/**
 * Represents `equal to` in [Comparable] and [Comparator].
 */
const val COMPARE_EQ = 0

/**
 * Represents `greater than` in [Comparable] and [Comparator].
 */
const val COMPARE_GT = 1

private val local: ThreadLocal<MutableMap<Any, Any?>> = ThreadLocal.withInitial { HashMap() }

/**
 * Returns default charset: UTF-8.
 */
@JvmName("charset")
fun defaultCharset(): Charset = utf8()

/**
 * Returns default buffer size: 8 * 1024.
 */
@JvmName("bufferSize")
fun defaultBufferSize(): Int = 8 * 1024

/**
 * Returns default serial version for current boat version.
 */
@JvmName("serialVersion")
fun defaultSerialVersion(): Long = Boat.serialVersion()

/**
 * Returns default radix: 10.
 */
@JvmName("radix")
fun defaultRadix(): Int = 10

/**
 * Returns default locale: [Locale.ENGLISH].
 */
@JvmName("locale")
fun defaultLocale(): Locale = Locale.ENGLISH

/**
 * Returns default concurrency level: [availableProcessors] * 2.
 */
@JvmName("concurrencyLevel")
fun defaultConcurrencyLevel(): Int = availableProcessors() * 4

/**
 * Returns default timestamp pattern: yyyyMMddHHmmssSSS.
 */
@JvmName("timestampPattern")
fun defaultTimestampPattern(): DatePattern = BtHolder.defaultTimestampPattern

/**
 * Returns default [String] for `null`.
 */
@JvmName("nullString")
fun defaultNullString(): String = "null"

/**
 * Casts [this] to any type.
 */
@JvmName("as")
fun <T> Any?.asType(): T {
    return this as T
}

/**
 * Casts [this] to any type.
 */
@JvmSynthetic
@JvmName("as")
fun <T> Nothing?.asType(): T {
    return this as T
}

/**
 * Casts [this] to any non-null type.
 */
fun <T : Any> T?.asNotNull(): T {
    return this as T
}

/**
 * Returns whether [this] equals [other].
 */
fun Any?.equals(other: Any?): Boolean {
    return Objects.equals(this, other)
}

/**
 * Returns whether [this] equals [other], or deep equals for array types.
 */
fun Any?.deepEquals(other: Any?): Boolean {
    return Objects.deepEquals(this, other)
}

/**
 * Returns true if [this] equals any of [others].
 */
fun Any?.anyEquals(vararg others: Any?): Boolean {
    return anyPredicate({ obj -> this.equals(obj) }, *others)
}

/**
 * Returns true if [this] equals each of [others].
 */
fun Any?.allEquals(vararg others: Any?): Boolean {
    return allPredicate({ obj -> this.equals(obj) }, *others)
}

/**
 * Returns whether all [args] are equals each other.
 */
fun equals(vararg args: Any?): Boolean {
    for (i in 0..args.size - 2) {
        if (!Objects.equals(args[i], args[i + 1])) {
            return false
        }
    }
    return true
}

/**
 * Returns hash code of [this].
 */
fun Any?.hash(): Int {
    return this.hashCode()
}

/**
 * Returns identity hash code with [System.identityHashCode].
 */
fun Any?.idHash(): Int {
    return System.identityHashCode(this)
}

/**
 * Returns hash code of [this], or array hash code if this is an array.
 */
fun Any?.arrayHash(): Int {
    return when (this) {
        is BooleanArray -> Arrays.hashCode(this)
        is ByteArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.hashCode(this)
        else -> hash()
    }
}

/**
 * Returns hash code of [this],
 * or array hash code if this is an array,
 * or deep array hash code if this is a deep array.
 */
fun Any?.deepHash(): Int {
    return when (this) {
        is BooleanArray -> Arrays.hashCode(this)
        is ByteArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.deepHashCode(this)
        else -> hash()
    }
}

/**
 * Returns a hash code based on the contents of this array as if it is `List`.
 */
fun hash(vararg args: Any?): Int {
    return args.contentHashCode()
}

/**
 * Returns a hash code based on the contents of this array as if it is `List`.
 * Nested arrays are treated as lists too.
 *
 * If any of arrays contains itself on any nesting level the behavior is undefined.
 */
fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}

/**
 * Returns `toString` of [this].
 */
fun Any?.toString(): String {
    return this.toStringKt()
}

/**
 * Returns `toString` of [this], or array `toString` if this is an array.
 */
fun Any?.arrayToString(): String {
    return when (this) {
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        is Array<*> -> Arrays.toString(this)
        else -> this.toStringKt()
    }
}

/**
 * Returns `toString` of [this],
 * or array `toString` if this is an array,
 * or deep array `toString` if this is a deep array.
 */
fun Any?.deepToString(): String {
    return when (this) {
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        is Array<*> -> Arrays.deepToString(this)
        else -> this.toStringKt()
    }
}

/**
 * Returns `toString` of given [args] by [arrayToString].
 */
fun toString(vararg args: Any?): String {
    return args.arrayToString()
}

/**
 * Returns `deepToString` of given [args] by [deepToString].
 */
fun deepToString(vararg args: Any?): String {
    return args.deepToString()
}

/**
 * If [this] is a [CharSequence], return itself; else return `toString` of this.
 */
fun Any?.toCharSeq(): CharSequence {
    return if (this is CharSequence)
        this
    else
        this.toStringKt()
}

/**
 * Returns [t] if it is not null, or [defaultValue] if [t] is null.
 */
fun <T : Any> getOrDefault(t: T?, defaultValue: T): T {
    return t ?: defaultValue
}

/**
 * Returns [t] if it is not null, or computes [supplier] and returns if [t] is null.
 */
fun <T : Any> getOrElse(t: T?, supplier: Supplier<T>): T {
    return t ?: supplier.get()
}

/**
 * Gets or creates a new value.
 * This function is usually used for the pattern:
 *
 * Checks whether the value is null, if it is not, return, else create a new one and set and return.
 */
fun <T : Any> getOrNew(lock: Any, getter: Supplier<T?>, setter: Consumer<T?>, creator: Supplier<T>): T {
    val v = getter.get()
    if (v !== null) {
        return v
    }
    synchronized(lock) {
        val v0 = getter.get()
        if (v0 !== null) {
            return v0
        }
        val nv = creator.get()
        setter.accept(nv)
        return nv
    }
}

/**
 * Gets or creates a new value.
 * This function is usually used for the pattern:
 *
 * Checks whether the value is null, if it is not, return, else create a new one and set and return.
 */
@JvmSynthetic
inline fun <T : Any> getOrNew(lock: Any, getter: () -> T?, setter: (T?) -> Unit, creator: () -> T): T {
    val v = getter()
    if (v !== null) {
        return v
    }
    synchronized(lock) {
        val v0 = getter()
        if (v0 !== null) {
            return v0
        }
        val nv = creator()
        setter(nv)
        return nv
    }
}

/**
 * Converts [this] to boolean:
 *
 * * If this is null, return false;
 * * If this is boolean, return itself;
 * * If this is number, return `false` if this is 0, else `true`;
 * * If this is [CharSequence], return `false` if this is "false", else `true`;
 * * Else `true`.
 */
fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> this.toInt() != 0
        is CharSequence -> !this.contentEquals("false")
        else -> true
    }
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is true.
 */
fun anyTrue(vararg objs: Any?): Boolean {
    return anyPredicate({ obj -> obj.toBoolean() }, *objs)
}

/**
 * Returns true if any of the result of [toBoolean] for each [objs] is false.
 */
fun anyFalse(vararg objs: Any?): Boolean {
    return anyPredicate({ obj -> !obj.toBoolean() }, *objs)
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is true.
 */
fun allTrue(vararg objs: Any?): Boolean {
    return allPredicate({ obj -> obj.toBoolean() }, *objs)
}

/**
 * Returns true if all the result of [toBoolean] for each [objs] is false.
 */
fun allFalse(vararg objs: Any?): Boolean {
    return allPredicate({ obj -> !obj.toBoolean() }, *objs)
}

/**
 * Ensures that this value is not less than [min].
 */
fun <T : Comparable<T>> T.atLeast(min: T): T {
    return this.coerceAtLeast(min)
}

/**
 * Ensures that this value is not greater than [max].
 */
fun <T : Comparable<T>> T.atMost(max: T): T {
    return this.coerceAtMost(max)
}

/**
 * Ensures that this value lies in the specified range.
 */
fun <T : Comparable<T>> T.atBetween(min: T, max: T): T {
    return this.coerceIn(min, max)
}

/**
 * Ensures that this value is not less than [min].
 */
fun <T> T.atLeast(min: T, comparator: Comparator<T>): T {
    return if (comparator.compare(this, min) <= 0) min else this
}

/**
 * Ensures that this value is not greater than [max].
 */
fun <T> T.atMost(max: T, comparator: Comparator<T>): T {
    return if (comparator.compare(this, max) >= 0) max else this
}

/**
 * Ensures that this value lies in the specified range.
 */
fun <T> T.atBetween(min: T, max: T, comparator: Comparator<T>): T {
    if (comparator.compare(this, min) <= 0) {
        return min
    }
    if (comparator.compare(this, max) >= 0) {
        return max
    }
    return this
}

/**
 * Returns whether given value between the given range:
 *
 * ```
 * value >= min && value <= max
 * ```
 */
fun <T : Comparable<T>> T.isBetween(min: T, max: T): Boolean {
    return this in min..max
}

/**
 * Returns whether given value between the given range:
 *
 * ```
 * value >= min && value <= max
 * ```
 */
fun <T> T.isBetween(min: T, max: T, comparator: Comparator<T>): Boolean {
    return comparator.compare(this, min) >= 0 && comparator.compare(this, max) <= 0
}

/**
 * Returns remaining length of [size] from [offset].
 */
fun remainingLength(size: Int, offset: Int): Int = size - offset

/**
 * Returns remaining length of [size] from [offset].
 */
fun remainingLength(size: Long, offset: Long): Long = size - offset

/**
 * Returns end index exclusive computed from [offset] and [length]:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Int, length: Int): Int = offset + length

/**
 * Returns end index exclusive computed from [offset] and [length]:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Long, length: Long): Long = offset + length

/**
 * Returns the least number to meet: [blockSize] * `number` >= [size].
 */
fun blockNumber(size: Int, blockSize: Int): Int {
    val num = size / blockSize
    return if (size % blockSize == 0) num else num + 1
}

/**
 * Returns the least number to meet: [blockSize] * `number` >= [size].
 */
fun blockNumber(size: Long, blockSize: Long): Long {
    val num = size / blockSize
    return if (size % blockSize == 0L) num else num + 1
}

/**
 * Separates the [oldSize] with [oldBlockSize], then change the block size to [newBlockSize],
 * returns the least size for new block size. This function is equivalent to:
 *
 * ```
 * int newSize = oldSize / oldBlockSize * newBlockSize;
 * if (oldSize % oldBlockSize == 0) {
 *     return newSize;
 * }
 * return newSize + newBlockSize;
 * ```
 */
fun newSizeForBlock(oldSize: Int, oldBlockSize: Int, newBlockSize: Int): Int {
    val newSize = oldSize / oldBlockSize * newBlockSize
    return if (oldSize % oldBlockSize == 0) newSize else newSize + newBlockSize
}

/**
 * Separates the [oldSize] with [oldBlockSize], then change the block size to [newBlockSize],
 * returns the least size for new block size. This function is equivalent to:
 *
 * ```
 * int newSize = oldSize / oldBlockSize * newBlockSize;
 * if (oldSize % oldBlockSize == 0) {
 *     return newSize;
 * }
 * return newSize + newBlockSize;
 * ```
 */
fun newSizeForBlock(oldSize: Long, oldBlockSize: Long, newBlockSize: Long): Long {
    val newSize = oldSize / oldBlockSize * newBlockSize
    return if (oldSize % oldBlockSize == 0L) newSize else newSize + newBlockSize
}

/**
 * Returns the detailed description of this throwable with its stack trace.
 */
fun Throwable.stackTraceToString(): String {
    return this.stackTraceToStringKt()
}

/**
 * Returns current [Thread].
 */
fun currentThread(): Thread {
    return Thread.currentThread()
}

/**
 * Sleeps current thread for [millis] and [nanos].
 */
@JvmOverloads
fun sleep(millis: Long, nanos: Int = 0) {
    Thread.sleep(millis, nanos)
}

/**
 * Sleeps current thread for [duration].
 */
fun sleep(duration: Duration) {
    sleep(duration.toMillis(), duration.nano)
}

/**
 * Returns thread local value of [key].
 */
@JvmName("getLocal")
fun <T> getThreadLocal(key: Any): T {
    return local.get()[key].asType()
}

/**
 * Sets thread local value of [key], returns old value.
 */
@JvmName("setLocal")
fun <T> setThreadLocal(key: Any, value: Any?): T {
    return local.get().put(key, value).asType()
}

/**
 * Returns container of all thread local values managed by `BThread`.
 *
 * Note any change for returned map will reflect [getThreadLocal] and [setThreadLocal], and vice versa.
 */
@JvmName("getLocals")
fun threadLocals(): MutableMap<Any, Any?> {
    return local.get()
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [predicate] for each stack trace element of current [Thread.getStackTrace]:
 *
 * * Invokes `predicate.test(element, false)` for each stack trace element util the `true` has returned,
 * let the index of this element be `loggerIndex`;
 * * Invokes `predicate.test(element, true)` for each stack trace element
 * from the `loggerIndex` exclusive util the `true` has returned
 * let the index of this element be `callerIndex`;
 * * Return element of which index is `callerIndex`.
 *
 * Note if stack trace elements of current thread is null,
 * or index of returned element is out of bounds, return null.
 */
fun callerStackTrace(
    predicate: BiPredicate<StackTraceElement, Boolean>
): StackTraceElement? {
    return callerStackTrace(0, predicate)
}

/**
 * Try to find caller stack trace element, usually used in logging.
 *
 * This function invokes [predicate] for each stack trace element of current [Thread.getStackTrace]:
 *
 * * Invokes `predicate.test(element, false)` for each stack trace element util the `true` has returned,
 * let the index of this element be `loggerIndex`;
 * * Invokes `predicate.test(element, true)` for each stack trace element
 * from the `loggerIndex` exclusive util the `true` has returned
 * let the index of this element be `callerIndex`;
 * * Return element of which index is ([offset] + `callerIndex`).
 *
 * Note if stack trace elements of current thread is null,
 * or index of returned element is out of bounds, return null.
 */
fun callerStackTrace(
    offset: Int,
    predicate: BiPredicate<StackTraceElement, Boolean>
): StackTraceElement? {
    val stackTrace = currentThread().stackTrace
    if (stackTrace.isNullOrEmpty()) {
        return null
    }
    for (i in stackTrace.indices) {
        var result = predicate.test(stackTrace[i], false)
        if (result) {
            for (j in i + 1 until stackTrace.size) {
                result = predicate.test(stackTrace[j], true)
                if (result) {
                    val index = j + offset
                    if (index.isInBounds(0, stackTrace.size)) {
                        return stackTrace[index]
                    }
                    return null
                }
            }
        }
    }
    return null
}

/**
 * Loads resource in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadResource(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): URL? {
    return classLoader.getResource(path.removeAbsolute().removeAbsolute())
}

/**
 * Loads resource as [InputStream] in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadStream(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): InputStream? {
    return classLoader.getResource(path.removeAbsolute().removeAbsolute())?.openStream()
}

/**
 * Loads content of resource as string in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadString(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): String? {
    return loadResource(path.removeAbsolute(), classLoader)?.openStream()?.readString(charset, true)
}

/**
 * Loads content of resource as properties in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadProperties(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): Map<String, String>? {
    return loadResource(path.removeAbsolute(), classLoader)?.openStream()?.readProperties(charset, true)
}

/**
 * Loads all same-path resources in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadResources(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): List<URL> {
    return classLoader.getResources(path.removeAbsolute()).toList()
}

/**
 * Loads all same-path contents of resources as strings in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadStrings(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): List<String> {
    return classLoader.getResources(path.removeAbsolute()).asSequence().map {
        it.openStream().readString(charset, true)
    }.toList()
}

/**
 * Loads all same-path contents of resources as properties list in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadPropertiesList(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): List<Map<String, String>> {
    return classLoader.getResources(path.removeAbsolute()).asSequence().map {
        it.openStream().readProperties(charset, true)
    }.toList()
}

private fun CharSequence.removeAbsolute(): String {
    return this.removeIfStartWith("/")
}

/**
 * Writes [this] object into [dest].
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(dest: OutputStream, close: Boolean = false) {
    val oop = ObjectOutputStream(dest)
    oop.writeObject(this)
    if (close) {
        oop.close()
    }
}

/**
 * Writes [this] object into [file].
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(file: File, close: Boolean = false) {
    return this.writeObject(FileOutputStream(file), close)
}

/**
 * Writes [this] object into file [fileName].
 * @param close whether close the stream after writing
 */
@JvmOverloads
fun Any.writeObject(fileName: CharSequence, close: Boolean = false) {
    return this.writeObject(FileOutputStream(fileName.toString()), close)
}

/**
 * Reads object from [this] input stream.
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> InputStream.readObject(close: Boolean = false): T {
    val ooi = ObjectInputStream(this)
    return ooi.readObject().asType<T>().let {
        if (close) {
            this.close()
        }
        it
    }
}

/**
 * Reads object from [this] file.
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> File.readObject(close: Boolean = false): T {
    return FileInputStream(this).readObject(close)
}

/**
 * Reads object from file [fileName].
 * @param close whether close the stream after reading
 */
@JvmOverloads
fun <T> readObject(fileName: CharSequence, close: Boolean = false): T {
    return FileInputStream(fileName.toString()).readObject(close)
}

private object BtHolder {
    private const val defaultTimestampPatternString: String = "yyyyMMddHHmmssSSS"
    val defaultTimestampPattern: DatePattern = run {
        // JDK8 bug:
        // Error for "yyyyMMddHHmmssSSS".toDatePattern()
        if (isJdk9OrHigher()) {
            return@run defaultTimestampPatternString.toDatePattern()
        }
        val formatter: DateTimeFormatter = DateTimeFormatterBuilder() // date/time
            .appendPattern("yyyyMMddHHmmss") // milliseconds
            .appendValue(ChronoField.MILLI_OF_SECOND, 3) // create formatter
            .toFormatter()
        DatePattern.of(defaultTimestampPatternString, formatter)
    }
}