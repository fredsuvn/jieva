package xyz.srclab.common.net.http

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.io.readString
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Response of http.
 */
open class HttpResp {

    var version: String = HTTP_VERSION_1_1
    var status: HttpStatus = HttpStatus.OK
    var headers: Map<String, List<String>> = emptyMap()
    var body: InputStream? = null
    var charset: Charset = DEFAULT_CHARSET

    fun bodyAsString(): String? {
        return body?.readString(charset, true)
    }
}