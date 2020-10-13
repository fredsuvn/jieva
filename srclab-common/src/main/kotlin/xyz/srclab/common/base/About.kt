package xyz.srclab.common.base

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author sunqian
 */
enum class About(
    @get:JvmName("product") val product: String,
    @get:JvmName("version") val version: String,
    @get:JvmName("releaseDate") val releaseDate: ZonedDateTime,
    @get:JvmName("poweredBy") val poweredBy: String,
    @get:JvmName("license") val license: String,
    @get:JvmName("eggTips") val eggTips: String,
) {

    V0(
        "Boat",
        "0.0.0",
        ZonedDateTime.of(
            LocalDate.of(2020, 10, 17), LocalTime.MIN, ZoneId.of("Asia/Shanghai")
        ),
        "srclab.xyz",
        "Apache 2.0 license [https://www.apache.org/licenses/LICENSE-2.0.txt]",
        """
            直长多行忽闲将欲拔停玉金
            挂风歧路复来登渡剑杯盘樽
            云破路难乘垂太黄四投珍清
            帆浪，，舟钓行河顾箸羞酒
            济会今行梦碧雪冰心不直斗
            沧有安路日溪满塞茫能万十
            海时在难边上山川然食钱千
        """.trimIndent()
    ),
    ;

    companion object {

        @JvmStatic
        val current: About
            @JvmName("current") get() = V0
    }

    override fun toString(): String {
        return "$product $version${Defaults.lineSeparator}" +
                "release on $releaseDate${Defaults.lineSeparator}" +
                "powered by $poweredBy${Defaults.lineSeparator}" +
                "license: $license"
    }
}

fun aboutBoat(): About {
    return About.current
}