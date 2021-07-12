package xyz.srclab.common.proxy

/**
 * To invoke current super method.
 */
interface SuperInvoke {

    fun invoke(vararg args: Any?): Any?
}