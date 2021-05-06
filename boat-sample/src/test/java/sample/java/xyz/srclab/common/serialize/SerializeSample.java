package sample.java.xyz.srclab.common.serialize;

import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.serialize.json.Json;
import xyz.srclab.common.serialize.json.JsonSerials;
import xyz.srclab.common.test.TestLogger;

import java.util.Map;

public class SerializeSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testJsonSerialize() {
        Json json = JsonSerials.toJson("{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}");
        Map<String, String> map = json.toObject(new TypeRef<Map<String, String>>() {
        });
        //{p1=p1 value, p2=p2 value}
        logger.log(map);
    }
}
