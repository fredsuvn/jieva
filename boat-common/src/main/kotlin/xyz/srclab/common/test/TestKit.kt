@file:JvmName("TestKit")
@file:JvmMultifileClass

package xyz.srclab.common.test

import xyz.srclab.common.collection.BaseIterableOps.Companion.asToList

fun equalsIgnoreOrder(a: Iterable<*>, b: Iterable<*>): Boolean {
    val ca = a.asToList()
    val cb = b.asToList()
    return ca.containsAll(cb) && cb.containsAll(ca)
}