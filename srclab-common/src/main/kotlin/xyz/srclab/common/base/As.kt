package xyz.srclab.common.base

/**
 * @author sunqian
 */
object As {

    @JvmStatic
    fun <R> any(any: Any?): R {
        return any as R
    }

    @JvmStatic
    fun <R : Any> notNull(any: Any): R {
        return any as R
    }

    @JvmStatic
    fun <R> nullable(any: Any?): R? {
        return if (any == null) null else any as R?
    }
}