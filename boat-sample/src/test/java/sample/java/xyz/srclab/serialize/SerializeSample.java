package sample.java.xyz.srclab.serialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.data.json.Json;
import xyz.srclab.common.data.json.JsonParser;
import xyz.srclab.common.data.json.JsonSerials;
import xyz.srclab.common.data.json.jackson.Jacksons;
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
        JsonParser serializer = JsonParser.DEFAULT;
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
        JsonParser serializer = Jacksons.newJsonSerializer(new ObjectMapper());
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
