package test.xyz.srclab.common.bean

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import xyz.srclab.common.base.Loader
import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.ConvertHandler
import xyz.srclab.common.bean.BeanKit
import xyz.srclab.common.bean.BeanOperator
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.test.doAssertEquals
import xyz.srclab.test.doExpectThrowable
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*

object ConverterTest {

    @Test(dataProvider = "testConvertClassData")
    fun testConvertClass(from: Any, to: Type, expected: Any) {
        val converter = Converter.DEFAULT
        val operator = BeanOperator.DEFAULT
        doAssertEquals(converter.convert(from, to), expected)
        doAssertEquals(converter.convert(from, to, operator), expected)
        doAssertEquals(converter.convert(from, to as Class<*>), expected)
        doAssertEquals(converter.convert(from, to as Type, operator), expected)
        doAssertEquals(converter.convert(from, TypeRef.of(to)), expected)
        doAssertEquals(converter.convert(from, TypeRef.of(to), operator), expected)
    }

    @DataProvider
    fun testConvertClassData(): Array<Array<*>> {
        val myDateString = "2001-04-25T16:10:00"
        val nowMillis = Loader.currentMillis()
        val nowDate = Date(nowMillis)
        val nowInstant = nowDate.toInstant()
        val myDate = Date.from(LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toInstant())
        val c = C()
        c.cc = "ccc"
        val d = D()
        d.cc = "ccc"
        return arrayOf(
            arrayOf(123, String::class.java, "123"),
            arrayOf("true", Boolean::class.java, true),
            arrayOf("123", Int::class.java, 123),
            arrayOf("123", Long::class.java, 123L),
            arrayOf("1", Char::class.java, '1'),
            arrayOf("123", Byte::class.java, 123.toByte()),
            arrayOf("123", Float::class.java, 123f),
            arrayOf("123", Double::class.java, 123.0),
            arrayOf("123", Short::class.java, 123.toShort()),
            arrayOf(123L, Int::class.java, 123),
            arrayOf(123, Long::class.java, 123L),
            arrayOf(123, Char::class.java, 123.toChar()),
            arrayOf(123, Byte::class.java, 123.toByte()),
            arrayOf(123, Float::class.java, 123f),
            arrayOf(123, Double::class.java, 123.0),
            arrayOf(123, Short::class.java, 123.toShort()),
            arrayOf(123, BigInteger::class.java, BigInteger("123")),
            arrayOf(123, BigDecimal::class.java, BigDecimal("123")),
            arrayOf(BigInteger("123"), BigDecimal::class.java, BigDecimal("123")),
            arrayOf(myDateString, Date::class.java, myDate),
            arrayOf(nowMillis, Date::class.java, nowDate),
            arrayOf(Instant.ofEpochMilli(nowMillis), Date::class.java, nowDate),
            arrayOf(LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault()), Date::class.java, nowDate),
            arrayOf(nowMillis, Instant::class.java, nowInstant),
            arrayOf(ZonedDateTime.ofInstant(nowInstant, ZoneId.systemDefault()), Instant::class.java, nowInstant),
            arrayOf(LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault()), Instant::class.java, nowInstant),
            arrayOf(nowMillis, Duration::class.java, Duration.ofMillis(nowMillis)),
            arrayOf("PT15S", Duration::class.java, Duration.ofSeconds(15)),
            arrayOf(
                myDateString,
                Instant::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toInstant()
            ),
            arrayOf(myDateString, LocalDateTime::class.java, LocalDateTime.parse(myDateString)),
            arrayOf(
                myDateString, LocalDateTime::class.java,
                LocalDateTime.of(2001, 4, 25, 16, 10, 0)
            ),
            arrayOf(
                nowMillis, LocalDateTime::class.java,
                LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault())
            ),
            arrayOf(
                myDateString,
                ZonedDateTime::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault())
            ),
            arrayOf(
                LocalDateTime.parse(myDateString),
                ZonedDateTime::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault())
            ),
            arrayOf(
                nowMillis,
                ZonedDateTime::class.java,
                LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault()).atZone(ZoneId.systemDefault())
            ),
            arrayOf(
                myDateString, OffsetDateTime::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toOffsetDateTime()
            ),
            arrayOf("10086", Double::class.java, 10086.0),
            arrayOf(
                A("123"), Map::class.java,
                mapOf("class" to A::class.java, "name" to "123")
            ),
            arrayOf(
                arrayOf(A("123")), List::class.java,
                listOf(A("123"))
            ),
            arrayOf(
                arrayOf(A("123")), Set::class.java,
                setOf(A("123"))
            ),
            arrayOf(
                setOf(A("123")), Array<A>::class.java,
                arrayOf(A("123"))
            ),
            arrayOf(
                arrayOf(A("123")), Array<A>::class.java,
                arrayOf(A("123"))
            ),
            arrayOf(
                listOf(c, c), Array<D>::class.java,
                arrayOf(d, d)
            )
        )
    }

    @Test(dataProvider = "testConvertTypeData")
    fun testConvertType(from: Any, to: TypeRef<*>, expected: Any) {
        val converter = Converter.DEFAULT
        val operator = BeanOperator.DEFAULT
        doAssertEquals(converter.convert(from, to.type), expected)
        doAssertEquals(converter.convert(from, to.type, operator), expected)
    }

    @DataProvider
    fun testConvertTypeData(): Array<Array<*>> {
        val bString = B<String>()
        bString.t = "123"
        val bInt = B<Int>()
        bInt.t = 123
        return arrayOf(
            arrayOf(
                mapOf("name" to "123"), object : TypeRef<Map<String, Int>>() {},
                mapOf("name" to 123)
            ),
            arrayOf(
                arrayOf("123"), object : TypeRef<List<Int>>() {},
                listOf(123)
            ),
            arrayOf(
                arrayOf("123"), object : TypeRef<Set<Int>>() {},
                setOf(123)
            ),
            arrayOf(
                listOf("123"), object : TypeRef<List<Int>>() {},
                listOf(123)
            ),
            arrayOf(
                listOf("123"), object : TypeRef<Set<Int>>() {},
                setOf(123)
            ),
            arrayOf(
                setOf("123"), object : TypeRef<Array<Int>>() {},
                arrayOf(123)
            ),
            arrayOf(
                setOf(mapOf("name" to "123")), object : TypeRef<Array<Map<String, Int>>>() {},
                arrayOf(mapOf("name" to 123))
            ),
            arrayOf(
                setOf(bString), object : TypeRef<Array<B<Int>>>() {},
                arrayOf(bInt)
            )
        )
    }

    @Test
    fun testConvertGeneric() {
        val b1 = B<String>()
        b1.t = "999"
        doAssertEquals(Converter.DEFAULT.convert(b1, object : TypeRef<B<Int>>() {}).t, 999)
    }

    @Test
    fun testUnsupported() {
        doExpectThrowable(java.lang.UnsupportedOperationException::class.java) {
            BeanKit.convert("", List::class.java)
        }
        doExpectThrowable(java.lang.UnsupportedOperationException::class.java) {
            BeanKit.convert("", Set::class.java)
        }
        doExpectThrowable(java.lang.UnsupportedOperationException::class.java) {
            BeanKit.convert("", Array<Any>::class.java)
        }
        doExpectThrowable(java.lang.UnsupportedOperationException::class.java) {
            BeanKit.convert("123", Char::class.java)
        }
    }

    val customBeanConverter = Converter.newBuilder()
        .setBeanOperator(BeanOperator.DEFAULT)
        .addHandler(object : ConvertHandler {
            override fun supportConvert(from: Any, to: Type, beanOperator: BeanOperator): Boolean {
                return from is Int || from is String
            }

            override fun convert(from: Any, to: Type, beanOperator: BeanOperator): Any {
                return "6"
            }
        })
        .addHandler(object : ConvertHandler {
            override fun supportConvert(from: Any, to: Type, beanOperator: BeanOperator): Boolean {
                return from is Int
            }

            override fun convert(from: Any, to: Type, beanOperator: BeanOperator): Any {
                return 9
            }
        })
        .build();

    @Test
    fun testCustomConverter() {
        val operator = BeanOperator.DEFAULT
        doAssertEquals(customBeanConverter.convert("s", String::class.java), "6")
        doAssertEquals(
            customBeanConverter.convert(
                "s",
                String::class.java,
                operator
            ), "6"
        )
        doAssertEquals(customBeanConverter.convert(9, Int::class.java), 9)
        doAssertEquals(customBeanConverter.convert(9, Int::class.java, operator), 9)
        doExpectThrowable(UnsupportedOperationException::class.java) {
            customBeanConverter.convert(BigDecimal.ONE, Object::class.java)
        }
    }

    @Test
    fun testEmptyConverter() {
        val emptyBeanConverter = Converter.newBuilder()
            .setBeanOperator(BeanOperator.DEFAULT)
            .addHandler(ConvertHandler.DEFAULT)
            .build()
        doAssertEquals(emptyBeanConverter.convert(9, Int::class.java), 9)
    }

    data class A(val name: String)

    class B<T> {
        var t: T? = null

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as B<*>

            if (t != other.t) return false

            return true
        }

        override fun hashCode(): Int {
            return t?.hashCode() ?: 0
        }
    }

    class C {
        var cc: String? = null
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as C

            if (cc != other.cc) return false

            return true
        }

        override fun hashCode(): Int {
            return cc?.hashCode() ?: 0
        }
    }

    class D {
        var cc: String? = null
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as D

            if (cc != other.cc) return false

            return true
        }

        override fun hashCode(): Int {
            return cc?.hashCode() ?: 0
        }
    }
}