@file:JvmName("BHttp")

package xyz.srclab.common.net.http

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.addIfNotStartWith
import java.io.InputStream
import java.net.URLEncoder

const val HTTP_VERSION_1_1 = "HTTP/1.1"
const val HTTP_VERSION_2 = "HTTP/2"

const val HTTP_PROTOCOL = "http"
const val HTTPS_PROTOCOL = "https"

const val HTTP_GET_METHOD = "GET"
const val HTTP_POST_METHOD = "POST"
const val HTTP_HEAD_METHOD = "HEAD"
const val HTTP_PUT_METHOD = "PUT"
const val HTTP_DELETE_METHOD = "DELETE"
const val HTTP_TRACE_METHOD = "TRACE"
const val HTTP_CONNECT_METHOD = "CONNECT"
const val HTTP_OPTIONS_METHOD = "OPTIONS"

const val DEFAULT_HTTP_CONNECT_TIMEOUT_MILLIS = 5000
const val DEFAULT_HTTP_READ_TIMEOUT_MILLIS = 5000

private val defaultClient: HttpClient
    get() {
        return HttpClient.defaultClient()
    }

@JvmName("newUrl")
@JvmOverloads
fun newHttpUrl(
    protocol: CharSequence,
    host: CharSequence,
    path: CharSequence? = null,
    query: Map<*, *>? = null,
    fragment: CharSequence? = null
): String {
    return newHttpUrl(protocol, null, null, host, null, path, query, fragment)
}

@JvmName("newUrl")
@JvmOverloads
fun newHttpUrl(
    protocol: CharSequence,
    host: CharSequence,
    port: Int,
    path: CharSequence? = null,
    query: Map<*, *>? = null,
    fragment: CharSequence? = null
): String {
    return newHttpUrl(protocol, null, null, host, port, path, query, fragment)
}

@JvmName("newUrlWithAuth")
@JvmOverloads
fun newHttpUrlWithAuth(
    protocol: CharSequence,
    username: CharSequence?,
    password: CharSequence?,
    host: CharSequence,
    path: CharSequence? = null,
    query: Map<*, *>? = null,
    fragment: CharSequence? = null
): String {
    return newHttpUrl(protocol, username, password, host, null, path, query, fragment)
}

@JvmName("newUrlWithAuth")
@JvmOverloads
fun newHttpUrlWithAuth(
    protocol: CharSequence,
    username: CharSequence?,
    password: CharSequence?,
    host: CharSequence,
    port: Int,
    path: CharSequence? = null,
    query: Map<*, *>? = null,
    fragment: CharSequence? = null
): String {
    return newHttpUrl(protocol, username, password, host, port, path, query, fragment)
}

@JvmName("newUrl")
fun newHttpUrl(
    protocol: CharSequence,
    username: CharSequence?,
    password: CharSequence?,
    host: CharSequence,
    port: Int?,
    path: CharSequence?,
    query: Map<*, *>?,
    fragment: CharSequence?
): String {
    val builder = StringBuilder()
    builder.append(protocol).append("://")
    if (username !== null) {
        builder.append(username)
        if (password !== null) {
            builder.append(":").append(password)
        }
        builder.append("@")
    }
    builder.append(host)
    if (port !== null) {

        builder.append(":").append(port)
    }
    if (path !== null) {
        builder.append(path.addIfNotStartWith("/"))
    }
    if (!query.isNullOrEmpty()) {
        builder.append("?").append(query.httpQueryToString())
    }
    if (fragment !== null) {
        builder.append("#").append(fragment)
    }
    return builder.toString()
}

@JvmName("queryToString")
fun Map<*, *>.httpQueryToString(): String {
    return this.map {
        val key = it.key.toString()
        val value = it.value.toString()
        val encodedKey = URLEncoder.encode(key, DEFAULT_CHARSET.name()).replace("+", "%20")
        val encodedValue = URLEncoder.encode(value, DEFAULT_CHARSET.name()).replace("+", "%20")
        "$encodedKey=$encodedValue"
    }.joinToString("&")
}

fun request(req: HttpReq): HttpResp {
    return defaultClient.request(req)
}

@JvmOverloads
fun request(url: String, method: String = HTTP_GET_METHOD): HttpResp {
    return defaultClient.request(HttpReq().let {
        it.url = url
        it.method = method
        it
    })
}

fun request(url: String, method: String, body: InputStream): HttpResp {
    return defaultClient.request(HttpReq().let {
        it.url = url
        it.method = method
        it.body = body
        it
    })
}

fun request(url: String, method: String, body: String): HttpResp {
    return defaultClient.request(HttpReq().let {
        it.url = url
        it.method = method
        it.body = body.byteInputStream()
        it
    })
}