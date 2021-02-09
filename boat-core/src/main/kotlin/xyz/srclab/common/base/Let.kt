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
interface Let<T> : Getter<T> {

    fun <R> then(action: (T) -> R): Let<R>

    companion object {

        @JvmStatic
        @JvmName("of")
        fun <T> T.ofLet(): Let<T> {
            return LetImpl(this)
        }
    }
}

private class LetImpl<T>(private var any: T) : Let<T> {

    override fun <R> then(action: (T) -> R): Let<R> {
        any = action(any).asAny()
        return this.asAny()
    }

    override fun get(): T {
        return any
    }

    override fun getOrNull(): T? {
        return any
    }
}