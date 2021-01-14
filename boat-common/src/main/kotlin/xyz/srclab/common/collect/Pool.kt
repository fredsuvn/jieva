package xyz.srclab.common.collect

/**
 * Collection contains [Set] and [List].
 *
 * @author sunqian
 */
interface Pool<E>: Iterable<E> {


}

/**
 * Collection contains [MutableSet] and [MutableList].
 *
 * @author sunqian
 */
interface MutablePool<E> : Pool<E>