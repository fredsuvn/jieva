package xyz.srclab.common.utils

import xyz.srclab.common.lang.Defaults
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.checkArgument
import xyz.srclab.common.lang.isNumeric
import kotlin.text.toInt as toIntKt

/**
 * Project about info.
 *
 * @see Author
 * @see SemVer
 */
interface About {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String

    @get:JvmName("version")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val version: String?

    @get:JvmName("author")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val authors: List<Author>

    @get:JvmName("mail")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val mail: String?

    @get:JvmName("url")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val url: String?

    @get:JvmName("licence")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val licences: List<String>

    @get:JvmName("poweredBy")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val poweredBy: List<About>

    @get:JvmName("copyright")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val copyright: String?

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(
            name: String,
            version: String? = null,
            author: List<Author> = emptyList(),
            mail: String? = null,
            url: String? = null,
            licence: List<String> = emptyList(),
            poweredBy: List<About> = emptyList(),
            copyright: String? = null,
        ): About {
            return AboutImpl(name, version, author, mail, url, licence, poweredBy, copyright)
        }

        private class AboutImpl(
            override val name: String,
            override val version: String?,
            override val authors: List<Author>,
            override val mail: String?,
            override val url: String?,
            override val licences: List<String>,
            override val poweredBy: List<About>,
            override val copyright: String?,
        ) : About {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is About) return false
                if (name != other.name) return false
                if (version != other.version) return false
                if (authors != other.authors) return false
                if (mail != other.mail) return false
                if (url != other.url) return false
                if (licences != other.licences) return false
                if (poweredBy != other.poweredBy) return false
                if (copyright != other.copyright) return false
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + version.hashCode()
                result = 31 * result + authors.hashCode()
                result = 31 * result + mail.hashCode()
                result = 31 * result + url.hashCode()
                result = 31 * result + licences.hashCode()
                result = 31 * result + poweredBy.hashCode()
                result = 31 * result + copyright.hashCode()
                return result
            }

            override fun toString(): String {
                val builder = StringBuilder()
                var count = 0
                if (!version.isNullOrEmpty()) {
                    builder.append("Version: $version")
                    count++
                }

                fun String.appendInfo() {
                    if (count > 0) {
                        builder.append(", ")
                    }
                    builder.append(this)
                }

                if (authors.isNotEmpty()) {
                    "Authors: $authors".appendInfo()
                }
                if (!mail.isNullOrEmpty()) {
                    "Mail: $mail".appendInfo()
                }
                if (!url.isNullOrEmpty()) {
                    "Url: $url".appendInfo()
                }
                if (licences.isNotEmpty()) {
                    "Licences: $licences".toString().appendInfo()
                }
                if (poweredBy.isNotEmpty()) {
                    "Powered By: $poweredBy".toString().appendInfo()
                }
                if (!copyright.isNullOrEmpty()) {
                    "Copyright: $copyright".appendInfo()
                }
                return if (count == 0) name else "$name[$builder]"
            }
        }
    }
}

/**
 * Author info.
 *
 * @see About
 */
interface Author {

    @get:JvmName("name")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String

    @get:JvmName("mail")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val mail: String?

    @get:JvmName("url")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val url: String?

    companion object {

        @JvmOverloads
        @JvmStatic
        fun of(name: String, mail: String? = null, url: String? = null): Author {
            return AuthorImpl(name, mail, url)
        }

        private class AuthorImpl(
            override val name: String,
            override val mail: String?,
            override val url: String?,
        ) : Author {

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is Author) return false
                if (name != other.name) return false
                if (mail != other.mail) return false
                if (url != other.url) return false
                return true
            }

            override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + (mail?.hashCode() ?: 0)
                result = 31 * result + (url?.hashCode() ?: 0)
                return result
            }

            override fun toString(): String {
                if (mail === null && url === null) {
                    return name
                }
                if (mail === null && url !== null) {
                    return "$name[$url]"
                }
                if (mail !== null && url === null) {
                    return "$name[$mail]"
                }
                return "$name[$mail, $url]"
            }
        }
    }
}

/**
 * Semantic version info, See: [SemVer](https://semver.org/).
 * The difference is this interface supports more than 3 version numbers.
 *
 * @see About
 */
interface SemVer : Comparable<SemVer> {

    @get:JvmName("normalNumbers")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val normalNumbers: List<Int>

    @get:JvmName("major")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val major: Int
        get() = normalNumbers[0]

    @get:JvmName("minor")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val minor: Int
        get() = if (normalNumbers.size > 1) normalNumbers[1] else 0

    @get:JvmName("patch")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val patch: Int
        get() = if (normalNumbers.size > 2) normalNumbers[2] else 0

