package xyz.srclab.common.base

import java.time.ZonedDateTime
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
    val releaseDate: ZonedDateTime
        @JvmName("releaseDate") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val help: String
        @JvmName("help") get() = "$url/help"

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val report: String
        @JvmName("report") get() = "$url/report"

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            url: String,
            version: Version,
            licence: Licence,
            poweredBy: PoweredBy,
            releaseDate: ZonedDateTime = ZonedDateTime.now(),
        ): About {
            return AboutImpl(name, url, version, licence, poweredBy, releaseDate)
        }

        @JvmStatic
        fun of(
            name: String,
            url: String,
            version: Version,
            licence: Licence,
            poweredBy: PoweredBy,
            releaseDate: ZonedDateTime,
            help: String,
            report: String,
        ): About {
            return AboutImpl(name, url, version, licence, poweredBy, releaseDate, help, report)
        }

        private class AboutImpl(
            override val name: String,
            override val url: String,
            override val version: Version,
            override val licence: Licence,
            override val poweredBy: PoweredBy,
            override val releaseDate: ZonedDateTime,
            help: String? = null,
            report: String? = null
        ) : About {

            override val help: String = help ?: super.help
            override val report: String = report ?: super.report

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is About) return false

                if (name != other.name) return false
                if (url != other.url) return false
                if (version != other.version) return false
                if (licence != other.licence) return false
                if (poweredBy != other.poweredBy) return false
                if (releaseDate != other.releaseDate) return false
                if (help != other.help) return false
                if (report != other.report) return false

                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + url.hashCode()
                result = 31 * result + version.hashCode()
                result = 31 * result + licence.hashCode()
                result = 31 * result + poweredBy.hashCode()
                result = 31 * result + releaseDate.hashCode()
                result = 31 * result + help.hashCode()
                result = 31 * result + report.hashCode()
                return result
            }

            override fun toString(): String {
                return """
                    $name $version, release on $releaseDate
                    $url
                    Under the ${licence.name} licence
                    Powered by ${poweredBy.name}
                """.trimIndent()
            }
        }
    }
}

interface Version : Comparable<Version> {

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
    val preRelease: List<PreReleaseIdentifier>
        @JvmName("preRelease") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val preReleaseString: String
        @JvmName("preReleaseString") get() = preRelease.joinToString(".")

    @Suppress(INAPPLICABLE_JVM_NAME)
    val buildMetadata: List<String>
        @JvmName("buildMetadata") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val buildMetadataString: String
        @JvmName("buildMetadataString") get() = buildMetadata.joinToString(".")

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isNormal: Boolean
        @JvmName("isNormal") get() = preRelease.isEmpty()

    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val isPreRelease: Boolean
        @JvmName("isPreRelease") get() = preRelease.isNotEmpty()

    interface PreReleaseIdentifier {

        val isNumeric: Boolean

        fun toNumber(): Int

        override fun toString(): String

        companion object {

            internal fun of(value: Int): PreReleaseIdentifier {
                return NumericIdentifier(value)
            }

            internal fun of(value: CharSequence): PreReleaseIdentifier {
                if (value.isNumeric()) {
                    return of(value.toString().toIntKt())
                }
                return StringIdentifier(value)
            }

            private class NumericIdentifier(private val value: Int) : PreReleaseIdentifier {
                override val isNumeric: Boolean = true
                override fun toNumber(): Int = value
                override fun toString(): String = value.toString()

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is PreReleaseIdentifier) return false

                    if (!other.isNumeric) return false
                    if (toNumber() != other.toNumber()) return false

                    return true
                }

                override fun hashCode(): Int {
                    return value
                }
            }

            private class StringIdentifier(private val value: CharSequence) : PreReleaseIdentifier {
                override val isNumeric: Boolean = false
                override fun toNumber(): Int = throw IllegalStateException("Not a numeric pre-release identifier.")
                override fun toString(): String = value.toString()

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is PreReleaseIdentifier) return false

                    if (other.isNumeric) return false
                    if (toString() != other.toString()) return false

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
        private var preRelease: MutableList<PreReleaseIdentifier>? = null
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

        fun preRelease(values: List<Any>) = apply {
            for (value in values) {
                if (value is Number) {
                    addPreRelease(value.toInt())
                } else {
                    addPreRelease(value.toString())
                }
            }
        }

        fun addPreRelease(value: Int) = apply {
            val identifier = PreReleaseIdentifier.of(value)
            val list = preRelease ?: mutableListOf()
            list.add(identifier)
            preRelease = list
        }

        fun addPreRelease(value: CharSequence) = apply {
            checkArgument(
                value.matches(identifierPattern),
                "Pre-release identifier should be ${identifierPattern.pattern}"
            )
            val identifier = PreReleaseIdentifier.of(value)
            val list = preRelease ?: mutableListOf()
            list.add(identifier)
            preRelease = list
        }

