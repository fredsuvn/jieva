package test.xyz.srclab.common.serialize.json

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.serialize.json.jsonToObject
import xyz.srclab.common.serialize.json.toJsonString
import xyz.srclab.common.test.TestLogger

class KotlinDataClassTest {

    private val logger = TestLogger.DEFAULT

    @Test
    fun testKotlinDataClass() {
        val data = Data("1", "2", "3")
        val json = data.toJsonString()
        logger.log("json: {}", json)
        Assert.assertEquals(json, "{\"data1\":\"1\",\"data2\":\"2\",\"data3\":\"3\"}")
        val data2 = json.jsonToObject(Data::class.java)
        logger.log("data2: {}", data2)
        Assert.assertEquals(data2, data)
    }
}