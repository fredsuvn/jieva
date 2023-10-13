package xyz.fsgik.common.net.http;

import xyz.fsgik.common.base.FsChars;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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
     * @return response
     */
    public static FsHttpResponse request(FsHttpRequest request) {
        return FsHttpClient.defaultClient().request(request);
    }

    /**
     * Requests given url and headers with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @return response
     */
    public static FsHttpResponse get(String url, FsHttpHeaders headers) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("GET")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and query string with GET method.
     *
     * @param url         given url
     * @param queryString given query string
     * @return response
     */
    public static FsHttpResponse get(String url, Map<String, String> queryString) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url, queryString)
                .method("GET")
                .build()
        );
    }

    /**
     * Requests given url, headers and query string with GET method.
     *
     * @param url         given url
     * @param headers     headers
     * @param queryString given query string
     * @return response
     */
    public static FsHttpResponse get(String url, FsHttpHeaders headers, Map<String, String> queryString) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url, queryString)
                .method("GET")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and headers with POST method.
     *
     * @param url     given url
     * @param headers headers
     * @return response
     */
    public static FsHttpResponse post(String url, FsHttpHeaders headers) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .build()
        );
    }

    /**
     * Requests given url and body with GET method.
     *
     * @param url  given url
     * @param body given body
     * @return response
     */
    public static FsHttpResponse post(String url, InputStream body) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url and body with GET method.
     *
     * @param url  given url
     * @param body given body
     * @return response
     */
    public static FsHttpResponse post(String url, ByteBuffer body) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url, headers and body with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @param body    given body
     * @return response
     */
    public static FsHttpResponse post(String url, FsHttpHeaders headers, InputStream body) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .body(body)
                .build()
        );
    }

    /**
     * Requests given url, headers and body with GET method.
     *
     * @param url     given url
     * @param headers headers
     * @param body    given body
     * @return response
     */
    public static FsHttpResponse post(String url, FsHttpHeaders headers, ByteBuffer body) {
        return FsHttpClient.defaultClient().request(
            FsHttpRequest.newBuilder()
                .url(url)
                .method("POST")
                .headers(headers)
                .body(body)
                .build()
        );
    }

    /**
     * Encodes given form string (for application/x-www-form-urlencoded) with {@link URLEncoder#encode(String, String)}.
     * Using {@link FsChars#defaultCharset()}.
     *
     * @param formString given form string
     * @return encoded string
     */
    public static String encodeForm(String formString) {
        return encodeForm(formString, FsChars.defaultCharset());
    }

    /**
     * Encodes given form string (for application/x-www-form-urlencoded) with {@link URLEncoder#encode(String, String)}.
     *
     * @param formString given form string
     * @param charset    charset
     * @return encoded string
     */
    public static String encodeForm(String formString, Charset charset) {
        try {
            return URLEncoder.encode(formString, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new FsHttpException(e);
        }
    }

    /**
     * Encodes given query string (for url after '?')
     * with {@link URLEncoder#encode(String, String)} and replace '+' to '%20'.
     * Using {@link FsChars#defaultCharset()}.
     *
     * @param queryString given query string
     * @return encoded string
     */
    public static String encodeQuery(String queryString) {
        return encodeQuery(queryString, FsChars.defaultCharset());
    }

    /**
     * Encodes given query string (for url after '?')
     * with {@link URLEncoder#encode(String, String)} and replace '+' to '%20'.
     *
     * @param queryString given query string
     * @param charset     charset
     * @return encoded string
     */
    public static String encodeQuery(String queryString, Charset charset) {
        return encodeForm(queryString, charset).replaceAll("\\+", "%20");
    }
}
