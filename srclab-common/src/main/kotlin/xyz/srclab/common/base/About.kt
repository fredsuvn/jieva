package xyz.srclab.common.base

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author sunqian
 */
interface About {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val name: String
        @JvmName("name") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val version: String
        @JvmName("version") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val releaseDate: ZonedDateTime
        @JvmName("releaseDate") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val poweredBy: String
        @JvmName("poweredBy") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val license: String
        @JvmName("license") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val eggTips: String
        @JvmName("eggTips") get

    companion object {

        @JvmStatic
        val current: About
            @JvmName("current") get() = V0

        @JvmStatic
        val v0: About
            @JvmName("v0") get() = V0

        private abstract class BaseAbout : About {

            override val name = "srclab-common"

            override val releaseDate = ZonedDateTime.of(
                buildReleaseDate(),
                LocalTime.MIN,
                ZoneId.of("Asia/Shanghai")
            ).asNotNull()

            override val poweredBy = "srclab.xyz"

            override val license = "Apache 2.0 license [https://www.apache.org/licenses/LICENSE-2.0.txt]"

            abstract fun buildReleaseDate(): LocalDate

            override fun toString(): String {
                return "$name $version $releaseDate" +
                        Defaults.lineSeparator +
                        "Powered by: $poweredBy" +
                        Defaults.lineSeparator +
                        "License: $license" +
                        Defaults.lineSeparator +
                        eggTips
            }
        }

        private object V0 : BaseAbout() {

            override fun buildReleaseDate(): LocalDate = LocalDate.of(2020, 10, 11)

            override val version = "0.0.0"

            override val eggTips = """
                直长多行忽闲将欲拔停玉金
                挂风歧路复来登渡剑杯盘樽
                云破路难乘垂太黄四投珍清
                帆浪，，舟钓行河顾箸羞酒
                济会今行梦碧雪冰心不直斗
                沧有安路日溪满塞茫能万十
                海时在难边上山川然食钱千
            """.trimIndent()
        }
    }
}