package xyz.srclab.common.net.http

/**
 * Client of http.
 */
interface HttpClient {

    fun request(req: HttpReq): HttpResp

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