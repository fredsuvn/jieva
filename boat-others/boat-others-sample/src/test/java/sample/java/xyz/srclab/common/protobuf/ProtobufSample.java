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
        Assert.assertEquals(javaMessageData.getType(), MessageData.Type.TYPE_0);
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
        Assert.assertEquals(javaRequestMessage.getData().getType(), MessageData.Type.TYPE_0);
        Assert.assertEquals(javaRequestMessage.getData().getMessage(), "666");
        Assert.assertEquals(javaRequestMessage.getData().getNumberList(), Arrays.asList("7", "8", "9"));
        Assert.assertEquals(
            javaRequestMessage.getData().getEntryMap(),
            Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
        );

        javaRequestMessage.setId("999");
        javaMessageData.setType(MessageData.Type.TYPE_1);
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

        private MessageData.Type type;
        private String message;
        private List<String> numberList;
        private Map<String, String> entryMap;

        public MessageData.Type getType() {
            return type;
        }

        public void setType(MessageData.Type type) {
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
