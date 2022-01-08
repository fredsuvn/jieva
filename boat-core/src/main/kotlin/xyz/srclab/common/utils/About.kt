package xyz.srclab.common.utils

import xyz.srclab.common.base.*
import xyz.srclab.common.base.StringRef.Companion.stringRef

/**
 * Project about info.
 *
 * @see Author
 * @see SemVer
 */
interface About {

    val name: String
    val version: String?
    val authors: List<Author>
    val mail: String?
    val url: String?
    val licences: List<String>
    val poweredBy: List<About>
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
                val builder = StringBuilder(name)
                if (version !== null) {
                    builder.append(" V$version")
                }
                if (copyright !== null) {
                    builder.append(" $copyright")
                }
                return builder.toString()
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

    val name: String
    val mail: String?
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
                if (mail === null) {
                    return "$name[$url]"
                }
                if (url === null) {
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

    val major: Int

    val minor: Int

    val patch: Int

    val preRelease: List<String>

    val buildMetadata: List<String>

    val buildMetadataString: String
        get() = buildMetadata.joinDotToString()

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
        if (major != other.minor) {
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


    companion object {

        private val IDENTIFIER_CHAR_MATCHER = NUMERIC_MATCHER
            .or(UPPER_CASE_MATCHER)
            .or(LOWER_CASE_MATCHER)
            .or(HYPHEN_MATCHER)

        private const val ILLEGAL_SEM_VER_CHARS = "Illegal SemVer: "

        @JvmOverloads
        @JvmStatic
        fun of(
            major: Int,
            minor: Int,
            patch: Int,
            preRelease: List<String> = emptyList(),
            buildMetadata: List<String> = emptyList()
        ): SemVer {
            return SemVerImpl(major, minor, patch, preRelease, buildMetadata)
        }

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
                return !this.isLedByZeros()
            }

            fun CharSequence.checkAndParseNormal(): Int {
                if (!this.isNumericAndNoLedByZeros()) {
                    throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
                }
                return this.toInt()
            }

            fun CharSequence.checkPreRelease() {
                if (this.isNumeric() && this.isLedByZeros()) {
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

            var normalSeq: CharSequence? = null
            var preReleaseSeq: CharSequence? = null
            var buildMetadataSeq: CharSequence? = null

            val iPre = HYPHEN_MATCHER.indexIn(this)
            if (iPre in 0..4) {
                throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
            }
            val iPlus = PLUS_MATCHER.indexIn(this)
            if (iPlus in 0..4) {
                throw IllegalArgumentException(ILLEGAL_SEM_VER_CHARS + this)
            }
            if (iPre < 0 && iPlus < 0) {
                normalSeq = this
            } else if (iPre > 0 && iPlus < 0) {
                normalSeq = this.stringRef(0, iPre)
                preReleaseSeq = this.stringRef(iPre + 1)
            } else if (iPre < 0) {
                normalSeq = this.stringRef(0, iPlus)
                buildMetadataSeq = this.stringRef(iPlus + 1)
            } else if (iPre < iPlus) {
                normalSeq = this.stringRef(0, iPre)
                preReleaseSeq = this.stringRef(iPre + 1, iPlus)
                buildMetadataSeq = this.stringRef(iPlus + 1)
            } else {
                normalSeq = this.stringRef(0, iPlus)
                buildMetadataSeq = this.stringRef(iPlus + 1)
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

            return SemVerImpl(major, minor, patch, preRelease, buildMetadata)
        }

        private class SemVerImpl(
            override val major: Int,
            override val minor: Int,
            override val patch: Int,
            override val preRelease: List<String>,
            override val buildMetadata: List<String>
        ) : SemVer {

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
        }

        private fun List<Any>.joinDotToString(): String {
            return this.joinToString(".")
        }
    }
}