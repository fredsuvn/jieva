package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object Sort {

    @JvmStatic
    fun <T> selfComparableComparator(): Comparator<T> {
        return As.notNull(SELF_COMPARABLE_COMPARATOR)
    }

    @JvmStatic
    private val SELF_COMPARABLE_COMPARATOR: Comparator<*> by lazy {
        Comparator { o1: Any?, o2: Any? ->
            val c1 = o1 as Comparable<Any?>
            val c2 = o2 as Comparable<Any?>
            c1.compareTo(c2)
        }
    }
}