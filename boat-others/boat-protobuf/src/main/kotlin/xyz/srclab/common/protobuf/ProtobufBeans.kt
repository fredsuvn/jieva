@file:JvmName("ProtobufBeans")

package xyz.srclab.common.protobuf

import xyz.srclab.common.bean.BeanResolver

/**
 * [BeanResolver] supports protobuf types.
 *
 * @see ProtobufBeanResolveHandler
 */
@JvmField
val PROTOBUF_BEAN_RESOLVER: BeanResolver = BeanResolver.DEFAULT.withPreResolveHandler(ProtobufBeanResolveHandler)