package xyz.srclab.common.net.http;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Http response info, including status, headers and body.
 * <p>
 * If the body of response is not null, that means the connection may not have been released yet,
 * call {@link InputStream#close()} to release the connection. See {@link #getBody()}.
 *
 * @author fredsuvn
 */
public class FsHttpResponse {

    private int statusCode;
    private @Nullable String statusReason;
    private @Nullable Map<String, Object> headers;
    private @Nullable InputStream body;

    /**
     * Constructs with empty parameters.
     */
    public FsHttpResponse() {
    }

    /**
     * Constructs with status, headers and body.
     * See {@link #getStatusCode()}, {@link #getStatusReason()}, {@link #getHeaders()} and {@link #getBody()}.
     *
     * @param statusCode   status code
     * @param statusReason status reason
     * @param headers      response headers
     * @param body         response body
     * @see #getStatusCode()
     * @see #getStatusReason()
     * @see #getHeaders()
     * @see #getBody()
     */
    public FsHttpResponse(
        int statusCode, @Nullable String statusReason, @Nullable Map<String, ?> headers, @Nullable InputStream body) {
        this.statusCode = statusCode;
        this.statusReason = statusReason;
        this.headers = FsCollect.immutableMap(headers);
        this.body = body;
    }

    /**
     * Returns status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets status code.
     *
     * @param statusCode status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Returns status reason.
     */
    public String getStatusReason() {
        return statusReason;
    }

    /**
     * Sets status reason.
     *
     * @param statusReason status reason
     */
    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    /**
     * Returns response headers.
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
     * Returns response body, maybe null.
     * <p>
     * If the stream is not null, that means the http connection may not have been released,
     * call {@link InputStream#close()} to release the connection.
     */
    @Nullable
    public InputStream getBody() {
        return body;
    }

    /**
     * Sets response body.
     *
     * @param body response body
     */
    public void setBody(InputStream body) {
        this.body = body;
    }
}
