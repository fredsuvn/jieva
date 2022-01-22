package xyz.srclab.common.net.http

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.base.DEFAULT_IO_BUFFER_SIZE
import xyz.srclab.common.base.charset
import xyz.srclab.common.io.asInputStream
import java.net.HttpURLConnection
import java.net.URL

object JDK8HttpClient : HttpClient {

    override fun connect(req: HttpReq): HttpConnect {
        return HttpConnectImpl(req)
    }

    private class HttpConnectImpl(
        private val req: HttpReq
    ) : HttpConnect {

        private var conn: HttpURLConnection? = null

        override fun getResponse(readAll: Boolean): HttpResp {
            val conn = URL(req.url).openConnection() as HttpURLConnection
            this.conn = conn

            //set
            conn.connectTimeout = req.connectTimeoutMillis
            conn.readTimeout = req.readTimeoutMillis
            conn.requestMethod = req.method
            if (req.chunkedSize > 0) {
                conn.setChunkedStreamingMode(req.chunkedSize)
            } else if (req.contentLength >= 0) {
                conn.setFixedLengthStreamingMode(req.contentLength)
            }
            conn.useCaches = req.useCaches
            for (header in req.headers) {
                for (value in header.value) {
                    conn.setRequestProperty(header.key, value)
                }
            }

            //connect
            conn.doOutput = true
            conn.connect()
            val body = req.body

            //get
            if (body !== null) {
                body.copyTo(conn.outputStream, if (req.chunkedSize > 0) req.chunkedSize else DEFAULT_IO_BUFFER_SIZE)
            }
            val status = HttpStatus(conn.responseCode, conn.responseMessage)
            val charset = conn.contentType?.split(";")?.firstOrNull {
                it.trim().startsWith("charset=")
            }?.substring(8)
            return HttpResp().let {
                it.status = status
                it.headers = conn.headerFields
                it.body = if (readAll) conn.inputStream.readBytes().asInputStream() else conn.inputStream
                it.charset = charset?.charset() ?: DEFAULT_CHARSET
                it
            }
        }

        override fun close() {
            conn?.disconnect()
        }
    }
}