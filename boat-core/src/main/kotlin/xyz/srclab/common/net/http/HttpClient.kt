package xyz.srclab.common.net.http

/**
 * Client of http.
 */
interface HttpClient {

    fun connect(req: HttpReq): HttpConnect

    /**
     * Connects and read all data from response, then release the connection resource.
     */
    fun request(req: HttpReq): HttpResp {
        val connect = connect(req)
        val resp = connect.getResponse(true)
        connect.close()
        return resp
    }

    companion object {

        private var defaultClient: HttpClient = JDK8HttpClient

        @JvmStatic
        fun defaultClient(): HttpClient {
            return defaultClient
        }

        @JvmStatic
        fun setDefaultClient(defaultClient: HttpClient) {
            this.defaultClient = defaultClient
        }
    }
}