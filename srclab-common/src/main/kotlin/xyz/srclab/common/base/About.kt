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

interface Version : Comparable<Version> {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val major: Int
        @JvmName("major") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val minor: Int
        @JvmName("minor") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val patch: Int
        @JvmName("patch") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val preRelease: List<String>
        @JvmName("preRelease") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val build: List<String>
        @JvmName("build") get

    class Builder {

        private var major = 0
        private var minor = 0
        private var patch = 0
        private var preRelease: List<String>? = null
        private var build: List<String>? = null

        fun major(value: Int) = apply {
            major = value
        }

        fun minor(value: Int) = apply {
            minor = value
        }

        fun patch(value: Int) = apply {
            patch = value
        }


        fun build(): Version {
            return object : Version {



            }
        }

        private class VersionImpl(
            override val major: Int,
            override val minor: Int,
            override val patch: Int,
            override val preRelease: List<String>,
            override val build: List<String>
        ) : Version {

            override fun compareTo(other: Version): Int {
                if (major > other.major) {
                    return 1
                }
                if (major < other.major) {
                    return -1
                }
                if (minor > other.minor) {
                    return 1
                }
                if (minor < other.minor) {
                    return -1
                }
                if (patch > other.patch) {
                    return 1
                }
                if (patch < other.patch) {
                    return -1
                }
                if (preRelease.isEmpty() && other.preRelease.isEmpty()) {
                    return 0
                }
                if (preRelease.isNotEmpty() && other.preRelease.isEmpty()) {
                    return 0
                }
                if (preRelease.isEmpty() && other.preRelease.isNotEmpty()) {
                    return 0
                }
            }
        }
    }

    companion object {


    }
}

fun aboutBoat(): About {
    return About.current
}