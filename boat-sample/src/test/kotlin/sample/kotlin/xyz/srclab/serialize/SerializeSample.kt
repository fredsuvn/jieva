package sample.kotlin.xyz.srclab.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import org.testng.annotations.Test
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.data.json.JsonParser
import xyz.srclab.common.data.json.jackson.toJsonSerializer
import xyz.srclab.common.data.json.stringifyJson
import xyz.srclab.common.data.json.toJson

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
        val serializer = JsonParser.DEFAULT
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
        val serializer: JsonParser = ObjectMapper().toJsonSerializer()
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