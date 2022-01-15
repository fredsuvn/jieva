package xyz.srclab.common.net.http

import java.io.InputStream

/**
 * Request of http.
 */
open class HttpReq {
    var version: String = HTTP_VERSION_1_1
    var url: String = "localhost"
    var method: String = HTTP_GET_METHOD
    var headers: Map<String, List<String>> = emptyMap()
    var body: InputStream? = null
    var contentLength: Long = -1L
    var chunkedSize: Int = DEFAULT_BUFFER_SIZE
    var connectTimeoutMillis: Int = DEFAULT_HTTP_CONNECT_TIMEOUT_MILLIS
    var readTimeoutMillis: Int = DEFAULT_HTTP_READ_TIMEOUT_MILLIS
}