        fun buildMetadata(values: List<CharSequence>) = apply {
            for (value in values) {
                addBuildMetadata(value)
            }
        }

        fun addBuildMetadata(value: CharSequence) = apply {
            checkArgument(
                value.matches(identifierPattern),
                "Build metadata identifier should be ${identifierPattern.pattern}"
            )
            val list = buildMetadata ?: mutableListOf()
            list.add(value.toString())
            buildMetadata = list
        }

        fun build(): Version {
            return VersionImpl(
                major,
                minor,
                patch,
                preRelease ?: emptyList(),
                buildMetadata ?: emptyList(),
            )
        }

        private class VersionImpl(
            override val major: Int,
            override val minor: Int,
            override val patch: Int,
            override val preRelease: List<PreReleaseIdentifier>,
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
                    }
                    val stringCompare = preV.toString().compareTo(otherPreV.toString())
                    if (stringCompare != 0) {
                        return stringCompare
                    }
                }
                if (otherPreIt.hasNext()) {
                    return -1
                }
                return 0
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as VersionImpl

                if (major != other.major) return false
                if (minor != other.minor) return false
                if (patch != other.patch) return false
                if (preRelease != other.preRelease) return false
                if (buildMetadata != other.buildMetadata) return false

                return true
            }

            override fun hashCode(): Int {
                var result = major
                result = 31 * result + minor
                result = 31 * result + patch
                result = 31 * result + preRelease.hashCode()
                result = 31 * result + buildMetadata.hashCode()
                return result
            }

            override fun toString(): String {
                val buffer = StringBuilder("$major.$minor.$patch")
                if (preRelease.isNotEmpty()) {
                    buffer.append("-$preReleaseString")
                }
                if (buildMetadata.isNotEmpty()) {
                    buffer.append("+$buildMetadataString")
                }
                return buffer.toString()
            }
        }

        companion object {
            private val identifierPattern = "[0-9A-Za-z-]+".toRegex()
        }
    }

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(
            major: Int,
            minor: Int,
            patch: Int,
            preRelease: List<Any> = emptyList(),
            buildMetadata: List<String> = emptyList()
        ): Version {
            return Builder()
                .major(major)
                .minor(minor)
                .patch(patch)
                .preRelease(preRelease)
                .buildMetadata(buildMetadata)
                .build()
        }

        @JvmStatic
        @JvmName("parse")
        fun CharSequence.parseToVersion(): Version {

            fun parseNormal(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                checkArgument(
                    list.size in 1..3,
                    "Semantic version need 1-3 dot separated parts: x[.y[.z]]."
                )
                val major = list[0].let {
                    try {
                        it.toIntKt()
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Wrong major: $it", e)
                    }
                }
                val minor = if (list.size < 2) 0 else {
                    list[1].let {
                        try {
                            it.toIntKt()
                        } catch (e: Exception) {
                            throw IllegalArgumentException("Wrong minor: $it", e)
                        }
                    }
                }
                val patch = if (list.size < 3) 0 else {
                    list[2].let {
                        try {
                            it.toIntKt()
                        } catch (e: Exception) {
                            throw IllegalArgumentException("Wrong patch: $it", e)
                        }
                    }
                }
                builder.major(major)
                builder.minor(minor)
                builder.patch(patch)
            }

            fun parsePreVersion(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                builder.preRelease(list)
            }

            fun parseBuildMetadata(subSpec: CharSequence, builder: Builder) {
                val list = subSpec.split(".")
                builder.buildMetadata(list)
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
                    parseBuildMetadata(this.subSequence(plusSignIndex + 1, this.length), builder)
                }
                hyphenIndex > 0 && plusSignIndex > 0 && (plusSignIndex - hyphenIndex > 1) -> {
                    parseNormal(this.subSequence(0, hyphenIndex), builder)
                    parsePreVersion(this.subSequence(hyphenIndex + 1, plusSignIndex), builder)
                    parseBuildMetadata(this.subSequence(plusSignIndex + 1, this.length), builder)
                }
                else -> throw IllegalArgumentException("Illegal version specification: $this")
            }
            return builder.build()
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
    @JvmDefault
    val content: String
        @JvmName("content") get() = url

    companion object {

        @JvmStatic
        @JvmOverloads
        fun of(name: String, url: String, content: String = url): Licence {
            return LicenceImpl(name, url, content)
        }

        private class LicenceImpl(
            override val name: String,
            override val url: String,
            content: String? = null,
        ) : Licence {

            override val content: String = content ?: super.content

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
            override val name: String,
            override val url: String,
            override val mail: String,
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
                return """
                    $name
                    $mail
                    $url
                """.trimIndent()
            }
        }
    }
}