package test.xyz.srclab.common.protobuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.xyz.srclab.common.protobuf.protogen.MessageData;
import test.xyz.srclab.common.protobuf.protogen.RequestMessage;
import xyz.srclab.common.base.Anys;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanType;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.protobuf.ProtobufBeans;
import xyz.srclab.common.protobuf.ProtobufConverts;
import xyz.srclab.common.protobuf.ProtobufJsons;
import xyz.srclab.common.serialize.json.JsonSerials;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProtobufTest {

    private static final Logger logger = LoggerFactory.getLogger(ProtobufTest.class);

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
                dataType.properties().keySet().toArray(),
                new Object[]{"type", "message", "numberList", "entryMap", "class"}
        );
        BeanType requestType = beanResolver.resolve(requestMessage.getClass());
        Assert.assertEquals(
                requestType.properties().keySet().toArray(),
                new Object[]{"id", "data", "class"}
        );

        JavaMessageData javaMessageData = new JavaMessageData();
        Beans.copyProperties(messageData, javaMessageData, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info("javaMessageData: {}", JsonSerials.toJsonString(javaMessageData));
        Assert.assertEquals(javaMessageData.getType(), MessageData.Type.TYPE_0);
        Assert.assertEquals(javaMessageData.getMessage(), "666");
        Assert.assertEquals(javaMessageData.getNumberList(), Arrays.asList("7", "8", "9"));
        Assert.assertEquals(
                javaMessageData.getEntryMap(),
                Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
        );

        JavaRequestMessage javaRequestMessage = new JavaRequestMessage();
        Beans.copyProperties(requestMessage, javaRequestMessage, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info("javaRequestMessage: {}", JsonSerials.toJsonString(javaRequestMessage));
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
        Beans.copyProperties(javaMessageData, messageDataBuilder, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info(
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
        Beans.copyProperties(javaRequestMessage, requestMessageBuilder, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
        logger.info(
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
        logger.info(
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