    @get:JvmName("normalString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val normalString: String
        get() = normalNumbers.joinDotToString()

    @get:JvmName("preRelease")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val preRelease: List<PreReleaseIdentifier>

    @get:JvmName("preReleaseString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val preReleaseString: String
        get() = preRelease.joinDotToString()

    @get:JvmName("buildMetadata")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val buildMetadata: List<String>

    @get:JvmName("buildMetadataString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val buildMetadataString: String
        get() = buildMetadata.joinDotToString()

    @get:JvmName("isNormal")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isNormal: Boolean
        get() = preRelease.isEmpty()

    @get:JvmName("isPreRelease")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val isPreRelease: Boolean
        get() = preRelease.isNotEmpty()

    interface PreReleaseIdentifier {

        val isNumeric: Boolean

        fun toNumber(): Int

        override fun toString(): String

        companion object {

            internal fun of(value: Any): PreReleaseIdentifier {
                if (value is Number) {
                    return of(value.toInt())
                }
                return of(value.toString())
            }

            internal fun of(value: Int): PreReleaseIdentifier {
                return NumericIdentifier(value)
            }

            internal fun of(value: CharSequence): PreReleaseIdentifier {
                if (value.isNumeric()) {
                    return of(value.toString().toIntKt())
                }
                if (value.matches(IDENTIFIER_PATTERN)) {
                    return StringIdentifier(value)
                }
                throw IllegalArgumentException(
                    "SemVer pre-release identifier should be in ${IDENTIFIER_PATTERN.pattern}: $value"
                )
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

    private class SemVerImpl(
        override val normalNumbers: List<Int>,
        override val preRelease: List<PreReleaseIdentifier>,
        override val buildMetadata: List<String>,
    ) : SemVer {

        override fun compareTo(other: SemVer): Int {
            for (i in normalNumbers.indices) {
                if (normalNumbers[i] > other.normalNumbers[i]) {
                    return 1
                }
                if (normalNumbers[i] < other.normalNumbers[i]) {
                    return -1
                }
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
                //Numeric identifiers always have lower precedence than non-numeric identifiers.
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
            if (other !is SemVer) return false
            if (normalNumbers != other.normalNumbers) return false
            if (preRelease != other.preRelease) return false
            if (buildMetadata != other.buildMetadata) return false
            return true
        }

        override fun hashCode(): Int {
            var result = normalNumbers.hashCode()
            result = 31 * result + preRelease.hashCode()
            result = 31 * result + buildMetadata.hashCode()
            return result
        }

        override fun toString(): String {
            val buffer = StringBuilder(normalString)
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

        private val IDENTIFIER_PATTERN = "[0-9A-Za-z-]+".toRegex()

        @JvmOverloads
        @JvmStatic
        fun of(
            normalNumbers: List<Int>,
            preRelease: List<Any> = emptyList(),
            buildMetadata: List<String> = emptyList(),
        ): SemVer {
            return newSemVer(normalNumbers, preRelease.toPreRelease(), buildMetadata)
        }

        @JvmOverloads
        @JvmStatic
        fun of(
            major: Int,
            minor: Int,
            patch: Int,
            preRelease: List<Any> = emptyList(),
            buildMetadata: List<String> = emptyList()
        ): SemVer {
            return newSemVer(listOf(major, minor, patch), preRelease.toPreRelease(), buildMetadata)
        }

        @JvmStatic
        fun newSemVer(
            normalNumbers: List<Int>,
            preRelease: List<PreReleaseIdentifier>,
            buildMetadata: List<String>,
        ): SemVer {
            return SemVerImpl(normalNumbers, preRelease, buildMetadata.toBuildMetadata())
        }

        @JvmName("parse")
        @JvmStatic
        fun CharSequence.parseSemVer(): SemVer {

            fun parseNormalNumbers(subSpec: CharSequence): List<Int> {
                val list = subSpec.split(".")
                checkArgument(
                    list.isNotEmpty(),
                    "SemVer need at least dot separated parts: x[.y[.z[...]]]."
                )
                return list.map {
                    try {
                        it.toIntKt()
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Wrong normal number: $it", e)
                    }
                }
            }

            fun parsePreVersion(subSpec: CharSequence): List<PreReleaseIdentifier> {
                val list = subSpec.split(".")
                return list.toPreRelease()
            }

            fun parseBuildMetadata(subSpec: CharSequence): List<String> {
                return subSpec.split(".").toBuildMetadata()
            }

            val hyphenIndex = Defaults.HYPHEN_MATCHER.indexIn(this)
            val plusSignIndex = Defaults.PLUS_SIGN_MATCHER.indexIn(this)
            if (hyphenIndex < 0 && plusSignIndex < 0) {
                return newSemVer(parseNormalNumbers(this), emptyList(), emptyList())
            }
            if (hyphenIndex > 0 && plusSignIndex < 0) {
                return newSemVer(
                    parseNormalNumbers(this.subSequence(0, hyphenIndex)),
                    parsePreVersion(this.subSequence(hyphenIndex + 1, this.length)),
                    emptyList()
                )
            }
            if (hyphenIndex < 0 && plusSignIndex > 0) {
                return newSemVer(
                    parseNormalNumbers(this.subSequence(0, plusSignIndex)),
                    emptyList(),
                    parseBuildMetadata(this.subSequence(plusSignIndex + 1, this.length))
                )
            }
            if (plusSignIndex < hyphenIndex) {
                return newSemVer(
                    parseNormalNumbers(this.subSequence(0, plusSignIndex)),
                    emptyList(),
                    parseBuildMetadata(this.subSequence(plusSignIndex + 1, this.length))
                )
            }
            return newSemVer(
                parseNormalNumbers(this.subSequence(0, hyphenIndex)),
                parsePreVersion(this.subSequence(hyphenIndex + 1, plusSignIndex)),
                parseBuildMetadata(this.subSequence(plusSignIndex + 1, this.length))
            )
        }

        private fun List<Any>.joinDotToString(): String {
            return this.joinToString(".")
        }

        private fun List<Any>.toPreRelease(): List<PreReleaseIdentifier> {
            return this.map {
                PreReleaseIdentifier.of(it)
            }
        }

        private fun List<Any>.toBuildMetadata(): List<String> {
            return this.map {
                val string = it.toString()
                if (!string.matches(IDENTIFIER_PATTERN)) {
                    throw IllegalArgumentException(
                        "SemVer build metadata identifier should be in ${IDENTIFIER_PATTERN.pattern}: $string"
                    )
                }
                string
            }
        }
    }
}