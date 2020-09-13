package xyz.srclab.common.base

/**
 * @author sunqian
 */
object As {

    @JvmStatic
    fun <T> any(any: Any?): T {
        return any as T
    }

    @JvmStatic
    fun <T : Any> notNull(any: Any): T {
        return any as T
    }

    @JvmStatic
    fun <T> nullable(any: Any?): T? {
        return if (any == null) null else any as T?
    }
}