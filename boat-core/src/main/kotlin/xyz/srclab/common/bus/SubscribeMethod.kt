package xyz.srclab.common.bus

/**
 * Let annotated method to be an event subscribe method as `subscriber`.
 * Annotated method must have only one parameter -- represents the event object.
 *
 * @see EventBus
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class SubscribeMethod