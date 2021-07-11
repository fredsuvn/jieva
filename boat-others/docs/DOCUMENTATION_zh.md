# <span class="image">![Boat Others](../../logo.svg)</span> Boat Others — [Boat](../../README.md) 第三方支持和扩展库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

目录

-   [简介](#_简介)
-   [支持列表](#_支持列表)
    -   [Protobuf: boat-protobuf](#_protobuf_boat_protobuf)

## 简介

Boat others 为 [Boat](../../README.md)提供第三方支持和扩展.

## 支持列表

### Protobuf: boat-protobuf

boat-protobuf 提供为 `bean`, `convert` 何 `serialize` 领域的针对
protobuf 类型的处理器:

-   `ProtobufBeanResolveHandler`: 支持 protobuf 类型的bean解析器, 从
    `BeanResolveHandler` (boat-core) 扩展;

-   `ProtobufBeanGenerator`: 支持 protobuf 类型的
    `BeanConvertHandler.NewBeanProvider` (boat-core);

-   `ProtobufBeans.PROTOBUF_BEAN_RESOLVER`: 支持 protobuf
    类型的bean解析器处理器, 从 `BeanResolver` (boat-core) 扩展;

-   ProtobufConverts.PROTOBUF\_CONVERTER: 支持 protobuf
    类型的类型转换器, 从 `Converter` (boat-core) 扩展;

-   ProtobufJsons.PROTOBUF\_OBJECT\_MAPPER: `Jackson`的 `ObjectMapper`
    (boat-serialize), 支持 protobuf 类型;

-   ProtobufJsons.PROTOBUF\_JSON\_SERIALIZER: `JsonSerializer`
    (boat-serialize), 支持 protobuf 类型.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<tbody>
<tr class="odd">
<td class="icon"><div class="title">
Warning
</div></td>
<td class="content"><code>boat-protobuf</code> 需要 <code>protoc</code> 来编译 protobuf 文件, 一些架构并不支持 (如 <code>ARM</code>). 检查你的环境是否支持: <a href="https://repo1.maven.org/maven2/com/google/protobuf/protoc/" class="bare">https://repo1.maven.org/maven2/com/google/protobuf/protoc/</a></td>
</tr>
</tbody>
</table>

Java Examples

    package sample.java.xyz.srclab.common.protobuf;

    import org.testng.Assert;
    import org.testng.annotations.Test;
    import sample.xyz.srclab.common.protobuf.protogen.MessageData;
    import sample.xyz.srclab.common.protobuf.protogen.RequestMessage;
    import xyz.srclab.common.bean.BeanResolver;
    import xyz.srclab.common.bean.BeanType;
    import xyz.srclab.common.bean.Beans;
    import xyz.srclab.common.collect.Collects;
    import xyz.srclab.common.lang.Anys;
    import xyz.srclab.common.protobuf.ProtobufBeans;
    import xyz.srclab.common.protobuf.ProtobufConverts;
    import xyz.srclab.common.protobuf.ProtobufJsons;
    import xyz.srclab.common.serialize.json.JsonSerials;
    import xyz.srclab.common.test.TestLogger;

    import java.util.*;

    public class ProtobufSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testProtobufResolver() {
            MessageData messageData = MessageData.newBuilder()
                .setType(MessageData.Type.TYPE_0)
                .setMessage("666")
                .addAllNumber(Arrays.asList("7", "8", "9"))
                .putEntry("m1", "mm1")
                .putEntry("m2", "mm2")
                .build();
            RequestMessage requestMessage = RequestMessage.newBuilder()
                .setId("123")
                .setData(messageData)
                .build();

            BeanResolver beanResolver = ProtobufBeans.PROTOBUF_BEAN_RESOLVER;
            BeanType dataType = beanResolver.resolve(messageData.getClass());
            Assert.assertEquals(
                Collects.sorted(dataType.properties().keySet(), String.CASE_INSENSITIVE_ORDER),
                Collects.sorted(Collects.newCollection(new HashSet<>(),
                    "type", "message", "numberList", "entryMap", "class"),
                    String.CASE_INSENSITIVE_ORDER
                )
            );
            BeanType requestType = beanResolver.resolve(requestMessage.getClass());
            Assert.assertEquals(
                Collects.sorted(requestType.properties().keySet(), String.CASE_INSENSITIVE_ORDER),
                Collects.sorted(Collects.newCollection(new HashSet<>(),
                    "id", "data", "class"),
                    String.CASE_INSENSITIVE_ORDER
                )
            );

            JavaMessageData javaMessageData = new JavaMessageData();
            Beans.copyProperties(messageData, javaMessageData,
                ProtobufBeans.PROTOBUF_BEAN_RESOLVER, ProtobufConverts.PROTOBUF_CONVERTER);
            //javaMessageData: {"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}
            logger.log("javaMessageData: {}", JsonSerials.toJsonString(javaMessageData));
            Assert.assertEquals(javaMessageData.getType(), "TYPE_0");
            Assert.assertEquals(javaMessageData.getMessage(), "666");
            Assert.assertEquals(javaMessageData.getNumberList(), Arrays.asList("7", "8", "9"));
            Assert.assertEquals(
                javaMessageData.getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
            );

            JavaRequestMessage javaRequestMessage = new JavaRequestMessage();
            Beans.copyProperties(requestMessage, javaRequestMessage,
                ProtobufBeans.PROTOBUF_BEAN_RESOLVER, ProtobufConverts.PROTOBUF_CONVERTER);
            //javaRequestMessage: {"id":"123","data":{"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}}
            logger.log("javaRequestMessage: {}", JsonSerials.toJsonString(javaRequestMessage));
            Assert.assertEquals(javaRequestMessage.getId(), "123");
            Assert.assertEquals(javaRequestMessage.getData().getType(), "TYPE_0");
            Assert.assertEquals(javaRequestMessage.getData().getMessage(), "666");
            Assert.assertEquals(javaRequestMessage.getData().getNumberList(), Arrays.asList("7", "8", "9"));
            Assert.assertEquals(
                javaRequestMessage.getData().getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
            );

            javaRequestMessage.setId("999");
            javaMessageData.setType("TYPE_1");
            javaMessageData.setMessage("java");
            javaMessageData.setNumberList(Arrays.asList("5", "6", "7"));
            javaMessageData.setEntryMap(Anys.as(Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")));
            javaRequestMessage.setData(javaMessageData);

            MessageData.Builder messageDataBuilder = MessageData.newBuilder();
            Beans.copyProperties(javaMessageData, messageDataBuilder,
                ProtobufBeans.PROTOBUF_BEAN_RESOLVER, ProtobufConverts.PROTOBUF_CONVERTER);
            //messageDataBuilder: {"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}
            logger.log(
                "messageDataBuilder: {}",
                ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(messageDataBuilder)
            );
            Assert.assertEquals(messageDataBuilder.getType(), MessageData.Type.TYPE_1);
            Assert.assertEquals(messageDataBuilder.getMessage(), "java");
            Assert.assertEquals(messageDataBuilder.getNumberList(), Arrays.asList("5", "6", "7"));
            Assert.assertEquals(
                messageDataBuilder.getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
            );

            RequestMessage.Builder requestMessageBuilder = RequestMessage.newBuilder();
            Beans.copyProperties(javaRequestMessage, requestMessageBuilder,
                ProtobufBeans.PROTOBUF_BEAN_RESOLVER, ProtobufConverts.PROTOBUF_CONVERTER);
            //requestMessageBuilder: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
            logger.log(
                "requestMessageBuilder: {}",
                ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(requestMessageBuilder)
            );
            Assert.assertEquals(requestMessageBuilder.getId(), "999");
            Assert.assertEquals(requestMessageBuilder.getData().getType(), MessageData.Type.TYPE_1);
            Assert.assertEquals(requestMessageBuilder.getData().getMessage(), "java");
            Assert.assertEquals(requestMessageBuilder.getData().getNumberList(), Arrays.asList("5", "6", "7"));
            Assert.assertEquals(
                requestMessageBuilder.getData().getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
            );

            RequestMessage convertRequestMessage =
                ProtobufConverts.PROTOBUF_CONVERTER.convert(javaRequestMessage, RequestMessage.class);
            //convertRequestMessage: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
            logger.log(
                "convertRequestMessage: {}",
                ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(convertRequestMessage)
            );
            Assert.assertEquals(convertRequestMessage.getId(), "999");
            Assert.assertEquals(convertRequestMessage.getData().getType(), MessageData.Type.TYPE_1);
            Assert.assertEquals(convertRequestMessage.getData().getMessage(), "java");
            Assert.assertEquals(convertRequestMessage.getData().getNumberList(), Arrays.asList("5", "6", "7"));
            Assert.assertEquals(
                convertRequestMessage.getData().getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
            );
        }

        public static class JavaRequestMessage {

            private String id;
            private JavaMessageData data;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public JavaMessageData getData() {
                return data;
            }

            public void setData(JavaMessageData data) {
                this.data = data;
            }
        }

        public static class JavaMessageData {

            private String type;
            private String message;
            private List<String> numberList;
            private Map<String, String> entryMap;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public List<String> getNumberList() {
                return numberList;
            }

            public void setNumberList(List<String> numberList) {
                this.numberList = numberList;
            }

            public Map<String, String> getEntryMap() {
                return entryMap;
            }

            public void setEntryMap(Map<String, String> entryMap) {
                this.entryMap = entryMap;
            }
        }
    }

Kotlin Examples

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
