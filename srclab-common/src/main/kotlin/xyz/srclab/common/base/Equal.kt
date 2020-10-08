package xyz.srclab.common.base

/**
 * @author sunqian
 */
object Equal {

    @JvmStatic
    fun equals(a: Any?, b: Any?): Boolean {
        return a == b
    }

    @JvmStatic
    fun deepEquals(a: Any?, b: Any?): Boolean {
        if (a === b) {
            return true
        }
        if (a == null || b == null) {
            return false
        }
        return deepEquals0(a, b)
    }

    @JvmStatic
    fun deepEquals(a: Array<*>, b: Array<*>): Boolean {
        return a.contentDeepEquals(b)
    }

    @JvmStatic
    fun deepEquals(a: List<*>, b: List<*>): Boolean {
        return if (a === b) {
            true
        } else listDeepEquals(a, b)
    }

    @JvmStatic
    fun deepEquals(a: Set<*>, b: Set<*>): Boolean {
        return if (a === b) {
            true
        } else setDeepEquals(a, b)
    }

    @JvmStatic
    fun deepEquals(a: Collection<*>, b: Collection<*>): Boolean {
        return if (a === b) {
            true
        } else collectionDeepEquals(a, b)
    }

    @JvmStatic
    fun deepEquals(a: Iterable<*>, b: Iterable<*>): Boolean {
        return if (a === b) {
            true
        } else iterableDeepEquals(a, b)
    }

    @JvmStatic
    fun deepEquals(a: Map<*, *>, b: Map<*, *>): Boolean {
        return if (a === b) {
            true
        } else mapDeepEquals(a, b)
    }

    private fun deepEquals0(a: Any, b: Any): Boolean {
        if (a is Array<*> && b is Array<*>) {
            return deepEquals(a, b)
        }
        if (a is List<*> && b is List<*>) {
            return listDeepEquals(a, b)
        }
        if (a is Set<*> && b is Set<*>) {
            return setDeepEquals(a, b)
        }
        if (a is Map<*, *> && b is Map<*, *>) {
            return mapDeepEquals(a, b)
        }
        if (a is Collection<*> && b is Collection<*>) {
            return collectionDeepEquals(a, b)
        }
        if (a is Collection<*> && b is Collection<*>) {
            return collectionDeepEquals(a, b)
        }
        if (a is Iterable<*> && b is Iterable<*>) {
            return iterableDeepEquals(a, b)
        }
        if (a is ByteArray && b is ByteArray) {
            return a.contentEquals(b)
        }
        if (a is ShortArray && b is ShortArray) {
            return a.contentEquals(b)
        }
        if (a is CharArray && b is CharArray) {
            return a.contentEquals(b)
        }
        if (a is IntArray && b is IntArray) {
            return a.contentEquals(b)
        }
        if (a is LongArray && b is LongArray) {
            return a.contentEquals(b)
        }
        if (a is FloatArray && b is FloatArray) {
            return a.contentEquals(b)
        }
        if (a is DoubleArray && b is DoubleArray) {
            return a.contentEquals(b)
        }
        return a == b
    }

    private fun listDeepEquals(a: List<*>, b: List<*>): Boolean {
        return collectionDeepEquals0(a, b)
    }

    private fun setDeepEquals(a: Set<*>, b: Set<*>): Boolean {
        return collectionDeepEquals0(a, b)
    }

    private fun collectionDeepEquals(a: Collection<*>, b: Collection<*>): Boolean {
        return collectionDeepEquals0(a, b)
    }

    private fun iterableDeepEquals(a: Iterable<*>, b: Iterable<*>): Boolean {
        return iterableDeepEquals0(a, b)
    }

    private fun collectionDeepEquals0(a: Collection<*>, b: Collection<*>): Boolean {
        if (a.size != b.size) {
            return false
        }
        return if (a.isEmpty()) {
            true
        } else iterableDeepEquals0(a, b)
    }

    private fun iterableDeepEquals0(a: Iterable<*>, b: Iterable<*>): Boolean {
        val iteratorA = a.iterator()
        val iteratorB = b.iterator()
        while (iteratorA.hasNext()) {
            if (!deepEquals(iteratorA.next(), iteratorB.next())) {
                return false
            }
        }
        return true
    }

    private fun mapDeepEquals(a: Map<*, *>, b: Map<*, *>): Boolean {
        if (a.size != b.size) {
            return false
        }
        if (a.isEmpty()) {
            return true
        }
        val iteratorA = a.entries.iterator()
        val iteratorB = b.entries.iterator()
        while (iteratorA.hasNext()) {
            val entryA = iteratorA.next()
            val entryB = iteratorB.next()
            if (!deepEquals(entryA.key, entryB.key)
                || !deepEquals(entryA.value, entryB.value)
            ) {
                return false
            }
        }
        return true
    }
}