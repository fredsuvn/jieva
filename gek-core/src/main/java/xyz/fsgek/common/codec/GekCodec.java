package xyz.fsgek.common.codec;

import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;

public class GekCodec {

    public static CipherDataProcessor cipher(Cipher cipher) {
        return new CipherDataProcessor(cipher);
    }

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input buffer into output buffer.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher    given {@link Cipher}
     * @param in        input buffer
     * @param out       output buffer
     * @param blockSize block size of input data, maybe 0 if computing without dividing in blocks
     * @return the number of bytes stored in output
     * @throws GekCodecException codec exception
     */
    public static int doCipher(Cipher cipher, ByteBuffer in, ByteBuffer out, int blockSize) throws GekCodecException {
        try {
            if (blockSize <= 0) {
                return cipher.doFinal(in, out);
            }
            int remaining = in.remaining();
            int outSize = 0;
            while (remaining > 0) {
                int inSize = Math.min(remaining, blockSize);
                ByteBuffer r = GekBuffer.slice(in, inSize);
                outSize += cipher.doFinal(r, out);
                remaining -= inSize;
                in.position(in.position() + inSize);
            }
            return outSize;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input buffer into output stream.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher    given {@link Cipher}
     * @param in        input buffer
     * @param out       output stream
     * @param blockSize block size of input data, maybe 0 if computing without dividing in blocks
     * @return the number of bytes stored in output
     * @throws GekCodecException codec exception
     */
    public static long doCipher(Cipher cipher, ByteBuffer in, OutputStream out, int blockSize) throws GekCodecException {
        try {
            if (blockSize <= 0) {
                byte[] result = doFinal(cipher, in);
                out.write(result);
                return result.length;
            }
            int remaining = in.remaining();
            long outSize = 0;
            while (remaining > 0) {
                int inSize = Math.min(remaining, blockSize);
                ByteBuffer r = GekBuffer.slice(in, inSize);
                byte[] outBytes = doFinal(cipher, r);
                out.write(outBytes);
                outSize += outBytes.length;
                remaining -= inSize;
                in.position(in.position() + inSize);
            }
            return outSize;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input stream into output buffer.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher    given {@link Cipher}
     * @param in        input stream
     * @param out       output buffer
     * @param blockSize block size of input data, maybe 0 if computing without dividing in blocks
     * @return the number of bytes stored in output
     * @throws GekCodecException codec exception
     */
    public static int doCipher(Cipher cipher, InputStream in, ByteBuffer out, int blockSize) throws GekCodecException {
        try {
            if (blockSize <= 0) {
                byte[] inBytes = GekIO.read(in);
                return cipher.doFinal(ByteBuffer.wrap(inBytes), out);
            }
            int outSize = 0;
            byte[] inBytes = new byte[blockSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                ByteBuffer r = ByteBuffer.wrap(inBytes, 0, readCount);
                outSize += cipher.doFinal(r, out);
                if (readCount < blockSize) {
                    break;
                }
            }
            return outSize;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input stream into output stream.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher    given {@link Cipher}
     * @param in        input stream
     * @param out       output stream
     * @param blockSize block size of input data, maybe 0 if computing without dividing in blocks
     * @return the number of bytes stored in output
     * @throws GekCodecException codec exception
     */
    public static long doCipher(Cipher cipher, InputStream in, OutputStream out, int blockSize) throws GekCodecException {
        try {
            if (blockSize <= 0) {
                byte[] inBytes = GekIO.read(in);
                byte[] result = cipher.doFinal(inBytes);
                out.write(result);
                return result.length;
            }
            int outSize = 0;
            byte[] inBytes = new byte[blockSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                byte[] outBytes = cipher.doFinal(inBytes, 0, readCount);
                out.write(outBytes);
                outSize += outBytes.length;
                if (readCount < blockSize) {
                    break;
                }
            }
            return outSize;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    private static byte[] doFinal(Cipher cipher, ByteBuffer in) throws IllegalBlockSizeException, BadPaddingException {
        if (in.hasArray()) {
            return cipher.doFinal(in.array(), in.arrayOffset() + in.position(), in.remaining());
        }
        byte[] inBytes = GekBuffer.getBytes(in);
        return cipher.doFinal(inBytes);
    }

    /**
     * Digests with given {@link MessageDigest} from input buffer, and return digest result.
     * This method does not initialize the digest, so it needs to be initialized before calling this method.
     *
     * @param digest given {@link MessageDigest}
     * @param in     input buffer
     * @return digest result
     * @throws GekCodecException codec exception
     */
    public static byte[] doDigest(MessageDigest digest, ByteBuffer in) throws GekCodecException {
        try {
            digest.update(in);
            return digest.digest();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Digests with given {@link MessageDigest} from input stream, and return digest result.
     * This method does not initialize the digest, so it needs to be initialized before calling this method.
     *
     * @param digest     given {@link MessageDigest}
     * @param in         input stream
     * @param bufferSize buffer size of input data, maybe 0 if computing without dividing in blocks
     * @return digest result
     * @throws GekCodecException codec exception
     */
    public static byte[] doDigest(MessageDigest digest, InputStream in, int bufferSize) throws GekCodecException {
        try {
            if (bufferSize <= 0) {
                byte[] input = GekIO.read(in);
                return digest.digest(input);
            }
            byte[] inBytes = new byte[bufferSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                digest.update(inBytes, 0, readCount);
                if (readCount < bufferSize) {
                    break;
                }
            }
            return digest.digest();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Generates MAC with given {@link Mac} from input buffer, and return MAC result.
     * This method does not initialize the mac, so it needs to be initialized before calling this method.
     *
     * @param mac given {@link Mac}
     * @param in  input buffer
     * @return MAC result
     * @throws GekCodecException codec exception
     */
    public static byte[] doMac(Mac mac, ByteBuffer in) throws GekCodecException {
        try {
            mac.update(in);
            return mac.doFinal();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Generates MAC with given {@link Mac} from input stream, and return MAC result.
     * This method does not initialize the mac, so it needs to be initialized before calling this method.
     *
     * @param mac        given {@link Mac}
     * @param in         input stream
     * @param bufferSize buffer size of input data, maybe 0 if computing without dividing in blocks
     * @return MAC result
     * @throws GekCodecException codec exception
     */
    public static byte[] doMac(Mac mac, InputStream in, int bufferSize) throws GekCodecException {
        try {
            if (bufferSize <= 0) {
                byte[] input = GekIO.read(in);
                return mac.doFinal(input);
            }
            byte[] inBytes = new byte[bufferSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                mac.update(inBytes, 0, readCount);
                if (readCount < bufferSize) {
                    break;
                }
            }
            return mac.doFinal();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Generates signature with given {@link Signature} from input buffer, and return signature result.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign given {@link Signature}
     * @param in   input buffer
     * @return signature result
     * @throws GekCodecException codec exception
     */
    public static byte[] doSign(Signature sign, ByteBuffer in) throws GekCodecException {
        try {
            sign.update(in);
            return sign.sign();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Generates signature with given {@link Signature} from input stream, and return signature result.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign       given {@link Signature}
     * @param in         input stream
     * @param bufferSize buffer size of input data, maybe 0 if computing without dividing in blocks
     * @return signature result
     * @throws GekCodecException codec exception
     */
    public static byte[] doSign(Signature sign, InputStream in, int bufferSize) throws GekCodecException {
        try {
            if (bufferSize <= 0) {
                byte[] input = GekIO.read(in);
                sign.update(input);
                return sign.sign();
            }
            byte[] inBytes = new byte[bufferSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                sign.update(inBytes, 0, readCount);
                if (readCount < bufferSize) {
                    break;
                }
            }
            return sign.sign();
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Verifies signature with given {@link Signature} from input buffer and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign      given {@link Signature}
     * @param in        input buffer
     * @param signature signature
     * @return result of verifying
     * @throws GekCodecException codec exception
     */
    public static boolean doVerify(Signature sign, ByteBuffer in, byte[] signature) throws GekCodecException {
        try {
            sign.update(in);
            return sign.verify(signature);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Verifies signature with given {@link Signature} from input buffer and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign      given {@link Signature}
     * @param in        input buffer
     * @param signature signature
     * @return result of verifying
     * @throws GekCodecException codec exception
     */
    public static boolean doVerify(Signature sign, ByteBuffer in, ByteBuffer signature) throws GekCodecException {
        try {
            sign.update(in);
            return doVerifyFinal(sign, signature);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Verifies signature with given {@link Signature} from input buffer and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign       given {@link Signature}
     * @param in         input stream
     * @param bufferSize buffer size of input data, maybe 0 if computing without dividing in blocks
     * @param signature  signature
     * @return result of verifying
     * @throws GekCodecException codec exception
     */
    public static boolean doVerify(Signature sign, InputStream in, int bufferSize, byte[] signature) throws GekCodecException {
        try {
            if (bufferSize <= 0) {
                byte[] input = GekIO.read(in);
                sign.update(input);
                return sign.verify(signature);
            }
            byte[] inBytes = new byte[bufferSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                sign.update(inBytes, 0, readCount);
                if (readCount < bufferSize) {
                    break;
                }
            }
            return sign.verify(signature);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Verifies signature with given {@link Signature} from input buffer and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign       given {@link Signature}
     * @param in         input stream
     * @param bufferSize buffer size of input data, maybe 0 if computing without dividing in blocks
     * @param signature  signature
     * @return result of verifying
     * @throws GekCodecException codec exception
     */
    public static boolean doVerify(Signature sign, InputStream in, int bufferSize, ByteBuffer signature) throws GekCodecException {
        try {
            if (bufferSize <= 0) {
                byte[] input = GekIO.read(in);
                sign.update(input);
                return doVerifyFinal(sign, signature);
            }
            byte[] inBytes = new byte[bufferSize];
            while (true) {
                int readCount = in.read(inBytes);
                if (readCount < 0) {
                    break;
                }
                if (readCount == 0) {
                    continue;
                }
                sign.update(inBytes, 0, readCount);
                if (readCount < bufferSize) {
                    break;
                }
            }
            return doVerifyFinal(sign, signature);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    private static boolean doVerifyFinal(Signature sign, ByteBuffer signature) throws SignatureException {
        if (signature.hasArray()) {
            return sign.verify(
                signature.array(), signature.arrayOffset() + signature.position(), signature.remaining());
        }
        byte[] bytes = GekBuffer.getBytes(signature);
        return sign.verify(bytes);
    }
}
