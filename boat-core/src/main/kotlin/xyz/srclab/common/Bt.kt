/**
 * Base and core utilities, provides convenient and common functions.
 */
@file:JvmName("Bt")

package xyz.srclab.common

import xyz.srclab.common.base.*
import xyz.srclab.common.collect.toStringMap
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.collections.joinTo as joinToKt
import kotlin.toString as toStringKt

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
fun Any?.equalsAny(vararg others: Any?): Boolean {
    return anyPredicate({ obj -> this.equals(obj) }, *others)
}

/**
 * Returns true if [this] equals each of [others].
 */
fun Any?.equalsAll(vararg others: Any?): Boolean {
    return allPredicate({ obj -> this.equals(obj) }, *others)
}

/**
 * Returns true if they are equal to each other.
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
 * Returns true if they are deep equal to each other.
 */
fun deepEquals(vararg args: Any?): Boolean {
    for (i in 0..args.size - 2) {
        if (!Objects.deepEquals(args[i], args[i + 1])) {
            return false
        }
    }
    return true
}

/**
 * Returns true if [this] is same with (===) any of [others].
 */
fun Any?.sameAny(vararg others: Any?): Boolean {
    return anyPredicate({ obj -> this === obj }, *others)
}

/**
 * Returns true if [this] is same with (===) each of [others].
 */
fun Any?.sameAll(vararg others: Any?): Boolean {
    return allPredicate({ obj -> this === obj }, *others)
}

/**
 * Returns true if they are same with (===) each other.
 */
fun same(vararg args: Any?): Boolean {
    for (i in 0..args.size - 2) {
        if (args[i] !== args[i + 1]) {
            return false
        }
    }
    return true
}

/**
 * Returns hash code of [this].
 */
fun Any?.toHash(): Int {
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
        else -> toHash()
    }
}

/**
 * Returns hash code of [this],
 * or array hash code if this is an array,
 * or deep array hash code if this is a deep array.
 */
fun Any?.deepToHash(): Int {
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
        else -> toHash()
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
 * Returns `toString` of [this], or `Arrays.toString` if this is an array.
 */
fun Any?.arrayString(): String {
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
 * or `Arrays.toString` if this is a primitive array,
 * or `Arrays.deepToString` if this is an object array.
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
 * Returns `toString` of given [args] by [arrayString].
 */
fun toString(vararg args: Any?): String {
    return args.arrayString()
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
 * Joints array or iterable to string.
 */
@JvmOverloads
fun Any.joinToString(separator: String = ", ", transform: JavaFunction<Any?, out CharSequence>? = null): String {
    return joinToString(separator, -1, "...", transform)
}

/**
 * Joints array or iterable to string.
 */
fun Any.joinToString(
    separator: String,
    limit: Int,
    truncated: CharSequence,
    transform: JavaFunction<Any?, out CharSequence>? = null
): String {
    return joinToString(StringAppender(), separator, limit, truncated, transform).toString()
}

/**
 * Joints array or iterable to string.
 */
@JvmOverloads
fun <A : Appendable> Any.joinToString(
    dest: A, separator: String = ", ", transform: JavaFunction<Any?, out CharSequence>? = null): A {
    return joinToString(dest, separator, -1, "...", transform)
}

/**
 * Joints array or iterable to string.
 */
fun <A : Appendable> Any.joinToString(
    dest: A,
    separator: String,
    limit: Int,
    truncated: CharSequence,
    transform: JavaFunction<Any?, out CharSequence>? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is BooleanArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ByteArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ShortArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is CharArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is IntArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is LongArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is FloatArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is DoubleArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is Iterable<*> -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        else -> throw IllegalArgumentException("Must be array or iterable: ${this.javaClass}")
    }
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

// Simple calculation for parameters.

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

// Parsing properties:

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun InputStream.readProperties(charset: Charset = BtProps.charset(), close: Boolean = false): Map<String, String> {
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
fun File.readProperties(charset: Charset = BtProps.charset()): Map<String, String> {
    return this.reader(charset).readProperties(true)
}