/**
 * Boat base utilities, including common and misc.
 */
@file:JvmName("Bt")

package xyz.srclab.common.base

import xyz.srclab.common.Boat
import xyz.srclab.common.collect.addAll
import xyz.srclab.common.collect.newArray
import xyz.srclab.common.collect.toStringMap
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.defaultConverter
import xyz.srclab.common.io.readString
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.reflect.defaultClassLoader
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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

/**
 * Returns default charset: UTF-8.
 */
fun defaultCharset(): Charset = utf8()

/**
 * Returns default [String] for `null`.
 */
fun defaultNullString(): String = "null"

/**
 * Returns default radix: 10.
 */
fun defaultRadix(): Int = 10

/**
 * Returns default locale: [Locale.ENGLISH].
 */
fun defaultLocale(): Locale = Locale.ENGLISH

/**
 * Returns default concurrency level: [availableProcessors] * 4.
 */
fun defaultConcurrencyLevel(): Int = availableProcessors() * 4

/**
 * Returns default timestamp pattern: yyyyMMddHHmmssSSS.
 */
fun defaultTimestampPattern(): String = "yyyyMMddHHmmssSSS"

/**
 * Returns default buffer size: 8 * 1024.
 */
fun defaultBufferSize(): Int = 8 * 1024

/**
 * Returns default serial version for current boat version.
 */
fun defaultSerialVersion(): Long = Boat.serialVersion()

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
fun <T : Any> notNull(t: T?, defaultValue: T): T {
    return t ?: defaultValue
}

/**
 * Returns [t] if it is not null, or result of [supplier] if [t] is null.
 */
fun <T : Any> notNull(t: T?, supplier: Supplier<out T>): T {
    return t ?: supplier.get()
}

/**
 * Returns result of conversion by [converter] if the result is not null, or [defaultValue] if the result is null.
 */
@JvmOverloads
fun <T : Any> notNull(t: Any?, type: Class<out T>, defaultValue: T, converter: Converter = defaultConverter()): T {
    val result = converter.convert(t, type)
    return result ?: defaultValue
}

/**
 * Returns result of conversion by [converter] if the result is not null, or [defaultValue] if the result is null.
 */
@JvmOverloads
fun <T : Any> notNull(t: Any?, type: Type, defaultValue: T, converter: Converter = defaultConverter()): T {
    val result = converter.convert<T>(t, type)
    return result ?: defaultValue
}

/**
 * Returns result of conversion by [converter] if the result is not null, or [defaultValue] if the result is null.
 */
