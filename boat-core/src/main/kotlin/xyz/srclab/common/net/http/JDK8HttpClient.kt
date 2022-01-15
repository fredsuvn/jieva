package xyz.srclab.common.net.http

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.toCharSet
import java.net.HttpURLConnection
import java.net.URL

object JDK8HttpClient : HttpClient {
    override fun request(req: HttpReq): HttpResp {
        val conn = URL(req.url).openConnection() as HttpURLConnection
        conn.connectTimeout = req.connectTimeoutMillis
        conn.readTimeout = req.readTimeoutMillis
        conn.requestMethod = req.method
        for (header in req.headers) {
            for (value in header.value) {
                conn.setRequestProperty(header.key, value)
            }
        }
        if (req.chunkedSize > 0) {
            conn.setChunkedStreamingMode(req.chunkedSize)
        } else if (req.contentLength >= 0) {
            conn.setFixedLengthStreamingMode(req.contentLength)
        }
        conn.doOutput = true
        conn.connect()
        val body = req.body
        if (body !== null) {
            body.copyTo(conn.outputStream, if (req.chunkedSize > 0) req.chunkedSize else DEFAULT_BUFFER_SIZE)
        }
        val status = HttpStatus(conn.responseCode, conn.responseMessage)
        val charset = conn.contentType?.split(";")?.firstOrNull {
            it.trim().startsWith("charset=")
        }?.substring(8)
        return HttpResp().let {
            it.status = status
            it.headers = conn.headerFields
            it.body = conn.inputStream
            it.charset = charset?.toCharSet() ?: DEFAULT_CHARSET
            it
        }
    }
}