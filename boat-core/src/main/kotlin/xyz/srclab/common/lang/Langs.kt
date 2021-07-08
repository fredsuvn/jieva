package xyz.srclab.common.lang

/**
 * Convenient interface for java:
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
interface Let<T : Any> : GenericSingleGetter<T> {

    fun <R : Any> then(action: (T) -> R): Let<R>

    companion object {

        @JvmStatic
        fun <T : Any> of(obj: T): Let<T> {
            return LetImpl(obj)
        }

        private class LetImpl<T : Any>(private var any: T) : Let<T> {

            override fun <R : Any> then(action: (T) -> R): Let<R> {
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
    }
}

/**
 * Represents next operation, usually used for the object which delegate performance to a group of handlers.
 *
 * If one of handlers returns [CONTINUE], means that handler failed to convert and suggests continue to next handler;
 * if returns [BREAK], means that handler failed to convert and suggests break handler chain.
 */
enum class Next {

    /**
     * Represents current handler failed to convert and suggests continue to next handler.
     */
    CONTINUE,

    /**
     * Represents current handler failed to convert and suggests break handler chain.
     */
    BREAK,
}