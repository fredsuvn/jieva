@file:JvmName("ProtobufConverts")

package xyz.srclab.common.protobuf

import xyz.srclab.common.convert.BeanConvertHandler
import xyz.srclab.common.convert.ConvertHandler
import xyz.srclab.common.convert.Converter

/**
 * Converter supports protobuf types.
 *
 * @see ProtobufBeanGenerator
 */
@JvmField
val PROTOBUF_CONVERTER: Converter = Converter.newConverter(
    ConvertHandler.defaultsWithBeanConvertHandler(
        BeanConvertHandler(ProtobufBeanGenerator, PROTOBUF_BEAN_RESOLVER)
    )
)