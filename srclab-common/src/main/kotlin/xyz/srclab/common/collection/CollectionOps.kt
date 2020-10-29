package xyz.srclab.common.collection

import xyz.srclab.common.base.*
import kotlin.collections.addAll as addAllKt
import kotlin.collections.count as countKt
import kotlin.collections.plus as plusKt
import kotlin.collections.removeAll as removeAllKt
import kotlin.collections.retainAll as retainAllKt
import kotlin.collections.toMutableList as toMutableListKt
import kotlin.collections.toSet as toSetKt
import kotlin.collections.toTypedArray as toTypedArrayKt

/**
 * @author sunqian
 */
abstract class CollectionOps<T, C : Collection<T>, MC : MutableCollection<T>, THIS : CollectionOps<T, C, MC, THIS>>
protected constructor(collection: C) : BaseIterableOps<T, C, MC, THIS>(collection) {

    override fun containsAll(elements: Array<out T>): Boolean {
        return finalCollection().containsAll(elements)
    }

    override fun containsAll(elements: Iterable<T>): Boolean {
        return finalCollection().containsAll(elements)
    }

    override fun count(): Int {
        return finalCollection().count()
    }

    override fun toMutableList(): MutableList<T> {
        return finalCollection().toMutableList()
    }

    @JvmOverloads
    override fun asToList(supplier: () -> MutableList<T> = { ArrayList(count()) }): List<T> {
        return finalCollection().asToList(supplier)
    }

    @JvmOverloads
    override fun asToMutableList(supplier: () -> MutableList<T> = { ArrayList(count()) }): MutableList<T> {
        return finalCollection().asToMutableList(supplier)
    }

    open fun toArray(): Array<Any?> {
        return finalCollection().toArray()
    }

    open fun toArray(generator: (size: Int) -> Array<T>): Array<T> {
        return finalCollection().toArray(generator)
    }

    open fun toArray(componentType: Class<*>): Array<T> {
        return finalCollection().toArray(componentType)
    }

    open fun toAnyArray(componentType: Class<*>): Any {
        return finalCollection().toAnyArray(componentType)
    }

    @JvmOverloads
    open fun toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
        return finalCollection().toBooleanArray(selector)
    }

    @JvmOverloads
    open fun toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
        return finalCollection().toByteArray(selector)
    }

    @JvmOverloads
    open fun toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
        return finalCollection().toShortArray(selector)
    }

    @JvmOverloads
    open fun toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
        return finalCollection().toCharArray(selector)
    }

    @JvmOverloads
    open fun toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
        return finalCollection().toIntArray(selector)
    }

    @JvmOverloads
    open fun toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
        return finalCollection().toLongArray(selector)
    }

    @JvmOverloads
    open fun toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
        return finalCollection().toFloatArray(selector)
    }

    @JvmOverloads
    open fun toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
        return finalCollection().toDoubleArray(selector)
    }

    open fun addAll(elements: Array<out T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun addAll(elements: Iterable<T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun addAll(elements: Sequence<T>): THIS {
        finalMutableCollection().addAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Array<out T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Iterable<T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun removeAll(elements: Sequence<T>): THIS {
        finalMutableCollection().removeAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Array<out T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Iterable<T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun retainAll(elements: Sequence<T>): THIS {
        finalMutableCollection().retainAll(elements)
        return this.asAny()
    }

    open fun clear(): THIS {
        finalMutableCollection().clear()
        return this.asAny()
    }

    open fun finalCollection(): Collection<T> {
        return iterable
    }

    open fun finalMutableCollection(): MutableCollection<T> {
        return iterable.asAny()
    }

    companion object {

        @JvmStatic
        fun <T> Collection<T>.containsAll(elements: Array<out T>): Boolean {
            return this.containsAll(elements.toSetKt())
        }

        @JvmStatic
        fun <T> Collection<T>.containsAll(elements: Iterable<T>): Boolean {
            return this.containsAll(elements.asToSet())
        }

        @JvmStatic
        fun <T> Collection<T>.count(): Int {
            return this.countKt()
        }

        @JvmStatic
        fun <T> Collection<T>.toMutableList(): MutableList<T> {
            return this.toMutableListKt()
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.asToList(supplier: () -> MutableList<T> = { ArrayList(count()) }): List<T> {
            return if (this is List<T>) this else toCollection(supplier())
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.asToMutableList(supplier: () -> MutableList<T> = { ArrayList(count()) }): MutableList<T> {
            return if (this is MutableList<T>) this else toCollection(supplier())
        }

        @JvmStatic
        fun <T> Collection<T>.toArray(): Array<Any?> {
            val list = this.asToList { ArrayList(count()) }
            return JavaCollectionOps.toArray(list)
        }

        @JvmStatic
        fun <T> Collection<T>.toArray(generator: (size: Int) -> Array<T>): Array<T> {
            val list = this.asToList { ArrayList(count()) }
            return JavaCollectionOps.toArray(list, generator(list.size))
        }

        @JvmStatic
        fun <T> Collection<T>.toArray(componentType: Class<*>): Array<T> {
            val list = this.asToList { ArrayList(count()) }
            val array: Array<T> = componentType.componentTypeToArray(0)
            return JavaCollectionOps.toArray(list, array)
        }

        @JvmStatic
        fun <T> Collection<T>.toAnyArray(componentType: Class<*>): Any {
            return when (componentType) {
                Boolean::class.javaPrimitiveType -> this.toBooleanArray()
                Byte::class.javaPrimitiveType -> this.toByteArray()
                Short::class.javaPrimitiveType -> this.toShortArray()
                Char::class.javaPrimitiveType -> this.toCharArray()
                Int::class.javaPrimitiveType -> this.toIntArray()
                Long::class.javaPrimitiveType -> this.toLongArray()
                Float::class.javaPrimitiveType -> this.toFloatArray()
                Double::class.javaPrimitiveType -> this.toDoubleArray()
                else -> this.toArray(componentType)
            }
        }

        @JvmStatic
        inline fun <reified T> Collection<T>.toTypedArray(): Array<T> {
            val list = this.asToList()
            return list.toTypedArrayKt()
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toBooleanArray(selector: (T) -> Boolean = { it.toBoolean() }): BooleanArray {
            val list = this.asToList()
            val result = BooleanArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toByteArray(selector: (T) -> Byte = { it.toByte() }): ByteArray {
            val list = this.asToList()
            val result = ByteArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toShortArray(selector: (T) -> Short = { it.toShort() }): ShortArray {
            val list = this.asToList()
            val result = ShortArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toCharArray(selector: (T) -> Char = { it.toChar() }): CharArray {
            val list = this.asToList()
            val result = CharArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toIntArray(selector: (T) -> Int = { it.toInt() }): IntArray {
            val list = this.asToList()
            val result = IntArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toLongArray(selector: (T) -> Long = { it.toLong() }): LongArray {
            val list = this.asToList()
            val result = LongArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toFloatArray(selector: (T) -> Float = { it.toFloat() }): FloatArray {
            val list = this.asToList()
            val result = FloatArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        @JvmOverloads
        inline fun <T> Collection<T>.toDoubleArray(selector: (T) -> Double = { it.toDouble() }): DoubleArray {
            val list = this.asToList()
            val result = DoubleArray(list.size)
            list.forEachIndexed { i, t -> result[i] = selector(t) }
            return result
        }

        @JvmStatic
        fun <T> Collection<T>.plus(element: T): List<T> {
            return this.plusKt(element)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Array<out T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> Collection<T>.plus(elements: Sequence<T>): List<T> {
            return this.plusKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Array<out T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Iterable<T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.addAll(elements: Sequence<T>): Boolean {
            return this.addAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Array<out T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Iterable<T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.removeAll(elements: Sequence<T>): Boolean {
            return this.removeAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Array<out T>): Boolean {
            return this.retainAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Iterable<T>): Boolean {
            return this.retainAllKt(elements)
        }

        @JvmStatic
        fun <T> MutableCollection<T>.retainAll(elements: Sequence<T>): Boolean {
            return this.retainAllKt(elements)
        }
    }
}