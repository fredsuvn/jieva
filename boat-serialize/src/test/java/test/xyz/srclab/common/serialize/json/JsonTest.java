package test.xyz.srclab.common.serialize.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Chars;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.serialize.jackson.Jacksons;
import xyz.srclab.common.serialize.json.Json;
import xyz.srclab.common.serialize.json.JsonSerializer;
import xyz.srclab.common.serialize.json.JsonType;
import xyz.srclab.common.serialize.json.Jsons;

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
        JsonSerializer jsonSerializer = JsonSerializer.DEFAULT;

        //Jackson
        TestObject testObject = new TestObject();
        testObject.setString("888");
        testObject.setMap(Arrays.asList(
            newMap("1", new BigDecimal(1)),
            newMap("2", new BigDecimal(2)),
            newMap("3", new BigDecimal(3))
        ));
        String jsonString = jsonSerializer.parse(testObject).toJsonString();
        Logs.info(jsonString);
        Assert.assertEquals(jsonString, objectMapper.writeValueAsString(testObject));

        //parse(Object)
        Object jsonStringAsObject = jsonString;
        Logs.info("jsonStringAsObject:{}", jsonSerializer.parse(jsonStringAsObject).toJsonString());
        Assert.assertEquals(
            jsonSerializer.parse(jsonStringAsObject).parse(TestObject.class),
            testObject
        );

        //json string -> java object
        Json json = jsonSerializer.parse(jsonString);
        TestObject fromJson = json.parse(TestObject.class);
        boolean equals = testObject.equals(fromJson);
        Logs.info("testObject == fromJson: " + equals);
        Assert.assertTrue(equals);

        TestObject2 testObject2 = new TestObject2();
        testObject2.setString(888);
        testObject2.setMap(Arrays.asList(
            newMap(new BigDecimal(1), "1"),
            newMap(new BigDecimal(2), "2"),
            newMap(new BigDecimal(3), "3")
        ));
        TestObject2 fromJson2 = json.parse(TestObject2.class);
        boolean equals2 = testObject2.equals(fromJson2);
        Logs.info("testObject2 == fromJson2: " + equals2);
        Assert.assertTrue(equals2);

        //java object -> java object
        Json jsonTest = jsonSerializer.parse(testObject);
        Logs.info("jsonTest: " + jsonTest.toString());
        Assert.assertEquals(jsonTest.parse(TestObject2.class), testObject2);

        //time format
        long now = System.currentTimeMillis();
        Logs.info("now: " + now);
        String nowJson = jsonSerializer.parse(now).parseString();
        Logs.info("nowJson: " + nowJson);
        Date date = jsonSerializer.parse(nowJson).parse(Date.class);
        Logs.info("date: " + date);
        Assert.assertEquals(date.getTime(), now);
        //这里实际是按照“秒”这个单位来create的，所以下面的now要乘以1000
        Instant instant = jsonSerializer.parse(nowJson).parse(Instant.class);
        Logs.info("instant: " + instant);
        Assert.assertEquals(instant.toEpochMilli(), now * 1000);

        //json type
        JsonType jsonType = json.getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.OBJECT);

        jsonType = jsonSerializer.parse("true").getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.BOOLEAN);

        jsonType = jsonSerializer.parse("1").getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.NUMBER);

        jsonType = jsonSerializer.parse("null").getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.NULL);

        jsonType = jsonSerializer.parse("[]").getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.ARRAY);

        jsonType = jsonSerializer.parse("\"666\"").getType();
        Logs.info("jsonType: {}", jsonType);
        Assert.assertEquals(jsonType, JsonType.STRING);

        //map
        Map<String, String> map = new HashMap<>();
        map.put("key1", "1");
        map.put("key2", "2");
        Json mapJson = jsonSerializer.parse(map);
        String parseString = "{\"key1\":\"1\",\"key2\":\"2\"}";
        Logs.info(mapJson.toString());
        Assert.assertEquals(mapJson.toString(), parseString);
        Assert.assertEquals(mapJson.toBytes(), parseString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testNull() {
        //Test null
        Json nullJson = Jsons.parse((Object) null);
        Assert.assertEquals(nullJson.toJsonString(), "null");
        Assert.assertNull(nullJson.parseStringOrNull());
        Assert.expectThrows(IllegalArgumentException.class, () -> nullJson.parse(String.class));
    }

    @Test
    public void testUrl() throws Exception {
        Path path = Paths.get("json.txt");
        Files.write(path, "{\"key1\":\"value1\"}".getBytes(Chars.DEFAULT_CHARSET));
        URL url = new URL("file:json.txt");
        Json json = Jsons.parse(url);
        Logs.info("json: {}", json);
        String value1 = json.parseMap().get("key1").toString();
        Assert.assertEquals(value1, "value1");
        Files.delete(path);
    }

    @Test
    public void testString() {
        String javaString = "abc";
        String jsonString = "\"abc\"";
        Assert.assertEquals(Jsons.toJson(javaString).toJsonString(), jsonString);
        Json json = Jsons.parse(jsonString);
        Logs.info("json: {}", json);
        Assert.assertEquals(json.parseString(), javaString);
        Assert.assertEquals(json.parse(String.class), javaString);
        Assert.assertEquals(json.getType(), JsonType.STRING);
    }

    @Test
    public void testSubJson() {
        Map<String, Object> map = Collects.newMap("s1", "sss", "s2", Arrays.asList("1", "2"));
        String jsonString = Jsons.toJsonString(map);
        Logs.info("jsonString: {}", jsonString);
        Map<String, Json> jsonMap = Jsons.parse(jsonString, new TypeRef<Map<String, Json>>() {
        });
        Logs.info("jsonMap: {}", jsonMap);
        Logs.info("jsonMap.get(\"s2\").getClass(): {}", jsonMap.get("s2").getClass());
        Assert.assertEquals(
            jsonMap.get("s2").getClass().getName(), "xyz.srclab.common.serialize.jackson.JacksonJsonSerializer$JsonImpl");
        Json s2 = jsonMap.get("s2");
        Assert.assertEquals(s2.parse(List.class), Arrays.asList("1", "2"));
        String jsonImplJson = "{\"s1\":\"sss\",\"s2\":[\"1\",\"2\"]}";
        Json jsonImpl = Jsons.parse(jsonImplJson, Json.class);
        Logs.info("jsonImpl: {}", jsonImpl);
    }

    @Test
    public void testJackson() {
        JsonSerializer serializer = Jacksons.newJsonSerializer(new ObjectMapper());
        String mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}";
        Map<String, String> map = serializer.parse(mapJson).parse(new TypeRef<Map<String, String>>() {
        });
        Logs.info("map:{}", map);
        Assert.assertEquals(map.toString(), "{p1=p1 value, p2=p2 value}");

        String stringJson = "abc";
        Logs.info("stringJson: {}", serializer.serialize(stringJson));
        Assert.assertEquals(serializer.serialize(stringJson).toString(), "\"abc\"");
    }

    @Test
    public void testJsonProperty() {
        JsonSerializer jsonSerializer = JsonSerializer.DEFAULT;

        Map<String, String> map = new LinkedHashMap<>();
        map.put("key1", "1");
        map.put("key2", "2");
        Json json = jsonSerializer.serialize(map);
        Logs.info("json: {}", json);

        Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("string", jsonSerializer.serialize("json"));
        map2.put("json", json);
        map2.put("jsonArray", jsonSerializer.serialize(new String[]{"1", "2", "3"}));
        Json json2 = jsonSerializer.serialize(map2);
        Logs.info("json2: {}", json2);
        Assert.assertEquals(json2.toJsonString(), "{\"string\":\"json\",\"json\":{\"key1\":\"1\",\"key2\":\"2\"},\"jsonArray\":[\"1\",\"2\",\"3\"]}");
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
