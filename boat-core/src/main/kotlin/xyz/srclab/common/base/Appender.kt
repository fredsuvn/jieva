package xyz.srclab.common.base

/**
 * Appender interface to append same-type object into one.
 *
 * @param A type of this appender
 * @param T type of appendable object
 */
interface Appender<A, T> {

    /**
     * Appends and returns this.
     */
    fun append(t: T): A
}

/**
 * [Appender] of which param [T] can be appended in segments.
 */
interface SegAppender<A, T> : Appender<A, T> {

    /**
     * Appends in segments and returns this.
     */
    fun append(t: T, start: Int): A

    /**
     * Appends in segments and returns this.
     */
    fun append(t: T, start: Int, end: Int): A
}