package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import xyz.srclab.jvm.compile.INAPPLICABLE_JVM_NAME
import java.time.ZonedDateTime
import java.util.regex.Pattern
import kotlin.text.toInt as toIntKt

interface About {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val url: String
        @JvmName("url") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val version: Version
        @JvmName("version") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val licence: Licence
        @JvmName("licence") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val poweredBy: PoweredBy
        @JvmName("poweredBy") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val report: String
        @JvmName("report") get() {
            return "${poweredBy.mail} or $url"
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val eggTips: String
        @JvmName("eggTips") get

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            url: String,
            version: Version,
            licence: Licence,
            poweredBy: PoweredBy,
            eggTips: String? = null,
        ): About {
            return AboutImpl(name, url, version, licence, poweredBy, eggTips ?: "None")
        }

        private class AboutImpl(
            override val name: String,
            override val url: String,
            override val version: Version,
            override val licence: Licence,
            override val poweredBy: PoweredBy,
            override val eggTips: String,
        ) : About {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is About) return false
                if (name != other.name) return false
                if (url != other.url) return false
                if (version != other.version) return false
                if (licence != other.licence) return false
                if (poweredBy != other.poweredBy) return false
                if (eggTips != other.eggTips) return false
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + url.hashCode()
                result = 31 * result + version.hashCode()
                result = 31 * result + licence.hashCode()
                result = 31 * result + poweredBy.hashCode()
                result = 31 * result + eggTips.hashCode()
                return result
            }

            override fun toString(): String {
                return """
                    $name $version,release on ${version.releaseDate}
                    $url
                    Under the $licence
                    Powered by $poweredBy
                """.trimIndent()
            }
        }
    }
}

