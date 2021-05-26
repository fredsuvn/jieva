package sample.java.xyz.srclab.serialize;

import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.serialize.json.Jacksons;
import xyz.srclab.common.serialize.json.Json;
import xyz.srclab.common.serialize.json.JsonSerializer;
import xyz.srclab.common.serialize.json.JsonSerials;
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
        JsonSerializer serializer = Jacksons.newJsonSerializer(Jacksons.DEFAULT_OBJECT_MAPPER);
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
