@file:JvmName("BLang")

package xyz.srclab.common.base

/**
 * Gets or creates a new value.
 * This function is usually used for operation which checks the value is null, if it is not, return,
 * else create a new one then set and return.
 */
inline fun <T> getOrNew(
    lock: Any,
    getter: () -> T?,
    setter: (T) -> Unit,
    creator: () -> T
): T {
    val current = getter()
    if (current !== null) {
        return current
    }
    synchronized(lock) {
        val newOne = creator()
        setter(newOne)
        return newOne
    }
}

/**
 * Abstract object represents a final object, which will cache the values of [hashCode] and [toString].
 * The subclass should implement [hashCode0] and [toString0] to compute the values of [hashCode] and [toString],
 * each computation will be processed only once.
 */
abstract class FinalObject {

    private var _hashCode: Int? = null
    private var _toString: String? = null

    override fun hashCode(): Int {
        return getOrNew(
            this,
            { this._hashCode },
            { this._hashCode = it },
            { hashCode0() },
        )
    }

    override fun toString(): String {
        return getOrNew(
            this,
            { this._toString },
            { this._toString = it },
            { toString0() },
        )
    }

    /**
     * Computes the hash code.
     */
    protected abstract fun hashCode0(): Int

    /**
     * Computes the toString value.
     */
    protected abstract fun toString0(): String
}