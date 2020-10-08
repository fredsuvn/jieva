package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object Sort {

    @JvmStatic
    fun <T> selfComparableComparator(): Comparator<T> {
        return SELF_COMPARABLE_COMPARATOR.asAny()
    }

    @JvmStatic
    private val SELF_COMPARABLE_COMPARATOR: Comparator<*> by lazy {
        Comparator { o1: Any?, o2: Any? ->
            val c1: Comparable<Any?> = o1.asAny()
            val c2: Comparable<Any?> = o2.asAny()
            c1.compareTo(c2)
        }
    }
}