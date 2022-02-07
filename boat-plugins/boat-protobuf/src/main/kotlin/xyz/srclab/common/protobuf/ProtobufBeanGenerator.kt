package xyz.srclab.common.protobuf

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import xyz.srclab.common.convert.BeanConvertHandler
import xyz.srclab.common.reflect.methodOrNull
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type

/**
 * [BeanConvertHandler.BeanGenerator] supports protobuf types.
 *
 * @see BeanConvertHandler
 */
object ProtobufBeanGenerator : BeanConvertHandler.BeanGenerator {

    override fun newBuilder(toType: Type): Any {
        if (toType !is Class<*>) {
            return toType.rawClass.newInstance()
        }
        when {
            //Builder
            Message.Builder::class.java.isAssignableFrom(toType) -> {
                val outClass = toType.declaringClass
                val newBuilderMethod = outClass.methodOrNull("newBuilder")
                if (newBuilderMethod === null) {
                    throw IllegalStateException("Cannot find newBuilder method for $outClass")
                }
                return newBuilderMethod.invoke(null)
            }
            //Message
            MessageOrBuilder::class.java.isAssignableFrom(toType) -> {
                val newBuilderMethod = toType.methodOrNull("newBuilder")
                if (newBuilderMethod === null) {
                    throw IllegalStateException("Cannot find newBuilder method for $toType")
                }
                return newBuilderMethod.invoke(null)
            }
            //Other bean
            else -> return toType.newInstance()
        }
    }

    //override fun builderType(toType: Type): Type {
    //    TODO("Not yet implemented")
    //}
    //
    //override fun builderType(builder: Any, toType: Type): Type {
    //    if (toType !is Class<*>) {
    //        return toType
    //    }
    //    return when {
    //        //Message but not builder
    //        (MessageOrBuilder::class.java.isAssignableFrom(toType)
    //            && !Message.Builder::class.java.isAssignableFrom(toType)
    //            && builder is Message.Builder) -> {
    //            builder.javaClass
    //        }
    //        else -> toType
    //    }
    //}

    override fun build(builder: Any, toType: Type): Any {
        if (toType !is Class<*>) {
            return builder
        }
        return when {
            //Message but not builder
            (MessageOrBuilder::class.java.isAssignableFrom(toType)
                    && !Message.Builder::class.java.isAssignableFrom(toType)
                    && builder is Message.Builder) -> {
                builder.build()
            }
            else -> builder
        }
    }
}