@JvmOverloads
fun <T : Any> notNull(t: Any?, type: TypeRef<T>, defaultValue: T, converter: Converter = defaultConverter()): T {
    val result = converter.convert(t, type)
    return result ?: defaultValue
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
fun remLength(size: Int, offset: Int): Int = size - offset

/**
 * Returns remaining length of [size] from [offset].
 */
fun remLength(size: Long, offset: Long): Long = size - offset

/**
 * Returns end index exclusive from [offset] through [length].
 * It is equivalent to:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Int, length: Int): Int = offset + length

/**
 * Returns end index exclusive from [offset] through [length].
 * It is equivalent to:
 *
 * ```
 * return offset + length;
 * ```
 */
fun endIndex(offset: Long, length: Long): Long = offset + length

/**
 * Returns count of segment in [segSize].
 * It is equivalent to:
 *
 * ```
 * int div = totalSize / segSize;
 * if (totalSize % segSize == 0) {
 *     return div;
 * }
 * return div + 1;
 * ```
 *
 * @param totalSize total size
 * @param segSize size of segment
 */
fun countSeg(totalSize: Int, segSize: Int): Int {
    val div = totalSize / segSize
    return if (totalSize % segSize == 0) div else div + 1
}

/**
 * Returns count of segment in [segSize].
 * It is equivalent to:
 *
 * ```
 * int div = totalSize / segSize;
 * if (totalSize % segSize == 0) {
 *     return div;
 * }
 * return div + 1;
 * ```
 *
 * @param totalSize total size
 * @param segSize size of segment
 */
fun countSeg(totalSize: Long, segSize: Long): Long {
    val div = totalSize / segSize
    return if (totalSize % segSize == 0L) div else div + 1
}

/**
 * Returns the detailed description of this throwable with its stack trace.
 */
fun Throwable.stackTraceToString(): String {
    return this.stackTraceToStringKt()
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

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun InputStream.readProperties(charset: Charset = defaultCharset(), close: Boolean = false): Map<String, String> {
    return this.reader(charset).readProperties(close)
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun Reader.readProperties(close: Boolean = false): Map<String, String> {
    val props = Properties()
    props.load(this)
    val map = props.toStringMap()
    if (close) {
        this.close()
    }
    return map
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun File.readProperties(charset: Charset = defaultCharset()): Map<String, String> {
    return this.reader(charset).readProperties(true)
}

/**
 * Runs a new process with [command].
 */
fun runProcess(command: String): Process {
    return Runtime.getRuntime().exec(command)
}

/**
 * Runs a new process with [command], directory([dir]) and environments([env]) (name=value format).
 */
fun runProcess(command: String, env: Array<out String>?, dir: File?): Process {
    newArray("", "")
    arrayOf("", "")
    return Runtime.getRuntime().exec(command, env, dir)
}

/**
 * Runs a new process with command array([cmdArray]).
 */
fun runProcess(vararg cmdArray: String): Process {
    return Runtime.getRuntime().exec(cmdArray)
}

/**
 * Runs a new process with command array([cmdArray]), directory([dir]) and environments([env]) (name=value format).
 */
fun runProcess(cmdArray: Array<out String>, env: Array<out String>?, dir: File?): Process {
    return Runtime.getRuntime().exec(cmdArray, env, dir)
}

// Quick way to create array and collection:

/**
 * Creates and returns an array consists of [elements].
 */
fun <T> array(vararg elements: T): Array<T> {
    return elements.asType()
}

/**
 * Collects [elements] into [dest] and returns the [dest].
 */
fun <T, C : MutableCollection<T>> collect(dest: C, vararg elements: T): C {
    dest.addAll(elements)
    return dest
}

/**
 * Collects [elements] into [dest] and returns the [dest].
 *
 * The [elements] will be split in two elements as `key-value-pairs`, that is:
 * the first element as `key1`, the second as `value1`, the third as `key2`, the fourth as `value2`, and so on.
 * Then, all pairs will be put into [dest] in encounter order.
 *
 * If the last pair only has a key (if length of [elements] is odd number), its value will be seen as `null`.
 */
fun <K, V, C : MutableMap<K, V>> collectMap(dest: C, vararg elements: Any?): C {
    val map = dest.asType<MutableMap<Any?, Any?>>()
    if (elements.size % 2 == 0) {
        var i = 0
        while (i < elements.size) {
            val key = elements[i++]
            val value = elements[i++]
            map[key] = value
        }
    } else {
        var i = 0
        while (i < elements.size - 1) {
            val key = elements[i++]
            val value = elements[i++]
            map[key] = value
        }
        map[elements[i]] = null
    }
    return dest
}

/**
 * Creates and returns a readonly [List] consists of [elements].
 */
fun <T> list(vararg elements: T): List<T> {
    return listOf(*elements)
}

/**
 * Creates and returns an [ArrayList] consists of [elements].
 */
fun <T> arrayList(vararg elements: T): ArrayList<T> {
    return collect(ArrayList(elements.size), *elements)
}

/**
 * Creates and returns a [LinkedList] consists of [elements].
 */
fun <T> linkedList(vararg elements: T): LinkedList<T> {
    return collect(LinkedList(), *elements)
}

/**
 * Creates and returns a readonly [Set] consists of [elements].
 */
fun <T> set(vararg elements: T): Set<T> {
    return setOf(*elements)
}

/**
 * Creates and returns a [HashSet] consists of [elements].
 */
fun <T> hashSet(vararg elements: T): HashSet<T> {
    return collect(HashSet(elements.size), *elements)
}

/**
 * Creates and returns a [LinkedHashSet] consists of [elements].
 */
fun <T> linkedHashSet(vararg elements: T): LinkedHashSet<T> {
    return collect(LinkedHashSet(elements.size), *elements)
}

/**
 * Creates and returns a readonly [Map] consists of [elements] by [collectMap].
 */
fun <K, V> map(vararg elements: Any?): Map<K, V> {
    return linkedHashMap(*elements)
}

/**
 * Creates and returns a [HashMap] consists of [elements] by [collectMap].
 */
fun <K, V> hashMap(vararg elements: Any?): HashMap<K, V> {
    return collectMap(HashMap(countSeg(elements.size, 2)), *elements)
}

/**
 * Creates and returns a [LinkedHashMap] consists of [elements] by [collectMap].
 */
fun <K, V> linkedHashMap(vararg elements: Any?): LinkedHashMap<K, V> {
    return collectMap(LinkedHashMap(countSeg(elements.size, 2)), *elements)
}

/**
 * Creates and returns a [ConcurrentHashMap] consists of [elements] by [collectMap].
 */
fun <K, V> concurrentHashMap(vararg elements: Any?): ConcurrentHashMap<K, V> {
    return collectMap(ConcurrentHashMap(countSeg(elements.size, 2)), *elements)
}