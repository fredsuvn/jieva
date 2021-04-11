@file:JvmName("ProtobufConverts")

package xyz.srclab.common.protobuf

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import xyz.srclab.common.bean.BeanCopyOptions
import xyz.srclab.common.convert.BeanConvertHandler
import xyz.srclab.common.convert.ConvertHandler
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.reflect.methodOrNull
import xyz.srclab.common.reflect.toInstance

private val protobufBuilderGenerator: (Class<*>) -> Any = { it ->
    when {
        //Builder
        Message.Builder::class.java.isAssignableFrom(it) -> {
            val outClass = it.declaringClass
            val newBuilderMethod = outClass.methodOrNull("newBuilder")
            if (newBuilderMethod === null) {
                throw IllegalStateException("Cannot find newBuilder method for $outClass")
            }
            newBuilderMethod.invoke(null)
        }
        //Message
        (MessageOrBuilder::class.java.isAssignableFrom(it)) -> {
            val newBuilderMethod = it.methodOrNull("newBuilder")
            if (newBuilderMethod === null) {
                throw IllegalStateException("Cannot find newBuilder method for $it")
            }
            newBuilderMethod.invoke(null)
        }
        //Other bean
        else -> it.toInstance()
    }
}

private val protobufBuildFunction: (builder: Any, toType: Class<*>) -> Any = { builder, toType ->
    when {
        //Message but not builder
        (MessageOrBuilder::class.java.isAssignableFrom(toType)
                && !Message.Builder::class.java.isAssignableFrom(toType)
                && builder is Message.Builder) -> {
            builder.build()
        }
        else -> builder
    }
}

/**
 * [BeanCopyOptions] for [BeanConvertHandler] which supports protobuf object.
 */
@JvmField
val PROTOBUF_CONVERT_BEAN_COPY_OPTIONS: BeanCopyOptions = BeanCopyOptions.DEFAULT
    .toBuilder()
    .beanResolver(PROTOBUF_BEAN_RESOLVER)
    .build()

@JvmField
val PROTOBUF_CONVERTER: Converter = Converter.newConverter(
    ConvertHandler.defaultsWithBeanConvertHandler(
        BeanConvertHandler(PROTOBUF_CONVERT_BEAN_COPY_OPTIONS, protobufBuilderGenerator, protobufBuildFunction)
    )
)