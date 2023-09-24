package xyz.srclab.common.net.http;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Http client interface.
 *
 * @author fredsuvn
 */
public interface FsHttpClient {

    /**
     * Return default http client.
     */
    static FsHttpClient defaultClient() {
        return Builder.INSTANCE;
    }

    /**
     * Returns new builder for this interface.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Requests with given request info
     *
     * @param request request info
     */
    FsHttpResponse request(FsHttpRequest request);

    /**
     * Builder for {@link FsHttpClient}.
     */
    class Builder {

        private static final FsHttpClient INSTANCE = newBuilder().build();

        private @Nullable Duration connectTimeout;
        private @Nullable Duration readTimeout;
        private int chunkSize = FsIO.IO_BUFFER_SIZE;
        private @Nullable Proxy proxy;

        /**
         * Sets connect timeout.
         *
         * @param connectTimeout connect timeout
         */
        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        /**
         * Sets read timeout.
         *
         * @param readTimeout read timeout
         */
        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets chunk size.
         *
         * @param chunkSize chunk size
         */
        public Builder chunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        /**
         * Sets proxy.
         *
         * @param proxy the proxy
         */
        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        FsHttpClient build() {
            return new FsHttpClientImpl(this);
        }

        private static final class FsHttpClientImpl implements FsHttpClient {

            private final @Nullable Duration connectTimeout;
            private final @Nullable Duration readTimeout;
            private final int chunkSize;
            private final @Nullable Proxy proxy;

            private FsHttpClientImpl(Builder builder) {
                this.connectTimeout = builder.connectTimeout;
                this.readTimeout = builder.readTimeout;
                this.chunkSize = builder.chunkSize;
                this.proxy = builder.proxy;
            }

            @Override
            public FsHttpResponse request(FsHttpRequest request) {
                try {
                    return doRequest(request);
                } catch (FsHttpException e) {
                    throw e;
                } catch (Exception e) {
                    throw new FsHttpException(e);
                }
            }

            private FsHttpResponse doRequest(FsHttpRequest request) throws Exception {
                URL url = request.getUrl();
                if (url == null) {
                    throw new FsHttpException("Null url.");
                }
                HttpURLConnection connection;
                if (proxy == null) {
                    URLConnection urlConnection = url.openConnection();
                    if (!(urlConnection instanceof HttpURLConnection)) {
                        throw new FsHttpException("Not a http url: " + urlConnection);
                    }
                    connection = Fs.as(urlConnection);
                } else {
                    URLConnection urlConnection = url.openConnection(proxy);
                    if (!(urlConnection instanceof HttpURLConnection)) {
                        throw new FsHttpException("Not a http url: " + urlConnection);
                    }
                    connection = Fs.as(urlConnection);
                }
                connection.setRequestMethod(request.getMethod());
                request.getHeaders().forEach((k, v) -> {
                    if (v instanceof Collection) {
                        Collection<?> cv = (Collection<?>) v;
                        for (Object o : cv) {
                            connection.setRequestProperty(k, String.valueOf(o));
                        }
                    } else {
                        connection.setRequestProperty(k, String.valueOf(v));
                    }
                });
                if (connectTimeout != null) {
                    connection.setConnectTimeout((int) connectTimeout.toMillis());
                }
                if (readTimeout != null) {
                    connection.setReadTimeout((int) readTimeout.toMillis());
                }
                Object requestBody = request.getBody();
                connection.setDoInput(true);
                if (requestBody != null) {
                    connection.setDoOutput(true);
                    OutputStream out = connection.getOutputStream();
                    if (requestBody instanceof InputStream) {
                        connection.setChunkedStreamingMode(chunkSize);
                        FsIO.readBytesTo((InputStream) requestBody, out);
                    } else if (requestBody instanceof ByteBuffer) {
                        connection.setFixedLengthStreamingMode(((ByteBuffer) requestBody).remaining());
                        FsIO.readBytesTo(FsIO.toInputStream((ByteBuffer) requestBody), out);
                    } else {
                        throw new FsHttpException("Invalid body type: " + requestBody.getClass());
                    }
                    out.flush();
                    out.close();
                }
                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();
                Map<String, List<String>> responseHeaders = connection.getHeaderFields();
                final InputStream responseBody = connection.getInputStream();
                if (requestBody == null) {
                    connection.disconnect();
                } else {
                    return new FsHttpResponse(responseCode, responseMessage, responseHeaders, new InputWithConnection(responseBody, connection));
                }
                return new FsHttpResponse(responseCode, responseMessage, responseHeaders, responseBody);
            }

            private static final class InputWithConnection extends InputStream {
                private final InputStream source;
                private final HttpURLConnection connection;

                private InputWithConnection(InputStream source, HttpURLConnection connection) {
                    this.source = source;
                    this.connection = connection;
                }

                @Override
                public int read() throws IOException {
                    return source.read();
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return source.read(b);
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    return source.read(b, off, len);
                }

                @Override
                public long skip(long n) throws IOException {
                    return source.skip(n);
                }

                @Override
                public int available() throws IOException {
                    return source.available();
                }

                @Override
                public void close() throws IOException {
                    source.close();
                    connection.disconnect();
                }

                @Override
                public void mark(int readlimit) {
                    source.mark(readlimit);
                }

                @Override
                public void reset() throws IOException {
                    source.reset();
                }

                @Override
                public boolean markSupported() {
                    return source.markSupported();
                }
            }
        }
    }
}
