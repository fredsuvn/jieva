package xyz.srclab.common.base

/**
 * @author sunqian
 */
object Require {

    @JvmStatic
    fun <T : Any> notNull(any: T?): T {
        Check.checkNull(any != null)
        return any as T
    }

    @JvmStatic
    fun <T : Any> notNull(any: T?, message: String?): T {
        Check.checkNull(any != null, message)
        return any as T
    }

    @JvmStatic
    fun <T : Any> notNull(any: T?, messagePattern: String?, vararg messageArgs: Any?): T {
        Check.checkNull(any != null, messagePattern, messageArgs)
        return any as T
    }

    @JvmStatic
    fun <T : Any> notNullElement(element: T?): T {
        Check.checkElement(element != null)
        return element as T
    }

    @JvmStatic
    fun <T : Any> notNullElement(element: T?, message: String?): T {
        Check.checkElement(element != null, message)
        return element as T
    }

    @JvmStatic
    fun <T : Any> notNullElement(element: T?, messagePattern: String?, vararg messageArgs: Any?): T {
        Check.checkElement(element != null, messagePattern, messageArgs)
        return element as T
    }

    @JvmStatic
    fun <T : Any> notNullElementByKey(element: T?, key: Any?): T {
        return notNullElement(element, "Key: {}.", key)
    }
}