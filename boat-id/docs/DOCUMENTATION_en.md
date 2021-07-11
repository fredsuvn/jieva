# <span class="image">![Boat Id](../../logo.svg)</span> `boat-id`: Boat Id — Id Generation Lib of [Boat](../../README.md)

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Usage](#_usage)

## Introduction

Boat Id is a simple global unique id generation framework for
[Boat](../../README.md). It provides a set of interface such as
`IdGenerator` and `SnowflakeIdGenerator` to generate global unique id.

## Usage

Boat Id provides `IdGenerator` as core interface to generate any type of
id, `SnowflakeIdGenerator` to generate long type id by **SnowflakeId**.

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
