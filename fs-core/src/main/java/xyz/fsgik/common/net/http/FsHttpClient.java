package xyz.fsgik.common.net.http;

import lombok.Getter;
import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.io.FsIO;

import java.net.Proxy;
import java.time.Duration;

/**
 * Http client interface.
 *
 * @author fredsuvn
 */
@ThreadSafe
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
    @Getter
    class Builder {

        private static final FsHttpClient INSTANCE = newBuilder().build();

        /**
         * Connection time out.
         */
        private @Nullable Duration connectTimeout;
        /**
         * Read time out.
         */
        private @Nullable Duration readTimeout;
        /**
         * Chunk size.
         */
        private int chunkSize = FsIO.IO_BUFFER_SIZE;
        /**
         * Proxy info.
         */
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
            return new HttpClientImpl(this);
        }
    }
}
