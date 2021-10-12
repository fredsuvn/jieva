# <span class="image">![Boat Serialize](../../logo.svg)</span> `boat-serialize`: Boat Serialize — [Boat](../../README.md) 序列化和反序列化库

<span id="author" class="author">Sun Qian</span>
<span id="email" class="email"><fredsuvn@163.com></span>

目录

-   [简介](#_简介)
-   [用法](#_用法)
    -   [JSON](#_json)
-   [样例](#_样例)

## 简介

Boat Serialize 提供了同意的接口 (`Serializer`, `Serial`)
来支持序列化和反序列化.

当前它支持的格式有:

-   [JSON](#_json)

## 用法

### JSON

Json包提供 `Serial` 的JSON格式实现: `Json`.

想要获得一个 `Json`, 我们可以使用 boat-serialize 的核心工具类:
`JsonSerials`.

Java Examples

    class Example{
        @Test
        public void test() {
            Json json = JsonSerials.toJson("{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}");
            Map<String, String> map = json.toObject(new TypeRef<Map<String, String>>() {
            });
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val json = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}".toJson()
            val map: Map<String, String> = json.toObject(object : TypeRef<Map<String, String>>() {})
        }
    }

`JsonSerials` 使用默认的 `JsonSerializer` — `Serializer` 针对 `JSON`
格式的核心接口, 来实现它的方法. 我们也可以直接使用 `JsonSerializer`:

Java Examples

    class Example{
        @Test
        public void test() {
            JsonSerializer serializer = JsonSerializer.DEFAULT;
            String mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}";
            Map<String, String> map = serializer.toJson(mapJson).toObject(new TypeRef<Map<String, String>>() {
            });
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val serializer = JsonSerializer.DEFAULT
            val mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}"
            val map: Map<String, String> =
                serializer.toJson(mapJson).toObject(object : TypeRef<Map<String, String>>() {})
        }
    }

默认的, json序列化由 `Jackson` 实现:

-   `JacksonJsonSerializer`: `Jackson` 对 `JsonSerializer` 的实现;

-   `Jacksons`: `Jackson` json工具;

Java Examples

    class Example{
        @Test
        public void test() {
            JsonSerializer serializer = Jacksons.newJsonSerializer(new ObjectMapper());
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val serializer: JsonSerializer = ObjectMapper().toJsonSerializer()
        }
    }

## 样例

Java Examples

    package sample.java.xyz.srclab.serialize;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.testng.annotations.Test;
    import xyz.srclab.common.reflect.TypeRef;
    import xyz.srclab.common.serialize.json.Json;
    import xyz.srclab.common.serialize.json.JsonSerializer;
    import xyz.srclab.common.serialize.json.JsonSerials;
    import xyz.srclab.common.serialize.jackson.Jacksons;
    import xyz.srclab.common.test.TestLogger;

    import java.util.Map;

    public class SerializeSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testJsonSerials() {
            Json json = JsonSerials.toJson("{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}");
            Map<String, String> map = json.toObject(new TypeRef<Map<String, String>>() {
            });
            //{p1=p1 value, p2=p2 value}
            logger.log(map);

            String stringJson = "abc";
            //"abc"
            logger.log("stringJson: {}", JsonSerials.stringify(stringJson));
        }

        @Test
        public void testJsonSerializer() {
            JsonSerializer serializer = JsonSerializer.DEFAULT;
            String mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}";
            Map<String, String> map = serializer.toJson(mapJson).toObject(new TypeRef<Map<String, String>>() {
            });
            //{p1=p1 value, p2=p2 value}
            logger.log(map);

            String stringJson = "abc";
            //"abc"
            logger.log("stringJson: {}", serializer.serialize(stringJson));
        }

        @Test
        public void testJackson() {
            JsonSerializer serializer = Jacksons.newJsonSerializer(new ObjectMapper());
            String mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}";
            Map<String, String> map = serializer.toJson(mapJson).toObject(new TypeRef<Map<String, String>>() {
            });
            //{p1=p1 value, p2=p2 value}
            logger.log(map);

            String stringJson = "abc";
            //"abc"
            logger.log("stringJson: {}", serializer.serialize(stringJson));
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.serialize

    import com.fasterxml.jackson.databind.ObjectMapper
    import org.testng.annotations.Test
    import xyz.srclab.common.reflect.TypeRef
    import xyz.srclab.common.serialize.json.JsonSerializer
    import xyz.srclab.common.serialize.jackson.toJsonSerializer
    import xyz.srclab.common.serialize.json.stringifyJson
    import xyz.srclab.common.serialize.json.toJson
    import xyz.srclab.common.test.TestLogger

    class SerializeSample {

        private val logger = TestLogger.DEFAULT

        @Test
        fun testJsonSerials() {
            val json = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}".toJson()
            val map: Map<String, String> = json.toObject(object : TypeRef<Map<String, String>>() {})
            //{p1=p1 value, p2=p2 value}
            logger.log(map)
            val stringJson = "abc"
            //"abc"
            logger.log("stringJson: {}", stringJson.stringifyJson())
        }

        @Test
        fun testJsonSerializer() {
            val serializer = JsonSerializer.DEFAULT
            val mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}"
            val map: Map<String, String> =
                serializer.toJson(mapJson).toObject(object : TypeRef<Map<String, String>>() {})
            //{p1=p1 value, p2=p2 value}
            logger.log(map)
            val stringJson = "abc"
            //"abc"
            logger.log("stringJson: {}", serializer.serialize(stringJson))
        }

        @Test
        fun testJackson() {
            val serializer: JsonSerializer = ObjectMapper().toJsonSerializer()
            val mapJson = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}"
            val map: Map<String, String> =
                serializer.toJson(mapJson).toObject(object : TypeRef<Map<String, String>>() {})
            //{p1=p1 value, p2=p2 value}
            logger.log(map)
            val stringJson = "abc"
            //"abc"
            logger.log("stringJson: {}", serializer.serialize(stringJson))
        }
    }
