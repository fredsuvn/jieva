package sample.xyz.srclab.common.serialize

import org.testng.annotations.Test
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.serialize.json.toJson
import xyz.srclab.common.test.TestLogger

class SerializeSampleKt {

    @Test
    fun testJsonSerialize() {
        val json = "{\"p1\":\"p1 value\",\"p2\":\"p2 value\"}".toJson()
        val map: Map<String, String> = json.toObject(object : TypeRef<Map<String, String>>() {})
        //{p1=p1 value, p2=p2 value}
        logger.log(map)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}