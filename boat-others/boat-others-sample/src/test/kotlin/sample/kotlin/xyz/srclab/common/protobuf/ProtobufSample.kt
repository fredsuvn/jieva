package sample.kotlin.xyz.srclab.common.protobuf

import org.testng.Assert
import org.testng.annotations.Test
import sample.xyz.srclab.common.protobuf.protogen.MessageData
import sample.xyz.srclab.common.protobuf.protogen.RequestMessage
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.addElements
import xyz.srclab.common.collect.putEntries
import xyz.srclab.common.collect.sorted
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.protobuf.PROTOBUF_BEAN_RESOLVER
import xyz.srclab.common.protobuf.PROTOBUF_CONVERTER
import xyz.srclab.common.protobuf.PROTOBUF_JSON_SERIALIZER
import xyz.srclab.common.serialize.json.toJsonString
import xyz.srclab.common.test.TestLogger

class ProtobufSample {

    @Test
    fun testProtobufResolver() {
        val messageData = MessageData.newBuilder()
            .setType(MessageData.Type.TYPE_0)
            .setMessage("666")
            .addAllNumber(listOf("7", "8", "9"))
            .putEntry("m1", "mm1")
            .putEntry("m2", "mm2")
            .build()
        val requestMessage = RequestMessage.newBuilder()
            .setId("123")
            .setData(messageData)
            .build()
        val beanResolver: BeanResolver = PROTOBUF_BEAN_RESOLVER
        val dataType = beanResolver.resolve(messageData.javaClass)
        Assert.assertEquals(
            dataType.properties.keys.sorted(String.CASE_INSENSITIVE_ORDER),
            HashSet<String>().addElements("type", "message", "numberList", "entryMap", "class")
                .sorted(String.CASE_INSENSITIVE_ORDER)
        )
        val requestType = beanResolver.resolve(requestMessage.javaClass)
        Assert.assertEquals(
            requestType.properties.keys.sorted(String.CASE_INSENSITIVE_ORDER),
            java.util.HashSet<String>().addElements("id", "data", "class")
                .sorted(String.CASE_INSENSITIVE_ORDER)
        )
        val javaMessageData = JavaMessageData()
        messageData.copyProperties(
            javaMessageData,
            PROTOBUF_BEAN_RESOLVER, PROTOBUF_CONVERTER
        )
        //javaMessageData: {"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}
        logger.log("javaMessageData: {}", javaMessageData.toJsonString())
        Assert.assertEquals(javaMessageData.type, "TYPE_0")
        Assert.assertEquals(javaMessageData.message, "666")
        Assert.assertEquals(javaMessageData.numberList, listOf("7", "8", "9"))
        Assert.assertEquals(
            javaMessageData.entryMap,
            LinkedHashMap<Any, Any>().putEntries("m1", "mm1", "m2", "mm2")
        )
        val javaRequestMessage = JavaRequestMessage()
        requestMessage.copyProperties(
            javaRequestMessage,
            PROTOBUF_BEAN_RESOLVER, PROTOBUF_CONVERTER
        )
        //javaRequestMessage: {"id":"123","data":{"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}}
        logger.log("javaRequestMessage: {}", javaRequestMessage.toJsonString())
        Assert.assertEquals(javaRequestMessage.id, "123")
        Assert.assertEquals(javaRequestMessage.data!!.type, "TYPE_0")
        Assert.assertEquals(javaRequestMessage.data!!.message, "666")
        Assert.assertEquals(javaRequestMessage.data!!.numberList, listOf("7", "8", "9"))
        Assert.assertEquals(
            javaRequestMessage.data!!.entryMap,
            LinkedHashMap<Any, Any>().putEntries("m1", "mm1", "m2", "mm2")
        )
        javaRequestMessage.id = "999"
        javaMessageData.type = "TYPE_1"
        javaMessageData.message = "java"
        javaMessageData.numberList = listOf("5", "6", "7")
        javaMessageData.entryMap = LinkedHashMap<Any, Any>().putEntries("j1", "jj1").asAny<Map<String?, String?>>()
        javaRequestMessage.data = javaMessageData
        val messageDataBuilder = MessageData.newBuilder()
        javaMessageData.copyProperties(
            messageDataBuilder,
            PROTOBUF_BEAN_RESOLVER, PROTOBUF_CONVERTER
        )
        //messageDataBuilder: {"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}
        logger.log(
            "messageDataBuilder: {}",
            PROTOBUF_JSON_SERIALIZER.toJsonString(messageDataBuilder)
        )
        Assert.assertEquals(messageDataBuilder.type, MessageData.Type.TYPE_1)
        Assert.assertEquals(messageDataBuilder.message, "java")
        Assert.assertEquals(messageDataBuilder.numberList, listOf("5", "6", "7"))
        Assert.assertEquals(
            messageDataBuilder.entryMap,
            LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
        )
        val requestMessageBuilder = RequestMessage.newBuilder()
        javaRequestMessage.copyProperties(
            requestMessageBuilder,
            PROTOBUF_BEAN_RESOLVER, PROTOBUF_CONVERTER
        )
        //requestMessageBuilder: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
        logger.log(
            "requestMessageBuilder: {}",
            PROTOBUF_JSON_SERIALIZER.toJsonString(requestMessageBuilder)
        )
        Assert.assertEquals(requestMessageBuilder.id, "999")
        Assert.assertEquals(requestMessageBuilder.data.type, MessageData.Type.TYPE_1)
        Assert.assertEquals(requestMessageBuilder.data.message, "java")
        Assert.assertEquals(requestMessageBuilder.data.numberList, listOf("5", "6", "7"))
        Assert.assertEquals(
            requestMessageBuilder.data.entryMap,
            LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
        )
        val convertRequestMessage: RequestMessage =
            PROTOBUF_CONVERTER.convert(javaRequestMessage, RequestMessage::class.java)
        //convertRequestMessage: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
        logger.log(
            "convertRequestMessage: {}",
            PROTOBUF_JSON_SERIALIZER.toJsonString(convertRequestMessage)
        )
        Assert.assertEquals(convertRequestMessage.id, "999")
        Assert.assertEquals(convertRequestMessage.data.type, MessageData.Type.TYPE_1)
        Assert.assertEquals(convertRequestMessage.data.message, "java")
        Assert.assertEquals(convertRequestMessage.data.numberList, listOf("5", "6", "7"))
        Assert.assertEquals(
            convertRequestMessage.data.entryMap,
            LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
        )
    }

    class JavaRequestMessage {
        var id: String? = null
        var data: JavaMessageData? = null
    }

    class JavaMessageData {
        var type: String? = null
        var message: String? = null
        var numberList: List<String?>? = null
        var entryMap: Map<String?, String?>? = null
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}