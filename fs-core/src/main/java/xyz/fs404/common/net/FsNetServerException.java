package xyz.fs404.common.net;

import java.net.ServerSocket;

/**
 * Exception for network server.
 *
 * @author fredsuvn
 */
public class FsNetServerException extends RuntimeException {

    private final Object source;

    /**
     * Constructs with server source object and cause.
     *
     * @param source server source object, such as {@link ServerSocket}
     * @param cause  the cause
     */
    public FsNetServerException(Object source, Throwable cause) {
        super(cause);
        this.source = source;
    }

    /**
     * Returns server source object, such as {@link ServerSocket}.
     */
    public Object getSource() {
        return source;
    }
}
