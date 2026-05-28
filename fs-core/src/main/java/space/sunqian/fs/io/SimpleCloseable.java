package space.sunqian.fs.io;

import java.io.Closeable;

/**
 * This interface is the runtime exception declaration version for {@link Closeable}, usually used in scenarios where
 * forced exception handling is not required.
 *
 * @author sunqian
 */
public interface SimpleCloseable extends Closeable {

    /**
     * Close this resource with runtime exception declaration.
     *
     * @throws IORuntimeException if an error occurs during the close operation
     */
    @Override
    void close() throws IORuntimeException;
}
