package test.xyz.srclab.common.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.serialize.json.Json;
import xyz.srclab.common.serialize.json.JsonSerializer;
import xyz.srclab.common.serialize.json.JsonType;
import xyz.srclab.common.test.TestLogger;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * @author sunqian
 */
public class JsonTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJson() throws Exception {
        JsonSerializer jsonSerializer = JsonSerializer.DEFAULT;

        //测试和原生Jackson生成json是否一致
        TestObject testObject = new TestObject();
        testObject.setString("888");
        testObject.setMap(Arrays.asList(
                newMap("1", new BigDecimal(1)),
                newMap("2", new BigDecimal(2)),
                newMap("3", new BigDecimal(3))
        ));
        String jsonString = jsonSerializer.toJsonString(testObject);
        logger.log(jsonString);
        Assert.assertEquals(jsonString, objectMapper.writeValueAsString(testObject));

        //测试toJson(Object)
        Object jsonStringAsObject = jsonString;
        logger.log(jsonSerializer.toJson(jsonStringAsObject).toJsonString());
        Assert.assertEquals(
                jsonSerializer.toJson(jsonStringAsObject).toJavaObject(TestObject.class),
                testObject
        );

        //测试从json string还原java对象
        Json json = jsonSerializer.toJson(jsonString);
        TestObject fromJson = json.toJavaObject(TestObject.class);
        boolean equals = testObject.equals(fromJson);
        logger.log("testObject == fromJson: " + equals);
        Assert.assertTrue(equals);

        TestObject2 testObject2 = new TestObject2();
        testObject2.setString(888);
        testObject2.setMap(Arrays.asList(
                newMap(new BigDecimal(1), "1"),
                newMap(new BigDecimal(2), "2"),
                newMap(new BigDecimal(3), "3")
        ));
        TestObject2 fromJson2 = json.toJavaObject(TestObject2.class);
        boolean equals2 = testObject2.equals(fromJson2);
        logger.log("testObject2 == fromJson2: " + equals2);
        Assert.assertTrue(equals2);

        //测试java对象转java对象
        Json jsonTest = jsonSerializer.toJson(testObject);
        logger.log("jsonTest: " + jsonTest.toJsonString());
        Assert.assertEquals(jsonTest.toJavaObject(TestObject2.class), testObject2);

        //测试时间类型
        long now = System.currentTimeMillis();
        logger.log("now: " + now);
        String nowJson = jsonSerializer.toJsonString(now);
        logger.log("nowJson: " + nowJson);
        Date date = jsonSerializer.toJson(nowJson).toJavaObject(Date.class);
        logger.log("date: " + date);
        Assert.assertEquals(date.getTime(), now);
        //这里实际是按照“秒”这个单位来create的，所以下面的now要乘以1000
        Instant instant = jsonSerializer.toJson(nowJson).toJavaObject(Instant.class);
        logger.log("instant: " + instant);
        Assert.assertEquals(instant.toEpochMilli(), now * 1000);

        //测试json type
        JsonType jsonType = json.type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.OBJECT);

        jsonType = jsonSerializer.toJson("true").type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.BOOLEAN);

        jsonType = jsonSerializer.toJson("1").type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.NUMBER);

        jsonType = jsonSerializer.toJson("null").type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.NULL);

        jsonType = jsonSerializer.toJson("[]").type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.ARRAY);

        jsonType = jsonSerializer.toJson("\"666\"").type();
        logger.log(jsonType);
        Assert.assertEquals(jsonType, JsonType.STRING);

        //测试map
        Map<String, String> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", "2");
        Json mapJson = jsonSerializer.toJson(map);
        String toJsonString = "{\"key1\":\"1\",\"key2\":\"2\"}";
        logger.log(mapJson.toJsonString());
        Assert.assertEquals(mapJson.toJsonString(), toJsonString);
        Assert.assertEquals(mapJson.toJsonBytes(), toJsonString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testNull() {
        //Test null
        Json nullJson = Json.NULL;
        Assert.assertEquals(nullJson.toJsonString(), "null");
        Assert.assertNull(nullJson.toJavaStringOrNull());
        Assert.expectThrows(IllegalStateException.class, nullJson::toJavaString);
    }

    private <K, V> Map<K, V> newMap(K key, V value) {
        Map<K, V> result = new HashMap<>(1);
        result.put(key, value);
        return result;
    }

    public static class TestObject {
        private String string;
        private long longValue;
        private List<Map<String, BigDecimal>> map;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public List<Map<String, BigDecimal>> getMap() {
            return map;
        }

        public void setMap(List<Map<String, BigDecimal>> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            TestObject that = (TestObject) object;
            return longValue == that.longValue &&
                    Objects.equals(string, that.string) &&
                    Objects.equals(map, that.map);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, longValue, map);
        }
    }

    public static class TestObject2 {
        private int string;
        private long longValue;
        private List<Map<BigDecimal, String>> map;

        public int getString() {
            return string;
        }

        public void setString(int string) {
            this.string = string;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public List<Map<BigDecimal, String>> getMap() {
            return map;
        }

        public void setMap(List<Map<BigDecimal, String>> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            TestObject2 that = (TestObject2) object;
            return string == that.string &&
                    longValue == that.longValue &&
                    Objects.equals(map, that.map);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, longValue, map);
        }
    }
}
