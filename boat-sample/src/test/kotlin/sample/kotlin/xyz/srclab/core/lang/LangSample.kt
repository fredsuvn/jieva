package sample.kotlin.xyz.srclab.core.lang

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.base.*
import xyz.srclab.common.lang.*
import xyz.srclab.common.lang.CharsFormat.Companion.messageFormat
import xyz.srclab.common.lang.CharsFormat.Companion.printfFormat
import xyz.srclab.common.lang.CharsTemplate.Companion.resolveTemplate
import xyz.srclab.common.lang.LazyToString.Companion.lazyToString
import xyz.srclab.common.lang.Processing.Companion.newProcessing
import xyz.srclab.common.lang.SpecParser.Companion.parseFirstClassNameToInstance
import xyz.srclab.common.utils.Counter.Companion.counterStarts
import java.math.BigDecimal
import java.util.*
import kotlin.text.toBigDecimal
import kotlin.text.toBoolean

class BaseSample {

    @Test
    fun testCurrent() {
        //null
        logger.log(Current.getOrNull("1"))
        Current.set("1", "2")
        //2
        logger.log(Current.get<Any>("1"))
        //System.currentTimeMillis();
        logger.log(Current.millis)
    }

    @Test
    fun testDefault() {
        //UTF-8
        logger.log(Defaults.charset)
        //Locale.getDefault();
        logger.log(Defaults.locale)
    }

    @Test
    fun testEnvironment() {
        logger.log(Environment.getProperty(Environment.OS_ARCH_KEY))
        logger.log(Environment.availableProcessors)
        logger.log(Environment.osVersion)
        logger.log(Environment.isOsWindows)
    }

    @Test
    fun testCharsFormat() {
        val byFast = "1, 2, {}".fastFormat(3)
        val byMessage = "1, 2, {0}".messageFormat(3)
        val byPrintf = "1, 2, %d".printfFormat(3)
        //1, 2, 3
        logger.log("byFast: {}", byFast)
        logger.log("byMessage: {}", byMessage)
        logger.log("byPrintf: {}", byPrintf)
    }

    @Test
    fun testCharsTemplate() {
        val args: MutableMap<Any, Any?> = HashMap()
        args["name"] = "Dog"
        args["name}"] = "DogX"
        args[1] = "Cat"
        args[2] = "Bird"
        val template1 = "This is a {name}, that is a {}".resolveTemplate("{", "}")
        //This is a Dog, that is a Cat
        logger.log(template1.process(args))
        val template2 = "This is a } {name}, that is a {}}".resolveTemplate("{", "}")
        //This is a } Dog, that is a Cat}
        logger.log(template2.process(args))
        val template3 = "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\".resolveTemplate("{", "}", "\\")
        //This is a } {DogX (Dog), that is a Bird\{\
        logger.log(template3.process(args))
    }

