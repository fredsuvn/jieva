package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.codec.FsCodecException;
import xyz.srclab.common.io.FsIO;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.Key;

/**
 * Denotes cipher for encrypting/decrypting, maybe has a backed {@link Cipher}.
 *
 * @author fredsuvn
 * @see Cipher
 */
public interface FsCipher {

    /**
     * Returns backed {@link Cipher} if it has, or null if it doesn't have one.
     * The backed {@link Cipher} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Cipher getCipher();

    /**
     * Encrypts source array.
     *
     * @param source source array
     */
    default byte[] encrypt(Key key, byte[] source) {
        return encrypt(key, source, 0, source.length);
    }

    /**
     * Encrypts source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    default byte[] encrypt(Key key, byte[] source, int offset, int length) {
        return encrypt(key, source, offset, length, null);
    }

    /**
     * Encrypts source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     * @param params algorithm parameters
     */
    byte[] encrypt(Key key, byte[] source, int offset, int length, @Nullable AlgorithmParams params);

    /**
     * Encrypts source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     */
    default int encrypt(Key key, byte[] source, byte[] dest) {
        return encrypt(key, source, 0, dest, 0, source.length);
    }

    /**
     * Encrypts source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    default int encrypt(Key key, byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        return encrypt(key, source, sourceOffset, dest, destOffset, length, null);
    }

    /**
     * Encrypts source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     * @param params       algorithm parameters
     */
    default int encrypt(
        Key key,
        byte[] source, int sourceOffset,
        byte[] dest, int destOffset,
        int length,
        @Nullable AlgorithmParams params
    ) {
        try {
            ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
            ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
            return encrypt(key, in, out, params);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encrypts source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    default ByteBuffer encrypt(Key key, ByteBuffer source) {
        return encrypt(key, source, (AlgorithmParams) null);
    }

    /**
     * Encrypts source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     * @param params algorithm parameters
     */
    default ByteBuffer encrypt(Key key, ByteBuffer source, @Nullable AlgorithmParams params) {
        try {
            byte[] src = FsIO.getBytes(source);
            return ByteBuffer.wrap(encrypt(key, src, 0, src.length, params));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encrypts source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    default int encrypt(Key key, ByteBuffer source, ByteBuffer dest) {
        return encrypt(key, source, dest, null);
    }

    /**
     * Encrypts source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     * @param params algorithm parameters
     */
    default int encrypt(Key key, ByteBuffer source, ByteBuffer dest, @Nullable AlgorithmParams params) {
        try {
            OutputStream out = FsIO.toOutputStream(dest);
            int result = (int) encrypt(key, FsIO.toInputStream(source), out, params);
            out.flush();
            return result;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encrypts source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     */
    default long encrypt(Key key, InputStream source, OutputStream dest) {
        return encrypt(key, source, dest, null);
    }

    /**
     * Encrypts source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     * @param params algorithm parameters
     */
    long encrypt(Key key, InputStream source, OutputStream dest, @Nullable AlgorithmParams params);

    /**
     * Decrypts source array.
     *
     * @param source source array
     */
    default byte[] decrypt(Key key, byte[] source) {
        return decrypt(key, source, 0, source.length);
    }

    /**
     * Decrypts source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    default byte[] decrypt(Key key, byte[] source, int offset, int length) {
        return decrypt(key, source, offset, length, null);
    }

    /**
     * Decrypts source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     * @param params algorithm parameters
     */
    byte[] decrypt(Key key, byte[] source, int offset, int length, @Nullable AlgorithmParams params);

    /**
     * Decrypts source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     */
    default int decrypt(Key key, byte[] source, byte[] dest) {
        return decrypt(key, source, 0, dest, 0, source.length);
    }

    /**
     * Decrypts source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    default int decrypt(Key key, byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        return decrypt(key, source, sourceOffset, dest, destOffset, length, null);
    }

    /**
     * Decrypts source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     * @param params       algorithm parameters
     */
    default int decrypt(
        Key key,
        byte[] source, int sourceOffset,
        byte[] dest, int destOffset,
        int length,
        @Nullable AlgorithmParams params
    ) {
        try {
            ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
            ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
            return decrypt(key, in, out, params);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decrypts source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    default ByteBuffer decrypt(Key key, ByteBuffer source) {
        return decrypt(key, source, (AlgorithmParams) null);
    }

    /**
     * Decrypts source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     * @param params algorithm parameters
     */
    default ByteBuffer decrypt(Key key, ByteBuffer source, @Nullable AlgorithmParams params) {
        try {
            byte[] src = FsIO.getBytes(source);
            return ByteBuffer.wrap(decrypt(key, src, 0, src.length, params));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decrypts source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    default int decrypt(Key key, ByteBuffer source, ByteBuffer dest) {
        return decrypt(key, source, dest, null);
    }

    /**
     * Decrypts source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     * @param params algorithm parameters
     */
    default int decrypt(Key key, ByteBuffer source, ByteBuffer dest, @Nullable AlgorithmParams params) {
        try {
            OutputStream out = FsIO.toOutputStream(dest);
            int result = (int) decrypt(key, FsIO.toInputStream(source), out, params);
            out.flush();
            return result;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decrypts source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     */
    default long decrypt(Key key, InputStream source, OutputStream dest) {
        return decrypt(key, source, dest, null);
    }

    /**
     * Decrypts source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     * @param params algorithm parameters
     */
    long decrypt(Key key, InputStream source, OutputStream dest, @Nullable AlgorithmParams params);
}
