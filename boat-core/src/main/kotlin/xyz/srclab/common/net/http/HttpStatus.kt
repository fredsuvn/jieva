package xyz.srclab.common.net.http

import java.net.HttpURLConnection

/**
 * Http response status.
 */
open class HttpStatus(
    open val code: Int,
    open val message: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HttpStatus) return false
        if (code != other.code) return false
        if (message != other.message) return false
        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }

    override fun toString(): String {
        return "$code $message"
    }

    companion object {

        @JvmField
        val OK = HttpStatus(HttpURLConnection.HTTP_OK, "OK")
    }
}