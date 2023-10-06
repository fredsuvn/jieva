package test;

import com.google.protobuf.ByteString;
import lombok.EqualsAndHashCode;
import org.testng.Assert;
import org.testng.annotations.Test;
import test.protobuf.Data;
import test.protobuf.Enum;
import test.protobuf.Request;
import xyz.fs404.common.bean.FsBean;
import xyz.fs404.common.bean.FsBeanProperty;
import xyz.fs404.common.bean.FsBeanResolver;
import xyz.fs404.common.collect.FsCollect;
import xyz.fs404.common.convert.FsConverter;
import xyz.fs404.common.protobuf.FsProtobuf;
import xyz.fs404.common.reflect.TypeRef;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProtobufTest {

    @Test
    public void testBean() {
        /*
        message Data {
            Enum em = 1;
            string str = 2;
            int64 num = 3;
            repeated string text = 4;
            map<string, string> entry = 5;
        }
         */
        FsBeanResolver resolver = FsProtobuf.protobufBeanResolver();
        FsBean dataBean = resolver.resolve(Data.class);
        Map<String, FsBeanProperty> propertyMap = dataBean.getProperties();
        Assert.assertEquals(propertyMap.size(), 14);
        Assert.assertEquals(propertyMap.get("em").getType(), Enum.class);
        Assert.assertEquals(propertyMap.get("str").getType(), String.class);
        Assert.assertEquals(propertyMap.get("num").getType(), long.class);
        Assert.assertEquals(propertyMap.get("textList").getType(), new TypeRef<List<String>>() {
        }.getType());
        Assert.assertEquals(propertyMap.get("entryMap").getType(), new TypeRef<Map<String, String>>() {
        }.getType());
        Assert.assertFalse(propertyMap.get("em").isWriteable());
        Assert.assertFalse(propertyMap.get("str").isWriteable());
        Assert.assertFalse(propertyMap.get("num").isWriteable());
        Assert.assertFalse(propertyMap.get("textList").isWriteable());
        Assert.assertFalse(propertyMap.get("entryMap").isWriteable());

        FsBean dataBuilderBean = resolver.resolve(Data.newBuilder().getClass());
        Map<String, FsBeanProperty> builderPropertyMap = dataBuilderBean.getProperties();
        Assert.assertEquals(builderPropertyMap.size(), 14);
        Assert.assertEquals(builderPropertyMap.get("em").getType(), Enum.class);
        Assert.assertEquals(builderPropertyMap.get("str").getType(), String.class);
        Assert.assertEquals(builderPropertyMap.get("num").getType(), long.class);
        Assert.assertEquals(builderPropertyMap.get("textList").getType(), new TypeRef<List<String>>() {
        }.getType());
        Assert.assertEquals(builderPropertyMap.get("entryMap").getType(), new TypeRef<Map<String, String>>() {
        }.getType());
        Assert.assertTrue(builderPropertyMap.get("em").isWriteable());
        Assert.assertTrue(builderPropertyMap.get("str").isWriteable());
        Assert.assertTrue(builderPropertyMap.get("num").isWriteable());
        Assert.assertTrue(builderPropertyMap.get("textList").isWriteable());
        Assert.assertTrue(builderPropertyMap.get("entryMap").isWriteable());

        Data.Builder dataBuilder = Data.newBuilder()
            .setEm(Enum.E2)
            .setStr("888")
            .setNum(999)
            .addAllText(Arrays.asList("2", "4", "6"))
            .putAllEntry(FsCollect.hashMap("1", "1", "3", "3"));
        Data data = dataBuilder.build();
        Assert.assertEquals(propertyMap.get("em").get(data), Enum.E2);
        Assert.assertEquals(propertyMap.get("str").get(data), "888");
        Assert.assertEquals(propertyMap.get("num").get(data), 999L);
        Assert.assertEquals(propertyMap.get("textList").get(data), Arrays.asList("2", "4", "6"));
        Assert.assertEquals(propertyMap.get("entryMap").get(data), FsCollect.hashMap("1", "1", "3", "3"));
        Assert.assertEquals(builderPropertyMap.get("em").get(dataBuilder), Enum.E2);
        Assert.assertEquals(builderPropertyMap.get("str").get(dataBuilder), "888");
        Assert.assertEquals(builderPropertyMap.get("num").get(dataBuilder), 999L);
        Assert.assertEquals(builderPropertyMap.get("textList").get(dataBuilder), Arrays.asList("2", "4", "6"));
        Assert.assertEquals(builderPropertyMap.get("entryMap").get(dataBuilder), FsCollect.hashMap("1", "1", "3", "3"));
        builderPropertyMap.get("em").set(dataBuilder, Enum.E1);
        builderPropertyMap.get("str").set(dataBuilder, "777");
        builderPropertyMap.get("num").set(dataBuilder, 888L);
        builderPropertyMap.get("textList").set(dataBuilder, Arrays.asList("3", "5", "7"));
        builderPropertyMap.get("entryMap").set(dataBuilder, FsCollect.hashMap("8", "8", "9", "9"));
        Data data2 = dataBuilder.build();
        Assert.assertEquals(propertyMap.get("em").get(data2), Enum.E1);
        Assert.assertEquals(propertyMap.get("str").get(data2), "777");
        Assert.assertEquals(propertyMap.get("num").get(data2), 888L);
        Assert.assertEquals(propertyMap.get("textList").get(data2), Arrays.asList("3", "5", "7"));
        Assert.assertEquals(propertyMap.get("entryMap").get(data2), FsCollect.hashMap("8", "8", "9", "9"));
    }

    @Test
    public void testConvertBean() {
        DataDto dataDto = new DataDto();
        dataDto.setEm(EnumDto.E2);
        dataDto.setStr("dataDto");
        dataDto.setTextList(Arrays.asList("11", "22", "33"));
        dataDto.setNum(777);
        dataDto.setBytes("123".getBytes());
        dataDto.setEntryMap(FsCollect.hashMap("88", "99"));
        dataDto.setFixed32(132);
        dataDto.setFixed64(164);
        dataDto.setSfixed32(232);
        dataDto.setSfixed64(264);
        dataDto.setUint32(332);
        dataDto.setUint64(364);
        dataDto.setSint32(432);
        dataDto.setSint64(464);
        RequestDto requestDto = new RequestDto();
        requestDto.setCode(111);
        requestDto.setMessage("111");
        requestDto.setData(dataDto);

        //dto -> protobuf
        ResponseDto responseDto = FsProtobuf.protobufConverter().convert(requestDto, ResponseDto.class);
        Assert.assertEquals(responseDto.getCode(), "111");
        Assert.assertEquals(responseDto.getMessage(), 111L);
        Data data = responseDto.getData();
        Assert.assertEquals(data.getEm(), Enum.E2);
        Assert.assertEquals(data.getStr(), dataDto.getStr());
        Assert.assertEquals(data.getTextList(), dataDto.getTextList());
        Assert.assertEquals(data.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
        Assert.assertEquals(data.getEntryMap(), dataDto.getEntryMap());
        Assert.assertEquals(data.getFixed32(), dataDto.getFixed32());
        Assert.assertEquals(data.getFixed64(), dataDto.getFixed64());
        Assert.assertEquals(data.getSfixed32(), dataDto.getSfixed32());
        Assert.assertEquals(data.getSfixed64(), dataDto.getSfixed64());
        Assert.assertEquals(data.getUint32(), dataDto.getUint32());
        Assert.assertEquals(data.getUint64(), dataDto.getUint64());
        Assert.assertEquals(data.getSint32(), dataDto.getSint32());
        Assert.assertEquals(data.getSint64(), dataDto.getSint64());

        //dto -> protobuf builder
        Data.Builder dataBuilder = FsProtobuf.protobufConverter().convert(requestDto.getData(), Data.Builder.class);
        Assert.assertEquals(dataBuilder.getEm(), Enum.E2);
        Assert.assertEquals(dataBuilder.getStr(), dataDto.getStr());
        Assert.assertEquals(dataBuilder.getTextList(), dataDto.getTextList());
        Assert.assertEquals(dataBuilder.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
        Assert.assertEquals(dataBuilder.getEntryMap(), dataDto.getEntryMap());
        Assert.assertEquals(dataBuilder.getFixed32(), dataDto.getFixed32());
        Assert.assertEquals(dataBuilder.getFixed64(), dataDto.getFixed64());
        Assert.assertEquals(dataBuilder.getSfixed32(), dataDto.getSfixed32());
        Assert.assertEquals(dataBuilder.getSfixed64(), dataDto.getSfixed64());
        Assert.assertEquals(dataBuilder.getUint32(), dataDto.getUint32());
        Assert.assertEquals(dataBuilder.getUint64(), dataDto.getUint64());
        Assert.assertEquals(dataBuilder.getSint32(), dataDto.getSint32());
        Assert.assertEquals(dataBuilder.getSint64(), dataDto.getSint64());

        //protobuf -> dto
        DataDto dataDto1 = FsProtobuf.protobufConverter().convert(data, DataDto.class);
        Assert.assertEquals(data.getEm(), Enum.E2);
        Assert.assertEquals(data.getStr(), dataDto.getStr());
        Assert.assertEquals(data.getTextList(), dataDto.getTextList());
        Assert.assertEquals(data.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
        Assert.assertEquals(data.getEntryMap(), dataDto.getEntryMap());
        Assert.assertEquals(data.getFixed32(), dataDto.getFixed32());
        Assert.assertEquals(data.getFixed64(), dataDto.getFixed64());
        Assert.assertEquals(data.getSfixed32(), dataDto.getSfixed32());
        Assert.assertEquals(data.getSfixed64(), dataDto.getSfixed64());
        Assert.assertEquals(data.getUint32(), dataDto.getUint32());
        Assert.assertEquals(data.getUint64(), dataDto.getUint64());
        Assert.assertEquals(data.getSint32(), dataDto.getSint32());
        Assert.assertEquals(data.getSint64(), dataDto.getSint64());

        //protobuf builder -> dto
        DataDto dataDto2 = FsProtobuf.protobufConverter().convert(dataBuilder, DataDto.class);
        Assert.assertEquals(dataBuilder.getEm(), Enum.E2);
        Assert.assertEquals(dataBuilder.getStr(), dataDto.getStr());
        Assert.assertEquals(dataBuilder.getTextList(), dataDto.getTextList());
        Assert.assertEquals(dataBuilder.getBytes(), ByteString.copyFrom(dataDto.getBytes()));
        Assert.assertEquals(dataBuilder.getEntryMap(), dataDto.getEntryMap());
        Assert.assertEquals(dataBuilder.getFixed32(), dataDto.getFixed32());
        Assert.assertEquals(dataBuilder.getFixed64(), dataDto.getFixed64());
        Assert.assertEquals(dataBuilder.getSfixed32(), dataDto.getSfixed32());
        Assert.assertEquals(dataBuilder.getSfixed64(), dataDto.getSfixed64());
        Assert.assertEquals(dataBuilder.getUint32(), dataDto.getUint32());
        Assert.assertEquals(dataBuilder.getUint64(), dataDto.getUint64());
        Assert.assertEquals(dataBuilder.getSint32(), dataDto.getSint32());
        Assert.assertEquals(dataBuilder.getSint64(), dataDto.getSint64());

        //protobuf -> protobuf
        Data.Builder dataBuilder1 = FsProtobuf.protobufConverter().convert(data, Data.Builder.class);
        Assert.assertEquals(dataBuilder1.build(), dataBuilder.build());
        Data data1 = FsProtobuf.protobufConverter().convert(dataBuilder1, Data.class);
        Assert.assertEquals(data1, dataBuilder1.build());

        Request request = Request.newBuilder()
            .setCode(3).setMessage(ByteString.copyFromUtf8("xxxx")).setData(data).build();
        RequestDto requestDto1 = FsProtobuf.protobufConverter().convert(request, RequestDto.class);
        Assert.assertEquals(requestDto1.getCode(), request.getCode());
        Assert.assertEquals(requestDto1.getMessage(), request.getMessage().toStringUtf8());
        Assert.assertEquals(requestDto1.getData(), dataDto);
    }

    @Test
    public void testConvert() {
        byte[] bytes = {1, 2, 3, 4};
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteString bs = ByteString.copyFrom(bytes);
        String str = bs.toStringUtf8();
        FsConverter converter = FsProtobuf.protobufConverter();
        Assert.assertEquals(converter.convert(bytes, ByteBuffer.class), buffer.slice());
        Assert.assertEquals(converter.convert(bs, ByteBuffer.class), buffer.slice());
        Assert.assertEquals(converter.convert(buffer, ByteBuffer.class), buffer.slice());
        Assert.assertEquals(converter.convert(bytes, byte[].class), bytes);
        Assert.assertEquals(converter.convert(bs, byte[].class), bytes);
        Assert.assertEquals(converter.convert(buffer, byte[].class), bytes);
        Assert.assertEquals(converter.convert(bytes, ByteString.class), bs);
        Assert.assertEquals(converter.convert(bs, ByteString.class), bs);
        Assert.assertEquals(converter.convert(buffer, ByteString.class), bs);
        Assert.assertEquals(converter.convert(bs, String.class), str);
        Assert.assertEquals(converter.convert(str, ByteString.class), bs);
    }

    @lombok.Data
    @EqualsAndHashCode
    public static class DataDto {
        private EnumDto em;
        private String str;
        private long num;
        private List<String> textList;
        private Map<String, String> entryMap;
        private int uint32;
        private long uint64;
        private int fixed32;
        private long fixed64;
        private int sfixed32;
        private long sfixed64;
        private int sint32;
        private long sint64;
        private byte[] bytes;
    }

    @lombok.Data
    @EqualsAndHashCode
    public static class RequestDto {
        private int code;
        private String message;
        private DataDto data;
    }

    @lombok.Data
    @EqualsAndHashCode
    public static class ResponseDto {
        private String code;
        private long message;
        private Data data;
    }

    public enum EnumDto {
        E1, E2;
    }
}
