package xyz.srclab.common.base

/**
 * This interface is used for java:
 * ```
 * Date date = Let.forAny(intValue)
 *     .then(StringUtils::intToString)
 *     .then(DateUtils::stringToDate)
 *     .get()
 * ```
 * Same as:
 * ```
 * String stringValue = StringUtils.intToString(intValue)
 * Date dateValue = DateUtils.stringToDate(stringValue)
 * ```
 */
interface Let<T> {

    fun <R> then(action: (T) -> R): Let<R>

    /**
     * If current value is null, return without action.
     */
    fun <R> thenOrNull(action: (T) -> R): Let<R>

    fun get(): T

    fun getOrNull(): T?

    companion object {

        @JvmStatic
        fun <T> any(any: T): Let<T> {
            return LetImpl(any)
        }
    }
}

private class LetImpl<T>(private var any: T) : Let<T> {

    override fun <R> then(action: (T) -> R): Let<R> {
        any = action(any).asAny()
        return this.asAny()
    }

    override fun <R> thenOrNull(action: (T) -> R): Let<R> {
        if (any === null) {
            return this.asAny()
        }
        return then(action)
    }

    override fun get(): T {
        return any
    }

    override fun getOrNull(): T? {
        return any
    }
}