package xyz.srclab.common.net.http

import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
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

    /**
     * If it is not negative, use this (will add context-length header). Default is -1.
     */
    var contentLength: Long = -1L

    /**
     * If it is positive, use this (will add chunked header). Default is [DEFAULT_IO_BUFFER_SIZE].
     */
    var chunkedSize: Int = DEFAULT_IO_BUFFER_SIZE

    /**
     * If ture, use this (will add cache header). Default is false.
     */
    var useCaches: Boolean = false

    var connectTimeoutMillis: Int = DEFAULT_HTTP_CONNECT_TIMEOUT_MILLIS
    var readTimeoutMillis: Int = DEFAULT_HTTP_READ_TIMEOUT_MILLIS
}