    @Test
    fun testNamingCase() {
        val upperCamel = "UpperCamel"
        val lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL)
        //upperCamel
        logger.log("lowerCamel: {}", lowerCamel)
    }

    @Test
    fun testLazyString() {
        val counter = 0.counterStarts()
        val lazyToString = lazyOf { counter.getAndIncrementInt() }.lazyToString()
        //0
        logger.log("lazyToString: {}", lazyToString)
    }

    @Test
    fun testLoaders() {
        val cls = "[[[Ljava.lang.String;".loadClass<Array<Array<Array<String>>>>()
        //class [[[Ljava.lang.String;
        logger.log("cls: {}", cls)
    }

    @Test
    fun testSpecParser() {
        val s = "java.lang.String".parseFirstClassNameToInstance<String>()
        //an empty String
        logger.log("s: {}", s)
    }

    @Test
    fun testBaseTypes() {

        //Anys examples:
        val lists = arrayOf<List<*>>().asAny<Array<List<String>>>()
        val hash = Arrays.asList("", 1).anyOrArrayHash()
        val equals = Arrays.asList("", 1).anyOrArrayEquals(Arrays.asList("", 1))

        //Chars examples:
        val bytes = "message10086".toByteArray()
        val toChars = bytes.toChars()
        val toBytes = toChars.toBytes()
        //message10086
        logger.log("toChars: {}", toChars)
        //[109, 101, 115, 115, 97, 103, 101, 49, 48, 48, 56, 54]
        logger.log("toBytes: {}", toBytes)

        //Nums examples:
        val n = "110".toBigDecimal()
        val i = BigDecimal("2333").toInt()
        //110
        logger.log("n: {}", n)
        //2333
        logger.log("i: {}", i)

        //Bools examples:
        val b = "true".toBoolean()
        //true
        logger.log("b: {}", b)

        //Dates examples:
        val timestamp = timestamp()
        val localDateTime = "2011-12-03T10:15:30".toLocalDateTime()
        //20210207144816045
        logger.log("timestamp: {}", timestamp)
        //2011-12-03T10:15:30
        logger.log("localDateTime: {}", localDateTime)

        //Compares example:
        //99
        logger.log("inBounds: {}", 100.between(0, 99))

        //Checks examples:
        try {
            checkArgument(1 == 2, "1 != 2")
        } catch (e: IllegalArgumentException) {
            //java.lang.IllegalArgumentException: 1 != 2
            logger.log("e: {}", e)
        }

        //Requires examples:
        try {
            val notNull = null.notNull<Any>("null")
        } catch (e: NullPointerException) {
            //java.lang.NullPointerException: null
            logger.log("e: {}", e)
        }

        //Enums examples:
        val t1: TestEnum = TestEnum::class.java.valueOfEnum("T1")
        //t1: T1
        logger.log("t1: {}", t1)
        val t2: TestEnum = TestEnum::class.java.valueOfEnumIgnoreCase("t2")
        //t2: T2
        logger.log("t2: {}", t2)
    }


    @Test
    fun testRandom() {
        //[10, 20)
        for (j in 0..9) {
            logger.log("random[10, 20): {}", randomBetween(10, 20))
        }

        val randomSupplier = RandomSupplier.newBuilder<Any>()
            .score(20, "A")
            .score(20, "B")
            .score(60, "C")
            .build()
        var countA = 0
        var countB = 0
        var countC = 0
        for (i in 0..999) {
            val result = randomSupplier.get()
            if (result == "A") {
                countA++
            } else if (result == "B") {
                countB++
            } else if (result == "C") {
                countC++
            }
        }
        val total = countA + countB + countC
        Assert.assertEquals(total, 1000)
        //countA: 189, countB: 190, countC: 621, total: 1000
        logger.log("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total)
    }

    @Test
    fun testCachingProductBuilder() {

        class CachingBuilderSample : CacheableBuilder<String>() {

            private var value = "null"
            private var counter = 0L

            fun setValue(value: String) {
                this.value = value
                commitModification()
            }

            override fun buildNew(): String {
                return "$value${counter++}"
            }
        }

        val cachingBuilderSample = CachingBuilderSample()
        cachingBuilderSample.setValue("1")
        val value1 = cachingBuilderSample.build()
        val value2 = cachingBuilderSample.build()
        cachingBuilderSample.setValue("2")
        val value3 = cachingBuilderSample.build()
        //10
        logger.log("value1: {}", value1)
        //10
        logger.log("value2: {}", value2)
        //21
        logger.log("value3: {}", value3)
    }

    @Test
    fun testProcess() {
        if (Environment.isOsUnix) {
            testProcessing("echo", "ECHO_CONTENT")
        }
        if (Environment.isOsWindows) {
            testProcessing("cmd.exe", "/c", "echo " + "ECHO_CONTENT")
        }
    }

    private fun testProcessing(vararg command: String) {
        val processing = newProcessing(*command)
        processing.waitForTermination()
        val output = processing.outputString()
        //ECHO_CONTENT
        logger.log(output)
    }

    @Test
    fun testShell() {
        logger.log("Hello, world!")
        logger.log("123{}456{}{}", EscChars.linefeed, EscChars.newline, EscChars.reset)
        logger.log(
            "{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        )
        logger.log(
            "{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        )
        logger.log(
            "{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        )
        logger.log(CtlChars.beep)
        //logger.log("123\010456\007");
        logger.log("123{}456{}", CtlChars.backspaces, CtlChars.beep)
    }

    @Test
    fun testSingleAccessor() {
        val singleAccessor = TestObjAccessor()
        Assert.assertNull(singleAccessor.getOrNull())
        Assert.assertEquals("666", singleAccessor.getOrElse("666"))
        Assert.assertEquals("666", singleAccessor.getOrElse { "666" })
        singleAccessor.set("777")
        Assert.assertEquals("777", singleAccessor.get())
        val genericSingleAccessor = TestTypedAccessor()
        Assert.assertNull(genericSingleAccessor.getOrNull())
        Assert.assertEquals("666", genericSingleAccessor.getOrElse("666"))
        Assert.assertEquals("666", genericSingleAccessor.getOrElse { "666" })
        genericSingleAccessor.set("777")
        Assert.assertEquals("777", genericSingleAccessor.get())
        val mapAccessor = TestMapAccessor()
        Assert.assertNull(mapAccessor.getOrNull("1"))
        Assert.assertEquals("666", mapAccessor.getOrElse("1", "666"))
        Assert.assertEquals("666", mapAccessor.getOrElse("1") { k: Any? -> "666" })
        mapAccessor.set("1", "777")
        Assert.assertEquals("777", mapAccessor.get("1"))
        val genericMapAccessor = TestTypedMapAccessor()
        Assert.assertNull(genericMapAccessor.getOrNull("1"))
        Assert.assertEquals("666", genericMapAccessor.getOrElse("1", "666"))
        Assert.assertEquals("666", genericMapAccessor.getOrElse("1") { k: String? -> "666" })
        genericMapAccessor.set("1", "777")
        Assert.assertEquals("777", genericMapAccessor.get("1"))
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}

enum class TestEnum {
    T1, T2
}

class TestObjAccessor : ObjAccessor {
    private var value: String? = null
    override fun <T : Any> getOrNull(): T? {
        return value as T?
    }

    override fun set(value: Any?) {
        this.value = value as String?
    }
}

class TestTypedAccessor : TypedAccessor<String> {
    private var value: String? = null
    override fun getOrNull(): String? {
        return value
    }

    override fun set(value: String?) {
        this.value = value
    }
}

class TestMapAccessor : MapAccessor {
    private val values: MutableMap<Any, Any?> = HashMap()
    override val contents: MutableMap<Any, Any?> = values
}

class TestTypedMapAccessor : TypedMapAccessor<String, String> {
    private val values: MutableMap<String, String?> = HashMap()
    override val contents: MutableMap<String, String?> = values
}