interface Version : Comparable<Version> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val releaseDate: ZonedDateTime
        @JvmName("releaseDate") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val major: Int
        @JvmName("major") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val minor: Int
        @JvmName("minor") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val patch: Int
        @JvmName("patch") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val preRelease: List<Identifier>
        @JvmName("preRelease") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val preReleaseToString: String
        @JvmName("preReleaseToString") get() {
            return preRelease.joinToString("")
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val buildMetadata: List<String>
        @JvmName("buildMetadata") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val buildMetadataToString: String
        @JvmName("buildMetadataToString") get() {
            return buildMetadata.joinToString("")
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isNormal: Boolean
        @JvmName("isNormal") get() {
            return preRelease.isEmpty()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val isPreRelease: Boolean
        @JvmName("isPreRelease") get() {
            return preRelease.isNotEmpty()
        }

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
                    return of(value.toString().toIntKt())
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

        private var releaseDate: ZonedDateTime = ZonedDateTime.now()
        private var major = 0
        private var minor = 0
        private var patch = 0
        private var preRelease: MutableList<Identifier>? = null
        private var buildMetadata: MutableList<String>? = null

        fun releaseDate(value: ZonedDateTime) = apply {
            releaseDate = value
        }

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

        fun addPreRelease(values: List<Any>) = apply {
            for (value in values) {
                if (value is Number) {
                    addPreRelease(value.toInt())
                } else {
                    addPreRelease(value.toString())
                }
            }
        }

        fun addBuildMetadata(value: CharSequence) = apply {
            checkIdentifierPattern(value)
            if (buildMetadata === null) {
                buildMetadata = mutableListOf()
            }
            buildMetadata?.add(value.toString())
        }

        fun addBuildMetadata(values: List<CharSequence>) = apply {
            for (value in values) {
                addBuildMetadata(value)
            }
        }

        fun build(): Version {
            return VersionImpl(
                releaseDate,
                major,
                minor,
                patch,
                preRelease ?: emptyList(),
                buildMetadata ?: emptyList(),
            )
        }

        private class VersionImpl(
            override val releaseDate: ZonedDateTime,
            override val major: Int,
            override val minor: Int,
            override val patch: Int,
            override val preRelease: List<Identifier>,
            override val buildMetadata: List<String>,
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

        val IDENTIFIER_PATTERN: Pattern = Pattern.compile("[0-9A-Za-z-]+")

        @JvmStatic
        fun identifierPattern(): Pattern = IDENTIFIER_PATTERN

        @JvmStatic
        @JvmOverloads
        fun of(
            releaseDate: ZonedDateTime = ZonedDateTime.now(),
            major: Int,
            minor: Int,
            patch: Int,
            preRelease: List<Any> = emptyList(),
            buildMetadata: List<String> = emptyList(),
        ): Version {
            return Builder()
                .releaseDate(releaseDate)
                .major(major)
                .minor(minor)
                .patch(patch)
                .addPreRelease(preRelease)
                .addBuildMetadata(buildMetadata)
                .build()
        }

        @JvmStatic
        @JvmName("parse")
        fun CharSequence.parseToVersion(): Version {

            fun parseNormal(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                checkArgument(
                    list.size in 1..3,
                    "Semantic version need 1-3 dot separated parts: x[.y[.z]], now is {}.", list.size
                )
                val major = list[0].let {
                    val result = try {
                        it.toIntKt()
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
                            it.toIntKt()
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
                            it.toIntKt()
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

            val hyphenCount = HYPHEN_MATCHER.countIn(this)
            checkArgument(
                hyphenCount == 0 || hyphenCount == 1,
                "Illegal version specification, hyphen count should be 0 or 1."
            )
            val hyphenIndex = HYPHEN_MATCHER.indexIn(this)
            checkArgument(
                hyphenIndex < 0 || hyphenIndex in 1..this.length - 2,
                "Illegal hyphen position: {}.", hyphenIndex
            )

            val plusSignCount = PLUS_SIGN_MATCHER.countIn(this)
            checkArgument(
                plusSignCount == 0 || plusSignCount == 1,
                "Illegal version specification, plus sign count should be 0 or 1."
            )
            val plusSignIndex = PLUS_SIGN_MATCHER.indexIn(this)
            checkArgument(
                plusSignIndex < 0 || plusSignIndex in 1..this.length - 2,
                "Illegal plus sign position: {}.", plusSignIndex
            )

            val builder = Builder()
            when {
                hyphenIndex < 0 && plusSignIndex < 0 -> parseNormal(this, builder)
                hyphenIndex > 0 && plusSignIndex < 0 -> {
                    parseNormal(this.subSequence(0, hyphenIndex), builder)
                    parsePreVersion(this.subSequence(hyphenIndex + 1, this.length), builder)
                }
                hyphenIndex < 0 && plusSignIndex > 0 -> {
                    parseNormal(this.subSequence(0, plusSignIndex), builder)
                    parsePreVersion(this.subSequence(plusSignIndex + 1, this.length), builder)
                }
                hyphenIndex > 0 && plusSignIndex > 0 && (plusSignIndex - hyphenIndex > 1) -> {
                    parseNormal(this.subSequence(0, hyphenIndex), builder)
                    parsePreVersion(this.subSequence(hyphenIndex + 1, plusSignIndex), builder)
                    parsePreVersion(this.subSequence(plusSignIndex + 1, this.length), builder)
                }
                else -> throw IllegalArgumentException("Illegal version specification: $this")
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

interface Licence {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val url: String
        @JvmName("url") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val content: String
        @JvmName("content") get

    companion object {

        @JvmStatic
        fun of(name: String, url: String, content: String): Licence {
            return LicenceImpl(name, url, content)
        }

        private class LicenceImpl(
            override val name: String, override val url: String, override val content: String
        ) : Licence {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Licence) return false
                if (name != other.name) return false
                if (url != other.url) return false
                if (content != other.content) return false
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + url.hashCode()
                result = 31 * result + content.hashCode()
                return result
            }

            override fun toString(): String {
                return "$name[$url]"
            }
        }
    }
}

interface PoweredBy {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val url: String
        @JvmName("url") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val mail: String
        @JvmName("mail") get

    companion object {

        @JvmStatic
        fun of(name: String, url: String, mail: String): PoweredBy {
            return PoweredByImpl(name, url, mail)
        }

        private class PoweredByImpl(
            override val name: String, override val url: String, override val mail: String
        ) : PoweredBy {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is PoweredBy) return false
                if (name != other.name) return false
                if (url != other.url) return false
                if (mail != other.mail) return false
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + url.hashCode()
                result = 31 * result + mail.hashCode()
                return result
            }

            override fun toString(): String {
                return "$name[$mail, $url]"
            }
        }
    }
}