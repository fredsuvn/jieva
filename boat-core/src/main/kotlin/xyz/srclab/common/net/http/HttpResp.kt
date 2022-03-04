package xyz.srclab.common.net.http

import xyz.srclab.common.base.defaultCharset
import xyz.srclab.common.io.readString
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Response of http.
 */
open class HttpResp {

    open var version: String = HTTP_VERSION_1_1
    open var status: HttpStatus = HttpStatus.OK
    open var headers: Map<String, List<String>> = emptyMap()
    open var body: InputStream? = null
    open var charset: Charset = defaultCharset()

    open fun bodyAsString(): String? {
        return body?.readString(charset, true)
    }
}