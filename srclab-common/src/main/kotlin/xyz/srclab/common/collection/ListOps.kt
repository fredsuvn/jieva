package xyz.srclab.common.collection

class ListOps<T> private constructor(list: List<T>) : IterableOps<T>(list) {

    override var inProcess: List<T>? = list
    override var sequence: Sequence<T>? = null
}