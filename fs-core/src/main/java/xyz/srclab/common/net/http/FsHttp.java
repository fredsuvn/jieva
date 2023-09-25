package xyz.srclab.common.net.http;

import java.util.Map;

/**
 * Http utilities, based on {@link FsHttpClient#defaultClient()}.
 *
 * @author fredsuvn
 */
public class FsHttp {

    /**
     * Requests with given request info.
     *
     * @param request request info
     */
    public static FsHttpResponse request(FsHttpRequest request) {
        return FsHttpClient.defaultClient().request(request);
    }

    /**
     * Requests given url and headers with GET method.
     *
     * @param url     given url
     * @param headers given headers
     */
    public static FsHttpResponse get(String url, Map<String, ?> headers) {
        return FsHttpClient.defaultClient().request(new FsHttpRequest(url, "GET", headers, null));
    }
}
