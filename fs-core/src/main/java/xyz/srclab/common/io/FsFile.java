package xyz.srclab.common.io;

import xyz.srclab.common.base.FsString;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * File utilities.
 *
 * @author fresduvn
 */
public class FsFile {

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     *
     * @param path given file of path
     */
    public static byte[] readFile(Path path) {
        return readFile(path, 0, -1);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     */
    public static byte[] readFile(Path path, long offset, long length) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return FsIO.readBytes(FsIO.toInputStream(random, offset, length));
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     * The read bytes will be encoded to String with {@link FsString#CHARSET}.
     *
     * @param path given file of path
     */
    public static String readFileString(Path path) {
        return readFileString(path, 0, -1);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     * The read bytes will be encoded to String with {@link FsString#CHARSET}.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to read to end of file
     */
    public static String readFileString(Path path, long offset, long length) {
        return readFileString(path, offset, length, FsString.CHARSET);
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     * The read bytes will be encoded to String with given charset.
     *
     * @param path    given file of path
     * @param charset given charset
     */
    public static String readFileString(Path path, Charset charset) {
        return readFileString(path, 0, -1, charset);
    }

    /**
     * Using {@link RandomAccessFile} to read given length bytes of given file of path from offset position,
     * the given length may be set to -1 to read to end of file.
     * The read bytes will be encoded to String with given charset.
     *
     * @param path    given file of path
     * @param offset  offset position
     * @param length  given length, maybe -1 to read to end of file
     * @param charset given charset
     */
    public static String readFileString(Path path, long offset, long length, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "r")) {
            return FsIO.readString(FsIO.toInputStream(random, offset, length), charset);
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to write  bytes into given file of path.
     *
     * @param path given file of path
     */
    public static void writeFile(Path path, InputStream data) {
        writeFile(path, 0, -1, data);
    }

    /**
     * Using {@link RandomAccessFile} to write given length bytes into given file of path from offset position,
     * the given length may be set to -1 to write unlimitedly.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     */
    public static void writeFile(Path path, long offset, long length, InputStream data) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rws")) {
            FsIO.readBytesTo(data, FsIO.toOutputStream(random, offset, length));
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    /**
     * Using {@link RandomAccessFile} to read all bytes of given file of path.
     * The read bytes will be encoded to String with given charset.
     *
     * @param path    given file of path
     * @param charset given charset
     */
    public static void writeFileString(Path path, CharSequence data, Charset charset) {
         writeFileString(path, 0, -1, data,charset);
    }

    /**
     * Using {@link RandomAccessFile} to write given data into given file of path from offset position,
     * the given length may be set to -1 to write unlimitedly.
     * The written bytes will be decoded from given data with given charset.
     *
     * @param path   given file of path
     * @param offset offset position
     * @param length given length, maybe -1 to write unlimitedly
     * @param data given data
     * @param charset given charset
     */
    public static void writeFileString(Path path, long offset, long length, CharSequence data, Charset charset) {
        try (RandomAccessFile random = new RandomAccessFile(path.toFile(), "rws")) {
            FsIO.toWriter(FsIO.toOutputStream(random, offset, length), charset).append(data);
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }
}
