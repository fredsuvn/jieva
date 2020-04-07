package test.xyz.srclab.common.bean

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import xyz.srclab.common.bean.BeanConverter
import xyz.srclab.common.bean.BeanConverterHandler
import xyz.srclab.common.bean.BeanOperator
import xyz.srclab.common.time.TimeHelper
import java.lang.reflect.Type
import java.time.*
import java.util.*

object BeanConverterTest {

    @Test(dataProvider = "testConvertBasicData")
    fun testConvertBasic(from: Any, to: Class<*>, expected: Any) {
        val converter = BeanConverter.DEFAULT
        doAssertEquals(converter.convert(from, to), expected)
    }

    @DataProvider
    fun testConvertBasicData(): Array<Array<*>> {
        val myDateString = "2001-04-25T16:10:00"
//        val myDateFormatString = "yyyy-MM-dd'T'mm:hh:ss"
        val nowMillis = TimeHelper.nowMillis()
        val nowDate = Date(nowMillis)
        val nowInstant = nowDate.toInstant()
        val myDate = Date.from(LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toInstant())
        return arrayOf(
            arrayOf("123", Int::class.java, 123),
            arrayOf(
                myDateString, LocalDateTime::class.java,
                LocalDateTime.of(2001, 4, 25, 16, 10, 0)
            ),
            arrayOf(myDateString, Date::class.java, myDate),
            arrayOf(nowMillis, Date::class.java, nowDate),
            arrayOf(nowMillis, Instant::class.java, nowInstant),
            arrayOf(
                myDateString,
                Instant::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toInstant()
            ),
            arrayOf(myDateString, LocalDateTime::class.java, LocalDateTime.parse(myDateString)),
            arrayOf(
                myDateString,
                ZonedDateTime::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault())
            ),
            arrayOf(
                myDateString, OffsetDateTime::class.java,
                LocalDateTime.parse(myDateString).atZone(ZoneId.systemDefault()).toOffsetDateTime()
            ),
            arrayOf("10086", Double::class.java, 10086.0)
        )
    }

    val customBeanConverter = BeanConverter.newBuilder()
        .addHandler(object : BeanConverterHandler {
            override fun supportConvert(from: Any, to: Type, beanOperator: BeanOperator): Boolean {
                return true
            }

            override fun convert(from: Any, to: Type, beanOperator: BeanOperator): Any {
                return "6"
            }
        })
        .addHandler(object : BeanConverterHandler {
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
        doAssertEquals(customBeanConverter.convert("s", String::class.java), "6")
        doAssertEquals(customBeanConverter.convert(9, Int::class.java), 9)
    }
}