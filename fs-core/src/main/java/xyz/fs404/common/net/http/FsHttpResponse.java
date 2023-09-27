package xyz.fs404.common.net.http;

import xyz.fs404.annotations.Nullable;
import xyz.fs404.common.collect.FsCollect;

import java.io.InputStream;
import java.util.Map;

/**
 * Http response info, including status, headers and body.
 * <p>
 * If the body of response is not null, that means the connection may not have been released yet,
 * call {@link InputStream#close()} to release the connection. See {@link #getBody()}.
 *
 * @author fredsuvn
 */
public class FsHttpResponse extends FsHttpContent {

    private int statusCode;
    private @Nullable String statusReason;
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
        this.body = body;
        if (FsCollect.isNotEmpty(headers)) {
            addHeaders(headers);
        }
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
