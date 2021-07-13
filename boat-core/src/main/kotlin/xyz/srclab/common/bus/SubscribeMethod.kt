package xyz.srclab.common.bus

import xyz.srclab.common.lang.Next

/**
 * Let annotated method to be an event subscribe method as `subscriber`.
 *
 * Annotated method must have only one parameter -- represents the event object.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class SubscribeMethod(

    /**
     * Priority of current event `subscriber`.
     *
     * Higher priority subscriber will receive the event first, and if it returns [Next.BREAK], event wil be dropped and
     * lower subscriber will not receive.
     *
     * Default is 0.
     */
    val priority: Int = 0
)