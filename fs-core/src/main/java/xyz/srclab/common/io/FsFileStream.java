package xyz.srclab.common.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * File stream which can input/output from specified position.
 *
 * @author fredsuvn
 */
public interface FsFileStream {

    /**
     * Opens an input stream for file of given path at specified file position,
     * data of the stream comes from cache or underlying file (if cache not found or expired).
     *
     * @param path   given path
     * @param offset specified file position
     */
    InputStream getInputStream(long offset);

    /**
     * Opens an output stream for file of given path at specified file position,
     * and cache will be updated after writing.
     *
     * @param path   given path
     * @param offset specified file position
     */
    OutputStream getOutputStream(long offset);

    /**
     * Sets new file length, truncated or extended.
     *
     * @param newLength new length
     */
    void setFileLength(long newLength);

    FileChannel getChannel();
}
