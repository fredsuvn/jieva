package xyz.srclab.common.id

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME

/**
 *  Id Component, used to create new component value.
 *
 * @param [T] component type
 * @author sunqian
 *
 * @see IdContext
 * @see IdJoiner
 * @see SnowflakeComponent
 */
interface IdComponent<T> {

    /**
     * Type of this component, could be un-unique.
     */
    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: String

    /**
     * Initializes this component. This method will be called once only.
     */
    fun init(args: List<Any>)

    fun newValue(context: IdContext): T
}