package xyz.srclab.common.utils

import com.google.common.base.CharMatcher
import xyz.srclab.common.base.*
import xyz.srclab.common.base.CharsRef.Companion.charsRef
import java.io.Serializable

/**
 * Project about info.
 *
 * @see Author
 * @see SemVer
 */
open class About @JvmOverloads constructor(
    val name: String,
    val version: String? = null,
    val authors: List<Author> = emptyList(),
    val mail: String? = null,
    val url: String? = null,
    val licences: List<String> = emptyList(),
    val poweredBy: List<About> = emptyList(),
    val copyright: String? = null,
) : Serializable {

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
        val builder = StringBuilder(name)
        if (version !== null) {
            builder.append(" V$version")
        }
        if (copyright !== null) {
            builder.append(" $copyright")
        }
        return builder.toString()
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Author info.
 *
 * @see About
 */
open class Author @JvmOverloads constructor(
    val name: String,
    val mail: String? = null,
    val url: String? = null
) : Serializable {

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
        if (mail === null) {
            return "$name[$url]"
        }
        if (url === null) {
            return "$name[$mail]"
        }
        return "$name[$mail, $url]"
    }

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Semantic version info, See: [SemVer](https://semver.org/).
 * The difference is this interface supports more than 3 version numbers.
 *
 * @see About
 */
open class SemVer @JvmOverloads constructor(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: List<String> = emptyList(),
    val buildMetadata: List<String> = emptyList()
) : Comparable<SemVer>, Serializable {

    fun normalString(): String = "$major.$minor.$patch"

    fun preReleaseString(): String = preRelease.joinDotToString()

    fun buildMetadataString(): String = buildMetadata.joinDotToString()

    fun fullString(): String {
        if (preRelease.isEmpty() && buildMetadata.isEmpty()) {
            return normalString()
        }
        if (preRelease.isEmpty() && buildMetadata.isNotEmpty()) {
            return normalString() + "+" + buildMetadataString()
        }
        if (preRelease.isNotEmpty() && buildMetadata.isEmpty()) {
            return normalString() + "-" + preReleaseString()
        }
        return normalString() + "-" + preReleaseString() + "+" + buildMetadataString()
    }

    override fun compareTo(other: SemVer): Int {
        if (major != other.major) {
            return major - other.major
        }
        if (minor != other.minor) {
            return minor - other.minor
        }
        if (patch != other.patch) {
            return patch - other.patch
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
        val it = preRelease.iterator()
        val ito = other.preRelease.iterator()
        while (it.hasNext()) {
            if (!ito.hasNext()) {
                return 1
            }
            val n = it.next()
            val no = ito.next()
            if (n == no) {
                continue
            }
            val isNumberN = n.isNumeric()
            val isNumberNo = no.isNumeric()
            if (isNumberN && isNumberNo) {
                if (n.length != no.length) {
                    return n.length - no.length
                }
                return n.compareTo(no)
            }
            if (isNumberN) {
                return -1
            }
            if (isNumberNo) {
                return 1
            }
            return n.compareTo(no)
        }
        if (ito.hasNext()) {
            return -1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SemVer) return false
        return this.compareTo(other) == 0
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + preRelease.hashCode()
        return result
    }

    override fun toString(): String {
        return fullString()
    }

    private fun List<Any>.joinDotToString(): String {
        return this.joinToString(".")
    }

    companion object {

        private val serialVersionUID: Long = defaultSerialVersion()

        private val hyphenMatcher: CharMatcher = hyphenMatcher()
        private val plusMatcher: CharMatcher = CharMatcher.`is`('+')

        private val IDENTIFIER_CHAR_MATCHER = CharMatcher.inRange('0', '9')
            .or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.inRange('A', 'Z'))
            .or(hyphenMatcher)

        private const val ILLEGAL_SEM_VER_CHARS = "Illegal SemVer: "

        @JvmName("parse")
        @JvmStatic
        fun CharSequence.parseSemVer(): SemVer {

            fun CharSequence.isNumericAndNoLedByZeros(): Boolean {
                if (this.isEmpty()) {
                    return false
                }
                if (!this.isNumeric()) {
                    return false
                }
                return !this.isLeadingZero()
            }

            fun CharSequence.checkAndParseNormal(): Int {
                if (!this.isNumericAndNoLedByZeros()) {
                    throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
                }
                return this.toInt()
            }

            fun CharSequence.checkPreRelease() {
                if (this.isNumeric() && this.isLeadingZero()) {
                    throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
                }
                if (!IDENTIFIER_CHAR_MATCHER.matchesAllOf(this)) {
                    throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
                }
            }

            fun CharSequence.checkBuildMetadata() {
                if (!IDENTIFIER_CHAR_MATCHER.matchesAllOf(this)) {
                    throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
                }
            }

            val normalSeq: CharSequence?
            var preReleaseSeq: CharSequence? = null
            var buildMetadataSeq: CharSequence? = null

            val iPre = hyphenMatcher.indexIn(this)
            if (iPre in 0..4) {
                throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
            }
            val iPlus = plusMatcher.indexIn(this)
            if (iPlus in 0..4) {
                throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
            }
            if (iPre < 0 && iPlus < 0) {
                normalSeq = this
            } else if (iPre > 0 && iPlus < 0) {
                normalSeq = this.charsRef(0, iPre)
                preReleaseSeq = this.charsRef(iPre + 1)
            } else if (iPre < 0) {
                normalSeq = this.charsRef(0, iPlus)
                buildMetadataSeq = this.charsRef(iPlus + 1)
            } else if (iPre < iPlus) {
                normalSeq = this.charsRef(0, iPre)
                preReleaseSeq = this.charsRef(iPre + 1, iPlus)
                buildMetadataSeq = this.charsRef(iPlus + 1)
            } else {
                normalSeq = this.charsRef(0, iPlus)
                buildMetadataSeq = this.charsRef(iPlus + 1)
            }

            val normals = normalSeq.split('.')
            if (normals.size != 3) {
                throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
            }
            val major = normals[0].checkAndParseNormal()
            val minor = normals[1].checkAndParseNormal()
            val patch = normals[2].checkAndParseNormal()

            var preRelease: List<String> = emptyList()
            var buildMetadata: List<String> = emptyList()

            if (preReleaseSeq !== null) {
                preRelease = preReleaseSeq.split('.')
                for (s in preRelease) {
                    s.checkPreRelease()
                }
            }
            if (buildMetadataSeq !== null) {
                buildMetadata = buildMetadataSeq.split('.')
                for (s in preRelease) {
                    s.checkBuildMetadata()
                }
            }

            return SemVer(major, minor, patch, preRelease, buildMetadata)
        }
    }
}