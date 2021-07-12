package xyz.srclab.common.convert

/**
 * Let annotated method to be a fast convert handler.
 *
 * Annotated method must have one parameter -- represents the `from` object, and return a `to` object.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class FastConvertHandler