package test.xyz.srclab.common.data.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BMap;
import xyz.srclab.common.data.DataException;
import xyz.srclab.common.data.jackson.BJackson;
import xyz.srclab.common.data.json.BJson;
import xyz.srclab.common.data.json.Json;
import xyz.srclab.common.data.json.JsonParser;
import xyz.srclab.common.data.json.JsonType;
import xyz.srclab.common.reflect.TypeRef;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

/**
 * @author sunqian
 */
public class JsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJson() throws Exception {
        JsonParser jsonParser = JsonParser.defaultParser();

        //Jackson
        TestObject testObject = new TestObject();
        testObject.setString("888");
        testObject.setMap(Arrays.asList(
            newMap("1", new BigDecimal(1)),
            newMap("2", new BigDecimal(2)),
            newMap("3", new BigDecimal(3))
        ));
        String jsonString = jsonParser.toJson(testObject).toJsonString();
        BLog.info(jsonString);
        Assert.assertEquals(jsonString, objectMapper.writeValueAsString(testObject));

        //parse(Object)
        BLog.info("jsonStringAsObject:{}", jsonParser.parse(jsonString).toJsonString());
        Assert.assertEquals(
            jsonParser.parse(jsonString).toObject(TestObject.class),
            testObject
        );

        //json string -> java object
        Json json = jsonParser.parse(jsonString);
        TestObject fromJson = json.toObject(TestObject.class);
        boolean equals = testObject.equals(fromJson);
        BLog.info("testObject == fromJson: " + equals);
        Assert.assertTrue(equals);

        TestObject2 testObject2 = new TestObject2();
        testObject2.setString(888);
        testObject2.setMap(Arrays.asList(
            newMap(new BigDecimal(1), "1"),
            newMap(new BigDecimal(2), "2"),
            newMap(new BigDecimal(3), "3")
        ));
        TestObject2 fromJson2 = json.toObject(TestObject2.class);
        boolean equals2 = testObject2.equals(fromJson2);
        BLog.info("testObject2 == fromJson2: " + equals2);
        Assert.assertTrue(equals2);

        //java object -> java object
        Json jsonTest = jsonParser.toJson(testObject);
        BLog.info("jsonTest: " + jsonTest.toString());
        Assert.assertEquals(jsonTest.toObject(TestObject2.class), testObject2);

        //time format
        long now = System.currentTimeMillis();
        BLog.info("now: " + now);
        String nowJson = jsonParser.toJson(now).toJsonString();
        BLog.info("nowJson: " + nowJson);
        Date date = jsonParser.parse(nowJson).toObject(Date.class);
        BLog.info("date: " + date);
        Assert.assertEquals(date.getTime(), now);
        //这里实际是按照“秒”这个单位来create的，所以下面的now要乘以1000
        Instant instant = jsonParser.parse(nowJson).toObject(Instant.class);
        BLog.info("instant: " + instant);
        Assert.assertEquals(instant.toEpochMilli(), now * 1000);

        //json type
        JsonType jsonType = json.getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.OBJECT);

        jsonType = jsonParser.parse("true").getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.BOOLEAN);

        jsonType = jsonParser.parse("1").getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.NUMBER);

        jsonType = jsonParser.parse("null").getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.NULL);

        jsonType = jsonParser.parse("[]").getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.ARRAY);

        jsonType = jsonParser.parse("\"666\"").getType();
        BLog.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.STRING);

        //map
        Map<String, String> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", "2");
        Json mapJson = jsonParser.toJson(map);
        String parseString = "{\"key1\":\"1\",\"key2\":\"2\"}";
        BLog.info(mapJson.toString());
        Assert.assertEquals(mapJson.toString(), parseString);
        Assert.assertEquals(mapJson.toBytes(), parseString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testNull() {
        //Test null
        Json nullJson = BJson.toJson((Object) null);
        Assert.assertEquals(nullJson.toJsonString(), "null");
        Assert.assertNull(nullJson.toObjectOrNull(String.class));
        Assert.expectThrows(DataException.class, () -> nullJson.toObject(String.class));
    }

    @Test
    public void testUrl() throws Exception {
        Path path = Paths.get("json.txt");
        Files.write(path, "{\"key1\":\"value1\"}".getBytes(BDefault.DEFAULT_CHARSET));
        URL url = new URL("file:json.txt");
        Json json = BJson.parse(url.openStream());
        BLog.info("json: {}", json);
        String value1 = json.toMap().get("key1").toString();
        Assert.assertEquals(value1, "value1");
        Files.delete(path);
    }

    @Test
    public void testString() {
        String javaString = "abc";
        String jsonString = "\"abc\"";
        Assert.assertEquals(BJson.toJson(javaString).toJsonString(), jsonString);
        Assert.assertEquals(BJson.toJsonString(javaString), jsonString);
        Json json = BJson.parse(jsonString);
        BLog.info("jsonString: {}", json);
        Assert.assertEquals(json.toJsonString(), jsonString);
        Assert.assertEquals(json.toObject(String.class), javaString);
        Assert.assertEquals(json.getType(), JsonType.STRING);
    }

    @Test
    public void testSubJson() {
        Map<String, Object> map = BMap.newMap("s1", "sss", "s2", Arrays.asList("1", "2"));
        String jsonString = BJson.toJsonString(map);
        BLog.info("jsonString: {}", jsonString);
        Map<String, Json> jsonMap = BJson.parse(jsonString, new TypeRef<Map<String, Json>>() {
        });
        BLog.info("jsonMap: {}", jsonMap);
        BLog.info("jsonMap.get(\"s2\"): {}", jsonMap.get("s2"));
        Json s2 = BJson.toJson(Arrays.asList("1", "2"));
        Assert.assertEquals(jsonMap.get("s2"), s2);
        s2 = jsonMap.get("s2");
        Assert.assertEquals(s2.toObject(List.class), Arrays.asList("1", "2"));
        String jsonImplJson = "{\"s1\":\"sss\",\"s2\":[\"1\",\"2\"]}";
        Json jsonImpl = BJson.parse(jsonImplJson, Json.class);
        BLog.info("jsonImpl: {}", jsonImpl);
    }

    @Test
    public void testJackson() {
        JsonParser jsonParser = BJackson.toJsonSerializer(new ObjectMapper());
        String mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}";
        Map<String, String> map = jsonParser.parse(mapJson).toObject(new TypeRef<Map<String, String>>() {
        });
        BLog.info("map:{}", map);
        Assert.assertEquals(map.toString(), "{p1=p1 value, p2=p2 value}");

        String stringJson = "abc";
        BLog.info("stringJson: {}", jsonParser.toJson(stringJson));
        Assert.assertEquals(jsonParser.toJson(stringJson).toString(), "\"abc\"");
    }

    @Test
    public void testJsonProperty() {
        JsonParser jsonParser = JsonParser.defaultParser();

        Map<String, String> map = new LinkedHashMap<>();
        map.put("key1", "1");
        map.put("key2", "2");
        Json json = jsonParser.toJson(map);
        BLog.info("json: {}", json);

        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("string", jsonParser.toJson("json"));
        map2.put("json", json);
        map2.put("jsonArray", jsonParser.toJson(new String[]{"1", "2", "3"}));
        Json json2 = jsonParser.toJson(map2);
        BLog.info("json2: {}", json2);
        Assert.assertEquals(json2.toJsonString(), "{\"string\":\"json\",\"json\":{\"key1\":\"1\",\"key2\":\"2\"},\"jsonArray\":[\"1\",\"2\",\"3\"]}");
    }

    private <K, V> Map<K, V> newMap(K key, V value) {
        return BMap.newMap(key, value);
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
