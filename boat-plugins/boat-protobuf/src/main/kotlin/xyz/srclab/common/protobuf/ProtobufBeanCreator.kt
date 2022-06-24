package xyz.srclab.common.protobuf

import com.google.protobuf.Message
import com.google.protobuf.MessageOrBuilder
import xyz.srclab.common.base.asType
import xyz.srclab.common.bean.BeanCreator
import xyz.srclab.common.reflect.methodOrNull

/**
 * [BeanCreator] for protobuf types.
 *
 * @see BeanCreator
 */
object ProtobufBeanCreator : BeanCreator {

    override fun <T> newBuilder(type: Class<T>): T {
        return when {
            //Builder
            Message.Builder::class.java.isAssignableFrom(type) -> {
                val outClass = type.declaringClass
                val newBuilderMethod = outClass.methodOrNull("newBuilder")
                if (newBuilderMethod === null) {
                    throw IllegalStateException("Cannot find newBuilder method for $outClass")
                }
                newBuilderMethod.invoke(null).asType()
            }
            //Message
            MessageOrBuilder::class.java.isAssignableFrom(type) -> {
                val newBuilderMethod = type.methodOrNull("newBuilder")
                if (newBuilderMethod === null) {
                    throw IllegalStateException("Cannot find newBuilder method for $type")
                }
                newBuilderMethod.invoke(null).asType()
            }
            //Other bean
            else -> type.newInstance()
        }
    }

    //override fun build(builder: Any, toType: Type): Any {
    //    if (toType !is Class<*>) {
    //        return builder
    //    }
    //    return when {
    //        //Message but not builder
    //        (MessageOrBuilder::class.java.isAssignableFrom(toType)
    //                && !Message.Builder::class.java.isAssignableFrom(toType)
    //                && builder is Message.Builder) -> {
    //            builder.build()
    //        }
    //        else -> builder
    //    }
    //}

    override fun <T, R> build(builder: T): R {
        if (builder === null) {
            return null.asType()
        }
        if (builder is Message.Builder) {
            return builder.build().asType()
        }
        return builder.asType()
        //val builderType = builder.javaClass
        //return when {
        //    //Message but not builder
        //    (MessageOrBuilder::class.java.isAssignableFrom(builderType)
        //        && !Message.Builder::class.java.isAssignableFrom(builderType)
        //        && builder is Message.Builder) -> {
        //        builder.build()
        //    }
        //    else -> builder
        //}
    }
}