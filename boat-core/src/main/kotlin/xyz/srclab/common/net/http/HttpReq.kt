package xyz.srclab.common.net.http

import xyz.srclab.common.base.defaultBufferSize
import java.io.InputStream

/**
 * Request of http.
 */
open class HttpReq {

    open var version: String = HTTP_VERSION_1_1
    open var url: String = "localhost"
    open var method: String = HTTP_GET_METHOD
    open var headers: Map<String, List<String>> = emptyMap()
    open var body: InputStream? = null

    /**
     * If it is not negative, use this (will add context-length header). Default is -1.
     */
    open var contentLength: Long = -1L

    /**
     * If it is positive, use this (will add chunked header). Default is [DEFAULT_IO_BUFFER_SIZE].
     */
    open var chunkedSize: Int = defaultBufferSize()

    /**
     * If ture, use this (will add cache header). Default is false.
     */
    open var useCaches: Boolean = false

    open var connectTimeoutMillis: Int = DEFAULT_HTTP_CONNECT_TIMEOUT_MILLIS
    open var readTimeoutMillis: Int = DEFAULT_HTTP_READ_TIMEOUT_MILLIS
}