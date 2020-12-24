package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.base.Invoker
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Field
import java.lang.reflect.Type

/**
 * @author sunqian
 */
interface BeanSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val type: Class<*>
        @JvmName("type") get() = genericType.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    val genericType: Type
        @JvmName("genericType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, PropertySchema>
        @JvmName("properties") get

    @JvmDefault
    fun getProperty(name: String): PropertySchema? {
        return properties[name]
    }

    companion object {

        @JvmStatic
        fun newBeanSchema(genericType: Type, properties: Map<String, PropertySchema>): BeanSchema {
            return BeanSchemaImpl(genericType, properties)
        }

        private class BeanSchemaImpl(
            override val genericType: Type,
            override val properties: Map<String, PropertySchema>,
        ) : BeanSchema {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is BeanSchema) return false
                if (genericType != other.genericType) return false
                if (properties != other.properties) return false
                return true
            }

            override fun hashCode(): Int {
                var result = genericType.hashCode()
                result = 31 * result + properties.hashCode()
                return result
            }

            override fun toString(): String {
                return genericType.typeName
            }
        }
    }
}

interface PropertySchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val type: Class<*>
        @JvmName("type") get() = genericType.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    val genericType: Type
        @JvmName("genericType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val ownerType: Class<*>
        @JvmName("ownerType") get() = genericOwnerType.rawClass

    @Suppress(INAPPLICABLE_JVM_NAME)
    val genericOwnerType: Type
        @JvmName("genericOwnerType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isReadable: Boolean
        @JvmName("isReadable") get() {
            return getter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isWriteable: Boolean
        @JvmName("isWriteable") get() {
            return setter !== null
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val getter: Invoker?
        @JvmName("getter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val setter: Invoker?
        @JvmName("setter") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val field: Field?
        @JvmName("field") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val fieldAnnotations: List<Annotation>
        @JvmName("fieldAnnotations") get

    fun <T> getValue(bean: Any): T

    fun <T> setValue(bean: Any, value: Any?): T
}