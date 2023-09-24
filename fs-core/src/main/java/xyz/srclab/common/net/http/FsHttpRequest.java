package xyz.srclab.common.net.http;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http request info, including URL, method, headers and body.
 *
 * @author fredsuvn
 */
public class FsHttpRequest {

    private URL url;
    private String method;
    private @Nullable Map<String, Object> headers;
    private @Nullable Object body;

    /**
     * Constructs with empty parameters.
     */
    public FsHttpRequest() {
    }

    /**
     * Constructs with url, method, headers and body.
     * See {@link #getUrl()}, {@link #getMethod()}, {@link #getHeaders()} and {@link #getBody()}.
     *
     * @param url     request url
     * @param method  request method
     * @param headers request headers
     * @param body    request body
     * @see #getUrl()
     * @see #getMethod()
     * @see #getHeaders()
     * @see #getBody()
     */
    public FsHttpRequest(URL url, String method, @Nullable Map<String, ?> headers, @Nullable Object body) {
        this.url = url;
        this.method = method;
        this.headers = FsCollect.immutableMap(headers);
        this.body = body;
    }

    /**
     * Constructs with url, method, headers and body.
     * See {@link #getUrl()}, {@link #getMethod()}, {@link #getHeaders()} and {@link #getBody()}.
     *
     * @param url     request url
     * @param method  request method
     * @param headers request headers
     * @param body    request body
     * @see #getUrl()
     * @see #getMethod()
     * @see #getHeaders()
     * @see #getBody()
     */
    public FsHttpRequest(String url, String method, @Nullable Map<String, ?> headers, @Nullable Object body) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new FsHttpException(e);
        }
        this.method = method;
        this.headers = FsCollect.immutableMap(headers);
        this.body = body;
    }

    /**
     * Returns request url.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Sets request url.
     *
     * @param url request url
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /**
     * Sets request url.
     *
     * @param url request url
     */
    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new FsHttpException(e);
        }
    }

    /**
     * Returns request method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets request method.
     *
     * @param method request method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns request headers.
     * <p>
     * Returned object is immutable.
     * If a value is instance of {@link Collection}, it will be considered as repeated header.
     * All values (and elements if the value is a collection) will be converted to string
     * by {@link String#valueOf(Object)} when put into actual http headers.
     */
    public Map<String, Object> getHeaders() {
        if (headers == null) {
            return Collections.emptyMap();
        }
        return FsCollect.immutableMap(headers);
    }

    /**
     * Adds header. If the value is instance of {@link Collection}, it will be considered as repeated header.
     *
     * @param key   header key
     * @param value header value
     */
    public void addHeader(String key, Object value) {
        if (headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, value);
    }

    /**
     * Adds header. If a value is instance of {@link Collection}, it will be considered as repeated header.
     *
     * @param headers headers to be added
     */
    public void addHeaders(Map<String, ?> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.putAll(headers);
    }

    /**
     * Returns request body, maybe null.
     * <p>
     * The type of request body is one of:
     * <ul>
     *     <li>
     *         {@link InputStream}: if the body's type is input stream, is will be transferred in chunked stream mode.
     *     </li>
     *     <li>
     *         {@link ByteBuffer}: if the body's type is byte buffer, is will be transferred in fixed length mode.
     *     </li>
     * </ul>
     */
    @Nullable
    public Object getBody() {
        return body;
    }

    /**
     * Sets request body.
     * Body of input stream type will be transferred in chunked stream mode
     *
     * @param body request body
     * @see #setBody(ByteBuffer)
     */
    public void setBody(InputStream body) {
        this.body = body;
    }

    /**
     * Sets request body.
     * Body of byte buffer type will be transferred in fixed length mode
     *
     * @param body request body
     * @see #setBody(InputStream)
     */
    public void setBody(ByteBuffer body) {
        this.body = body;
    }
}
