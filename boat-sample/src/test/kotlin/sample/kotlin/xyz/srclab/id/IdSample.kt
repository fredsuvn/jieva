package sample.kotlin.xyz.srclab.id

import org.testng.annotations.Test
import xyz.srclab.common.id.IdGenerator
import xyz.srclab.common.id.SnowflakeIdGenerator
import xyz.srclab.common.lang.toBinaryString
import java.util.*

class IdSample {

    private val logger = TestLogger.DEFAULT


    @Test
    fun testSnowflake() {
        val snowflakeIdGenerator = SnowflakeIdGenerator(1)
        for (i in 0..9) {
            val id = snowflakeIdGenerator.next()
            //Snowflake: 6819769124932030464 : 0101111010100100101011111110001011101101110000000001000000000000
            logger.log("Snowflake: $id : ${id.toBinaryString()}")
        }
    }

    @Test
    fun testIdGenerator() {
        val idGenerator = IdGenerator.newIdGenerator(
            { UUID.randomUUID().toString() },
            { l: String -> l.substring(0, 10) },
            { i: String -> i.substring(11, 15) }
        ) { l: String, i: String -> "$l-$i" }
        for (i in 0..9) {
            val id = idGenerator.next()
            //IdGenerator: 4f8c8c34-2-83-4
            logger.log("IdGenerator: $id")
        }
    }
}