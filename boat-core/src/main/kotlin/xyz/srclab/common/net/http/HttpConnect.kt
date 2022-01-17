package xyz.srclab.common.net.http

import java.io.InputStream

/**
 * Connect for http
 */
interface HttpConnect {

    /**
     * Same as `getResponse(false)`.
     */
    fun getResponse(): HttpResp {
        return getResponse(false)
    }

    /**
     * If [readAll] set to `true`, it will read all bytes from response and returns;
     * else the [InputStream] of returned response will keep the connection.
     */
    fun getResponse(readAll: Boolean): HttpResp

    fun close()
}