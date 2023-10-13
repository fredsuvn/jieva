package xyz.fsgik.common.net.http;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.collect.FsCollect;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Http request info, including URL, method, headers and body.
 *
 * @author fredsuvn
 */
public interface FsHttpRequest {

    /**
     * Returns a new builder.
     *
     * @return new builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns url.
     *
     * @return url
     */
    URL getUrl();

    /**
     * Returns method.
     *
     * @return method
     */
    String getMethod();

    /**
     * Returns headers.
     *
     * @return headers
     */
    @Nullable
    FsHttpHeaders getHeaders();

    /**
     * Returns body. There are two types of body.
     * <ul>
     *     <li>
     *         {@link InputStream}: if the body's type is input stream, is will be transferred in chunked stream mode.
     *     </li>
     *     <li>
     *         {@link ByteBuffer}: if the body's type is byte buffer, is will be transferred in fixed length mode.
     *     </li>
     * </ul>
     *
     * @return body
     */
    @Nullable
    Object getBody();

    /**
     * Builder for {@link FsHttpRequest}.
     */
    class Builder {

        private URL url;
        private String method;
        private @Nullable FsHttpHeaders headers;
        private @Nullable Object body;

        /**
         * Sets url.
         *
         * @param url url
         * @return this builder
         */
        public Builder url(URL url) {
            this.url = url;
            return this;
        }

        /**
         * Sets url.
         *
         * @param url url
         * @return this builder
         */
        public Builder url(String url) {
            try {
                this.url = new URL(url);
            } catch (MalformedURLException e) {
                throw new FsHttpException(e);
            }
            return this;
        }

        /**
         * Sets url with query string.
         *
         * @param url         url
         * @param queryString query string
         * @return this builder
         */
        public Builder url(String url, Map<String, String> queryString) {
            try {
                if (FsCollect.isEmpty(queryString)) {
                    this.url = new URL(url);
                } else {
                    String query = queryString.entrySet().stream().map(it ->
                        FsHttp.encodeQuery(it.getKey()) + "=" + FsHttp.encodeQuery(it.getValue())
                    ).collect(Collectors.joining("&"));
                    this.url = new URL(url + "?" + query);
                }
            } catch (MalformedURLException e) {
                throw new FsHttpException(e);
            }
            return this;
        }

        /**
         * Sets method.
         *
         * @param method method
         * @return this builder
         */
        public Builder method(String method) {
            this.method = method;
            return this;
        }

        /**
         * Sets headers.
         *
         * @param headers headers
         * @return this builder
         */
        public Builder headers(@Nullable FsHttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Sets body of {@link InputStream}.
         * The input stream type indicates it will be transferred in chunked stream mode, see {@link #getBody()}.
         *
         * @param body body
         * @return this builder
         * @see #getBody()
         */
        public Builder body(@Nullable InputStream body) {
            this.body = body;
            return this;
        }

        /**
         * Sets body of {@link ByteBuffer}.
         * The byte buffer type indicates it will be transferred in fixed length mode, see {@link #getBody()}.
         *
         * @param body body
         * @return this builder
         * @see #getBody()
         */
        public Builder body(@Nullable ByteBuffer body) {
            this.body = body;
            return this;
        }

        /**
         * Sets body of {@link ByteBuffer} wraps given body array.
         * The byte buffer type indicates it will be transferred in fixed length mode, see {@link #getBody()}.
         *
         * @param body body
         * @return this builder
         * @see #getBody()
         */
        public Builder body(byte[] body) {
            this.body = ByteBuffer.wrap(body);
            return this;
        }

        /**
         * Sets body of {@link ByteBuffer} wraps given body array of specified length from offset index.
         * The byte buffer type indicates it will be transferred in fixed length mode, see {@link #getBody()}.
         *
         * @param body   body
         * @param offset offset index
         * @param length specified length
         * @return this builder
         * @see #getBody()
         */
        public Builder body(byte[] body, int offset, int length) {
            this.body = ByteBuffer.wrap(body, offset, length);
            return this;
        }

        public FsHttpRequest build() {
            if (url == null) {
                throw new FsHttpException("url is not set.");
            }
            if (method == null) {
                throw new FsHttpException("method is not set.");
            }
            return new FsHttpRequest() {

                private final URL url = Builder.this.url;
                private final String method = Builder.this.method;
                private final @Nullable FsHttpHeaders headers = Builder.this.headers;
                private final @Nullable Object body = Builder.this.body;

                @Override
                public URL getUrl() {
                    return url;
                }

                @Override
                public String getMethod() {
                    return method;
                }

                @Override
                public @Nullable FsHttpHeaders getHeaders() {
                    return headers;
                }

                @Override
                public @Nullable Object getBody() {
                    return body;
                }
            };
        }
    }
}
