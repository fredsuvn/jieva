package xyz.srclab.common.net.http

/**
 * Http header.
 */
open class HttpHeader(
    open val key: String,
    open val value: String
) {

    open fun withNewValue(value: String): HttpHeader {
        return HttpHeader(key, value)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HttpHeader) return false
        if (key != other.key) return false
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun toString(): String {
        return "$key: $value"
    }

    companion object {

        @JvmField
        val ACCEPT_CHARSET = HttpHeader("Accept-Charset", "utf-8")

        @JvmField
        val CONTENT_TYPE_FORM = HttpHeader("Content-Type", "application/x-www-form-urlencoded")

        @JvmField
        val CONTENT_TYPE_FORM_UTF8 = CONTENT_TYPE_FORM.withNewValue(CONTENT_TYPE_FORM.value + ";charset=utf-8")

        @JvmField
        val CONTENT_TYPE_JSON = CONTENT_TYPE_FORM.withNewValue("application/json")

        @JvmField
        val CONTENT_TYPE_JSON_UTF8 = CONTENT_TYPE_JSON.withNewValue(CONTENT_TYPE_JSON.value + ";charset=utf-8")

        @JvmField
        val CONTENT_TYPE_HTML = CONTENT_TYPE_FORM.withNewValue("text/html")

        @JvmField
        val CONTENT_TYPE_HTML_UTF8 = CONTENT_TYPE_HTML.withNewValue(CONTENT_TYPE_HTML.value + ";charset=utf-8")

        @JvmField
        val CONTENT_TYPE_PLAIN = CONTENT_TYPE_FORM.withNewValue("text/plain")

        @JvmField
        val CONTENT_TYPE_PLAIN_UTF8 = CONTENT_TYPE_PLAIN.withNewValue(CONTENT_TYPE_JSON.value + ";charset=utf-8")

        @JvmField
        val CONTENT_TYPE_MULTI = CONTENT_TYPE_FORM.withNewValue("multipart/form-data")

        @JvmField
        val CONTENT_LENGTH = HttpHeader("Content-Length", "0")

        @JvmField
        val TRANSFER_ENCODING_CHUNKED = HttpHeader("Transfer-Encoding", "chunked")

        @JvmField
        val AUTHORIZATION_BASIC = HttpHeader("Authorization", "Basic base64encode")

        @JvmField
        val WWW_AUTHENTICATE = HttpHeader("WWW-Authenticate", "Basic realm=\"Secure Area\"")
    }
}