package xyz.srclab.common.bean

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Type

/**
 * Represents a bean type.
 *
 * @author sunqian
 *
 * @see PropertyType
 */
interface BeanType {

    @get:JvmName("type")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type

    @get:JvmName("properties")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertyType>

    @Throws(PropertyNotFoundException::class)
    fun getProperty(name: CharSequence): PropertyType {
        val nameString = name.toString()
        return properties[nameString] ?: throw PropertyNotFoundException(nameString)
    }
}