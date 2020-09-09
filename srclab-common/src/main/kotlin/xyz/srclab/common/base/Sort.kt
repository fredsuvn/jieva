package xyz.srclab.common.base

import xyz.srclab.common.base.As.notNull
import java.util.*

/**
 * @author sunqian
 */
object Sort {

    @JvmStatic
    val SELF_COMPARABLE_COMPARATOR: Comparator<*> = Comparator { o1: Any?, o2: Any? ->
        val c1 = o1 as Comparable<Any?>
        val c2 = o2 as Comparable<Any?>
        c1.compareTo(c2)
    }

    @JvmStatic
    fun <T> selfComparableComparator(): Comparator<T> {
        return notNull(SELF_COMPARABLE_COMPARATOR)
    }
}