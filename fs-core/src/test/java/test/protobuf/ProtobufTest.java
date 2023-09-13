package test.protobuf;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.FsBean;
import xyz.srclab.common.bean.FsBeanProperty;
import xyz.srclab.common.bean.FsBeanResolver;
import xyz.srclab.common.collect.FsCollect;
import xyz.srclab.common.protobuf.FsProtobuf;
import xyz.srclab.common.reflect.TypeRef;

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
        Assert.assertEquals(propertyMap.size(), 5);
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
        Assert.assertEquals(builderPropertyMap.size(), 5);
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
    public void testConvert() {
        Data.Builder dataBuilder = Data.newBuilder()
            .setEm(Enum.E2)
            .setStr("888")
            .setNum(999)
            .addAllText(Arrays.asList("2", "4", "6"))
            .putAllEntry(FsCollect.hashMap("1", "1", "3", "3"));
        Data data = dataBuilder.build();
    }

    public static class RequestDto {
        private int code;
        private byte[] message;
    }

    /*
    message Request {
    int32 code = 1;
    bytes message = 2;
    Data data = 3;
}

message Response {
    string code = 1;
    int64 state = 2;
    Data data = 3;
}

message Data {
    Enum em = 1;
    string str = 2;
    int64 num = 3;
    repeated string text = 4;
    map<string, string> entry = 5;
}

enum Enum {
    E1 = 0;
    E2 = 1;
}
     */
}
