package xyz.srclab.common.bean

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanType {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type
        @JvmName("type") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val rawClass: Class<*>
        @JvmName("rawClass") get() = type.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertyType>
        @JvmName("properties") get

    @JvmDefault
    fun getProperty(name: String): PropertyType? {
        return properties[name]
    }
}