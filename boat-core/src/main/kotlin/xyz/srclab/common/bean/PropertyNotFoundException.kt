package xyz.srclab.common.bean

/**
 * Property nor found exception.
 */
open class PropertyNotFoundException(name: String) : RuntimeException(name)