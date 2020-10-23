package xyz.srclab.common.bean

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Type

interface BeanWithSchema {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val bean: Any
        @JvmName("bean") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val type: Type
        @JvmName("type") get

    companion object {

        @JvmStatic
        fun of(bean: Any, type: Type): BeanWithSchema {
            return BeanWithSchemaImpl(bean, type)
        }
    }
}

fun Any.beanWithScheme(type: Type): BeanWithSchema {
    return BeanWithSchema.of(this, type)
}

private class BeanWithSchemaImpl(override val bean: Any, override val type: Type) : BeanWithSchema