package xyz.srclab.common.collect

import xyz.srclab.common.base.DEFAULT_SERIAL_VERSION
import java.io.Serializable

/**
 * Bridge between array and [MutableList].
 */
class ArrayBridgeList<T>(
    private val arrayBridge: ArrayBridge<T>,
) : AbstractMutableList<T>(), RandomAccess, Serializable {

    override val size: Int get() = arrayBridge.size

    override fun isEmpty(): Boolean = arrayBridge.isEmpty()
    override fun contains(element: T): Boolean = arrayBridge.contains(element)
    override fun get(index: Int): T = arrayBridge[index]
    override fun set(index: Int, element: T): T = arrayBridge[index].let { arrayBridge[index] = element;it }
    override fun indexOf(element: T): Int = arrayBridge.indexOf(element)
    override fun lastIndexOf(element: T): Int = arrayBridge.lastIndexOf(element)
    override fun add(index: Int, element: T) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): T = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = DEFAULT_SERIAL_VERSION
    }
}

/**
 * Provides base info for [ArrayBridgeList].
 */
// Fuck un-supporting-primitive-generic-type! DAMN!
interface ArrayBridge<T> : Serializable {
    val size: Int
    fun isEmpty(): Boolean
    fun contains(element: T): Boolean
    operator fun get(index: Int): T
    operator fun set(index: Int, element: T): T
    fun indexOf(element: T): Int
    fun lastIndexOf(element: T): Int
}