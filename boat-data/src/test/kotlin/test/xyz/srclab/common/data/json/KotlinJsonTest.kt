package test.xyz.srclab.common.data.json

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.base.info
import xyz.srclab.common.data.json.parseJson
import xyz.srclab.common.data.json.toJsonString

class KotlinDataClassTest {

    @Test
    fun testKotlinData() {
        val data = Data("1", "2", "3")
        val json = data.toJsonString()
        info("json: {}", json)
        Assert.assertEquals(json, "{\"data1\":\"1\",\"data2\":\"2\",\"data3\":\"3\"}")
        val data2 = json.parseJson(Data::class.java)
        info("data2: {}", data2)
        Assert.assertEquals(data2, data)
    }
}

data class Data(
    val data1: String,
    val data2: String,
    val data3: String,
)