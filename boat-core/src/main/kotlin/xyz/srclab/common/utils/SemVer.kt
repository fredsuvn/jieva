package xyz.srclab.common.utils

import com.google.common.base.CharMatcher
import xyz.srclab.common.base.*
import java.io.Serializable

/**
 * Semantic version info, format as:
 *
 * ```
 * major.minor.patch[-preRelease][+buildMetadata]
 * ```
 *
 * See: [SemVer](https://semver.org/).
 *
 * @param major major version
 * @param minor minor version
 * @param patch patch version
 * @param preRelease pre-release version info
 * @param buildMetadata build info
 */
open class SemVer @JvmOverloads constructor(
    /**
     * Gets major version.
     */
    val major: Int,
    /**
     * Gets minor version.
     */
    val minor: Int,
    /**
     * Gets patch version.
     */
    val patch: Int,
    /**
     * Gets pre-release version info.
     */
    val preRelease: List<String> = emptyList(),
    /**
     * Gets build info.
     */
    val buildMetadata: List<String> = emptyList()
) : FinalClass(), Comparable<SemVer>, Serializable {

    /**
     * Returns this version as normal string:
     *
     * ```
     * major.minor.patch
     * ```
     */
    fun normalString(): String = "$major.$minor.$patch"

    /**
     * Returns pre-release version info as string.
     */
    fun preReleaseString(): String = preRelease.joinDotToString()

    /**
     * Returns build info as string.
     */
    fun buildMetadataString(): String = buildMetadata.joinDotToString()

    /**
     * Returns full version info as string:
     *
     * ```
     * major.minor.patch-preRelease+buildMetadata
     * ```
     */
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

    override fun hashCode0(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + preRelease.hashCode()
        return result
    }

    override fun toString0(): String {
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

        /**
         * Parse [SemVer] from [this] chars.
         */
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