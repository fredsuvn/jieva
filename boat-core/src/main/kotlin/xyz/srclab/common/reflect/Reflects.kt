@file:JvmName("Reflects")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import xyz.srclab.common.collect.sort
import xyz.srclab.common.collect.sorted
import xyz.srclab.common.lang.asAny

//Inheritance sort:

/**
 * The subclass comes before the parent.
 */
@get:JvmName("inheritanceComparator")
val INHERITANCE_COMPARATOR: Comparator<Class<*>> = Comparator { c1, c2 ->
    if (c1 == c2) 0 else if (c1.isAssignableFrom(c2)) 1 else if (c2.isAssignableFrom(c1)) -1 else 0
}

/**
 * Use [INHERITANCE_COMPARATOR] to sort the classes
 */
fun <T> Iterable<Class<*>>.inheritanceSorted(): List<Class<T>> {
    return this.sorted(INHERITANCE_COMPARATOR).asAny()
}

/**
 * Use [INHERITANCE_COMPARATOR] to sort the classes
 */
fun <C : MutableList<Class<*>>> C.sortInheritance(): C {
    this.sort(INHERITANCE_COMPARATOR)
    return this
}