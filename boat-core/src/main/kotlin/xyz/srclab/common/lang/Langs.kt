package xyz.srclab.common.lang

import xyz.srclab.common.lang.Next.*

/**
 * Convenient interface for java:
 *
 * ```
 * Date date = Let.of(intValue)
 *     .then(StringUtils::intToString)
 *     .then(DateUtils::stringToDate)
 *     .get()
 * ```
 *
 * Same as:
 *
 * ```
 * String stringValue = StringUtils.intToString(intValue)
 * Date dateValue = DateUtils.stringToDate(stringValue)
 * ```
 */
interface Let<T : Any> : GenericGetter<T> {

    fun <R : Any> then(action: (T) -> R): Let<R>

    companion object {

        @JvmStatic
        fun <T : Any> of(obj: T): Let<T> {
            return LetImpl(obj)
        }

        private class LetImpl<T : Any>(obj: T) : Let<T> {

            private var value: Any = obj

            override fun <R : Any> then(action: (T) -> R): Let<R> {
                val t: T = value.asAny()
                value = action(t)
                return this.asAny()
            }

            override fun get(): T {
                return value.asAny()
            }

            override fun getOrNull(): T? {
                return value.asAny()
            }
        }
    }
}

/**
 * Represents next operation, usually used for the object which delegate performance to a group of handlers.
 *
 * For three values:
 *
 * * If one of handlers returns [CONTINUE], means that handler failed to convert and suggests continue to next handler;
 * * If returns [BREAK], means that handler failed to convert and suggests break handler chain;
 * * If returns [COMPLETE], means that handler success and complete conversation;
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

    /**
     * Represents current handler success and complete conversation.
     */
    COMPLETE,
}