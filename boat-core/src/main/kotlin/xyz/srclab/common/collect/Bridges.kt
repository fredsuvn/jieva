package xyz.srclab.common.collect

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.io.Serializable

/**
 * Represent a bridge between array and object.
 */
// Fuck un-supporting-primitive-generic-type! DAMN!
interface ArrayBridge<T> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @get:JvmName("get")
    val size: Int

    fun get(index: Int): T

    fun set(index: Int, element: T)
}

class ArrayBridgeList<T>(
    private val arrayBridge: ArrayBridge<T>,
    override val size: Int = arrayBridge.size
) : AbstractMutableList<T>(), RandomAccess, Serializable {

    override fun get(index: Int): T {
        return arrayBridge.get(index)
    }

    override fun set(index: Int, element: T): T {
        val oldValue = arrayBridge.get(index)
        arrayBridge.set(index, element)
        return oldValue
    }

    override fun add(index: Int, element: T) {
        throw UnsupportedOperationException()
    }

    override fun removeAt(index: Int): T {
        throw UnsupportedOperationException()
    }

    companion object {
        private const val serialVersionUID = -2764017481108945198L
    }
}