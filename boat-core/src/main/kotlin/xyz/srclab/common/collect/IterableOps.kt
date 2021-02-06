package xyz.srclab.common.collect

import xyz.srclab.common.base.asAny

class IterableOps<T>(iterable: Iterable<T>) :
    BaseIterableOps<T, Iterable<T>, MutableIterable<T>, IterableOps<T>>(iterable) {

    override fun <T> Iterable<T>.toIterableOps(): IterableOps<T> {
        operated = this.asAny()
        return this@IterableOps.asAny()
    }

    override fun plus(element: T): IterableOps<T> {
        return finalIterable().plus(element).toIterableOps()
    }

    override fun plus(elements: Array<out T>): IterableOps<T> {
        return finalIterable().plus(elements).toIterableOps()
    }

    override fun plus(elements: Iterable<T>): IterableOps<T> {
        return finalIterable().plus(elements).toIterableOps()
    }

    override fun plus(elements: Sequence<T>): IterableOps<T> {
        return finalIterable().plus(elements).toIterableOps()
    }

    override fun minus(element: T): IterableOps<T> {
        return finalIterable().minus(element).toIterableOps()
    }

    override fun minus(elements: Array<out T>): IterableOps<T> {
        return finalIterable().minus(elements).toIterableOps()
    }

    override fun minus(elements: Iterable<T>): IterableOps<T> {
        return finalIterable().minus(elements).toIterableOps()
    }

    override fun minus(elements: Sequence<T>): IterableOps<T> {
        return finalIterable().minus(elements).toIterableOps()
    }

    companion object {

        @JvmStatic
        fun <T> opsFor(iterable: Iterable<T>): IterableOps<T> {
            return IterableOps(iterable)
        }

        @JvmStatic
        fun <T> opsFor(array: Array<T>): IterableOps<T> {
            return opsFor(array.asList())
        }
    }
}