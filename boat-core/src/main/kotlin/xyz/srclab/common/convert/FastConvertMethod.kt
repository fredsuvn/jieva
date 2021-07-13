package xyz.srclab.common.convert

/**
 * Let annotated method to be a fast convert method.
 *
 * Annotated method must have one parameter -- represents the `fromType` object, and return a `toType` object.
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class FastConvertMethod