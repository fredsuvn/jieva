package xyz.srclab.common.collect

import java.util.*

/**
 * Collection contains [Set] and [List].
 *
 * @author sunqian
 */
interface Pool<E> : Set<E>, List<E> {

    @JvmDefault
    override fun spliterator(): Spliterator<E> {
        return super<Set>.spliterator()
    }
}

/**
 * Collection contains [MutableSet] and [MutableList].
 *
 * @author sunqian
 */
interface MutablePool<E> : Pool<E>, MutableSet<E>, MutableList<E> {

    @JvmDefault
    override fun spliterator(): Spliterator<E> {
        return super<Pool>.spliterator()
    }
}