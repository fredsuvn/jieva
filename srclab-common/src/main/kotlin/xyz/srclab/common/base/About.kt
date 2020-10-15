package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.regex.Pattern
import kotlin.text.toInt as stringToInt

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
    val preRelease: List<Identifier>
        @JvmName("preRelease") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val buildMetadata: List<String>
        @JvmName("buildMetadata") get

    interface Identifier {

        val isNumeric: Boolean

        fun toNumber(): Int

        override fun toString(): String

        companion object {

            internal fun of(value: Int): Identifier {
                return NumericIdentifier(value)
            }

            internal fun of(value: CharSequence): Identifier {
                if (StringUtils.isNumeric(value)) {
                    return of(value.toString().stringToInt())
                }
                checkIdentifierPattern(value)
                return StringIdentifier(value)
            }

            private class NumericIdentifier(private val value: Int) : Identifier {
                override val isNumeric: Boolean = true
                override fun toNumber(): Int = value
                override fun toString(): String = value.toString()

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as NumericIdentifier

                    if (value != other.value) return false

                    return true
                }

                override fun hashCode(): Int {
                    return value
                }
            }

            private class StringIdentifier(private val value: CharSequence) : Identifier {
                override val isNumeric: Boolean = false
                override fun toNumber(): Int = throw IllegalStateException("This Version.Identifier is not numeric.")
                override fun toString(): String = value.toString()

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as StringIdentifier

                    if (value != other.value) return false

                    return true
                }

                override fun hashCode(): Int {
                    return value.hashCode()
                }
            }
        }
    }

    class Builder {

        private var major = 0
        private var minor = 0
        private var patch = 0
        private var preRelease: MutableList<Identifier>? = null
        private var buildMetadata: MutableList<String>? = null

        fun major(value: Int) = apply {
            checkArgument(value >= 0, "Major value should be non-negative.")
            major = value
        }

        fun minor(value: Int) = apply {
            checkArgument(value >= 0, "Minor value should be non-negative.")
            minor = value
        }

        fun patch(value: Int) = apply {
            checkArgument(value >= 0, "Patch value should be non-negative.")
            patch = value
        }

        fun addPreRelease(value: Int) = apply {
            val identifier = Identifier.of(value)
            if (preRelease === null) {
                preRelease = mutableListOf()
            }
            preRelease?.add(identifier)
        }

        fun addPreRelease(value: CharSequence) = apply {
            val identifier = Identifier.of(value)
            if (preRelease === null) {
                preRelease = mutableListOf()
            }
            preRelease?.add(identifier)
        }

        fun addBuildMetadata(value: CharSequence) = apply {
            checkIdentifierPattern(value)
            if (buildMetadata === null) {
                buildMetadata = mutableListOf()
            }
            buildMetadata?.add(value.toString())
        }

        fun build(): Version {
            return VersionImpl(
                major, minor, patch, preRelease ?: emptyList(), buildMetadata ?: emptyList()
            )
        }

        private class VersionImpl(
            override val major: Int,
            override val minor: Int,
            override val patch: Int,
            override val preRelease: List<Identifier>,
            override val buildMetadata: List<String>
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
                if (preRelease.isEmpty() && other.preRelease.isNotEmpty()) {
                    return 1
                }
                if (preRelease.isNotEmpty() && other.preRelease.isEmpty()) {
                    return -1
                }
                val preIt = preRelease.iterator()
                val otherPreIt = other.preRelease.iterator()
                while (preIt.hasNext()) {
                    if (!otherPreIt.hasNext()) {
                        return 1
                    }
                    val preV = preIt.next()
                    val otherPreV = otherPreIt.next()
                    if (preV.isNumeric && !otherPreV.isNumeric) {
                        return -1
                    }
                    if (!preV.isNumeric && otherPreV.isNumeric) {
                        return 1
                    }
                    if (preV.isNumeric && otherPreV.isNumeric) {
                        if (preV.toNumber() > otherPreV.toNumber()) {
                            return 1
                        }
                        if (preV.toNumber() < otherPreV.toNumber()) {
                            return -1
                        }
                        if (preV.toNumber() == otherPreV.toNumber()) {
                            return 0
                        }
                    }
                    return preV.toString().compareTo(otherPreV.toString())
                }
                if (otherPreIt.hasNext()) {
                    return -1
                }
                return 0
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Version) return false

                if (major != other.major) return false
                if (minor != other.minor) return false
                if (patch != other.patch) return false
                if (preRelease != other.preRelease) return false

                return true
            }

            override fun hashCode(): Int {
                var result = major
                result = 31 * result + minor
                result = 31 * result + patch
                result = 31 * result + preRelease.hashCode()
                return result
            }

            override fun toString(): String {
                val buffer = StringBuilder("$major.$minor.$patch")
                if (preRelease.isNotEmpty()) {
                    buffer.append("-$preRelease")
                }
                if (buildMetadata.isNotEmpty()) {
                    buffer.append("+$buildMetadata")
                }
                return buffer.toString()
            }
        }
    }

    companion object {

        @JvmStatic
        val IDENTIFIER_PATTERN: Pattern = Pattern.compile("[0-9A-Za-z-]+")

        @JvmStatic
        fun parse(spec: CharSequence): Version {

            fun parseNormal(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                checkArgument(
                    list.size in 1..3,
                    "Semantic version need 1-3 dot separated parts: x[.y[.z]], now is {}.", list.size
                )
                val major = list[0].let {
                    val result = try {
                        it.stringToInt()
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Wrong major: $it", e)
                    }
                    if (result < 0) {
                        throw IllegalArgumentException("Major must be non-negative integer: $result")
                    }
                    result
                }
                val minor = if (list.size < 2) 0 else {
                    list[1].let {
                        val result = try {
                            it.stringToInt()
                        } catch (e: Exception) {
                            throw IllegalArgumentException("Wrong minor: $it", e)
                        }
                        if (result < 0) {
                            throw IllegalArgumentException("Minor must be non-negative integer: $result")
                        }
                        result
                    }
                }
                val patch = if (list.size < 3) 0 else {
                    list[2].let {
                        val result = try {
                            it.stringToInt()
                        } catch (e: Exception) {
                            throw IllegalArgumentException("Wrong patch: $it", e)
                        }
                        if (result < 0) {
                            throw IllegalArgumentException("Patch must be non-negative integer: $result")
                        }
                        result
                    }
                }
                builder.major(major)
                builder.minor(minor)
                builder.patch(patch)
            }

            fun parsePreVersion(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                for (s in list) {
                    builder.addPreRelease(s)
                }
            }

            fun parseBuildMetadata(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                for (s in list) {
                    builder.addBuildMetadata(s)
                }
            }

            val hyphenCount = HYPHEN_MATCHER.countIn(spec)
            checkArgument(
                hyphenCount == 0 || hyphenCount == 1,
                "Illegal version specification, hyphen count should be 0 or 1."
            )
            val hyphenIndex = HYPHEN_MATCHER.indexIn(spec)
            checkArgument(
                hyphenIndex < 0 || hyphenIndex in 1..spec.length - 2,
                "Illegal hyphen position: {}.", hyphenIndex
            )

            val plusSignCount = PLUS_SIGN_MATCHER.countIn(spec)
            checkArgument(
                plusSignCount == 0 || plusSignCount == 1,
                "Illegal version specification, plus sign count should be 0 or 1."
            )
            val plusSignIndex = PLUS_SIGN_MATCHER.indexIn(spec)
            checkArgument(
                plusSignIndex < 0 || plusSignIndex in 1..spec.length - 2,
                "Illegal plus sign position: {}.", plusSignIndex
            )

            val builder = Builder()
            when {
                hyphenIndex < 0 && plusSignIndex < 0 -> parseNormal(spec, builder)
                hyphenIndex > 0 && plusSignIndex < 0 -> {
                    parseNormal(spec.subSequence(0, hyphenIndex), builder)
                    parsePreVersion(spec.subSequence(hyphenIndex + 1, spec.length), builder)
                }
                hyphenIndex < 0 && plusSignIndex > 0 -> {
                    parseNormal(spec.subSequence(0, plusSignIndex), builder)
                    parsePreVersion(spec.subSequence(plusSignIndex + 1, spec.length), builder)
                }
                hyphenIndex > 0 && plusSignIndex > 0 && (plusSignIndex - hyphenIndex > 1) -> {
                    parseNormal(spec.subSequence(0, hyphenIndex), builder)
                    parsePreVersion(spec.subSequence(hyphenIndex + 1, plusSignIndex), builder)
                    parsePreVersion(spec.subSequence(plusSignIndex + 1, spec.length), builder)
                }
                else -> throw IllegalArgumentException("Illegal version specification: $spec")
            }
            return builder.build()
        }

        private fun checkIdentifierPattern(value: CharSequence) {
            checkArgument(
                IDENTIFIER_PATTERN.matcher(value).matches(),
                "Value of Version.Identifier should be ${IDENTIFIER_PATTERN.pattern()}."
            )
        }
    }
}

interface PoweredBy {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val name: String
        @JvmName("name") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val url: String
        @JvmName("url") get
}

interface Licence {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val name: String
        @JvmName("name") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val url: String
        @JvmName("url") get

    @Suppress("INAPPLICABLE_JVM_NAME")
    val content: String
        @JvmName("content") get
}

interface Egg {

    @Suppress("INAPPLICABLE_JVM_NAME")
    val tips: String
        @JvmName("tips") get
}

fun CharSequence.parseVersion(): Version {
    return Version.parse(this)
}

/*
 * Content:
 */