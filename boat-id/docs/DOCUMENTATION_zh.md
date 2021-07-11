# <span class="image">![Boat Id](../../logo.svg)</span> `boat-id`: Boat Id — [Boat](../../README.md) ID生成库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

目录

-   [简介](#_简介)
-   [Usage](#_usage)

## 简介

Boat Id 是一个简单的基于 [Boat](../../README.md) 的全局唯一id生成框架.
它提供一组接口来生成全局唯一id, 如 `IdGenerator` 和
`SnowflakeIdGenerator`.

## Usage

Boat Id 提供 `IdGenerator` 作为核心接口来生成任何类型的id,
`SnowflakeIdGenerator` 来生成基于 **雪花算法** 的id.

Java Examples

    package sample.java.xyz.srclab.id;

    import org.testng.annotations.Test;
    import xyz.srclab.common.id.IdGenerator;
    import xyz.srclab.common.id.SnowflakeIdGenerator;
    import xyz.srclab.common.lang.Nums;
    import xyz.srclab.common.test.TestLogger;

    import java.util.UUID;

    public class IdSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testSnowflake() {
            SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(1);
            for (int i = 0; i < 10; i++) {
                long id = snowflakeIdGenerator.next();
                //Snowflake: 6819769124932030464 : 0101111010100100101011111110001011101101110000000001000000000000
                logger.log("Snowflake: " + id + " : " + Nums.toBinaryString(id));
            }
        }

        @Test
        public void testIdGenerator() {
            IdGenerator<String, String, String, String> idGenerator = IdGenerator.newIdGenerator(
                () -> UUID.randomUUID().toString(),
                l -> l.substring(0, 10),
                i -> i.substring(11, 15),
                (l, i) -> l + "-" + i
            );
            for (int i = 0; i < 10; i++) {
                String id = idGenerator.next();
                //IdGenerator: 4f8c8c34-2-83-4
                logger.log("IdGenerator: " + id);
            }
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.id

    import org.testng.annotations.Test
    import xyz.srclab.common.id.IdGenerator
    import xyz.srclab.common.id.SnowflakeIdGenerator
    import xyz.srclab.common.lang.toBinaryString
    import xyz.srclab.common.test.TestLogger
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
