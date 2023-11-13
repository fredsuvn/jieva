package xyz.fsgek.common.codec;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.io.GekIO;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.function.Supplier;

/**
 * Codec utilities.
 *
 * @author fredsuvn
 */
public class GekCodec {

    /**
     * Returns a base64 codec process.
     *
     * @return a base64 codec process
     */
    public static Base64Codec base64() {
        return new Base64Codec();
    }

    /**
     * Returns a hex codec process.
     *
     * @return a hex codec process
     */
    public static HexCodec hex() {
        return new HexCodec();
    }

    /**
     * Returns a cipher codec process of specified cipher to encryption/decryption.
     * Note when the process starts, the status of the cipher cannot be changed.
     *
     * @param cipher specified cipher
     * @return a cipher codec process of specified cipher
     */
    public static CipherCodec cipher(Cipher cipher) {
        return cipher(() -> cipher);
    }

    /**
     * Returns a cipher codec process of specified cipher supplier to encryption/decryption.
     * {@link Supplier#get()} will be called before encryption/decryption each time.
     *
     * @param cipher specified cipher supplier
     * @return a cipher codec process of specified cipher supplier
     */
    public static CipherCodec cipher(Supplier<Cipher> cipher) {
        return new CipherCodec(cipher);
    }

    /**
     * Returns key factory with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return key factory with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static KeyFactory keyFactory(String algorithm) throws GekCodecException {
        try {
            return KeyFactory.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns key factory with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return key factory with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static KeyFactory keyFactory(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? KeyFactory.getInstance(algorithm) : KeyFactory.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns key generator with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return key generator with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static KeyGenerator keyGenerator(String algorithm) throws GekCodecException {
        try {
            return KeyGenerator.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns key generator with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return key generator with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static KeyGenerator keyGenerator(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? KeyGenerator.getInstance(algorithm) : KeyGenerator.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns key pair generator with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return key pair generator with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static KeyPairGenerator keyPairGenerator(String algorithm) throws GekCodecException {
        try {
            return KeyPairGenerator.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns key pair generator with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return key pair generator with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static KeyPairGenerator keyPairGenerator(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? KeyPairGenerator.getInstance(algorithm) : KeyPairGenerator.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns cipher with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return cipher with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static Cipher cipher(String algorithm) throws GekCodecException {
        try {
            return Cipher.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns cipher with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return cipher with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static Cipher cipher(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns message digest with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return message digest with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static MessageDigest messageDigest(String algorithm) throws GekCodecException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns message digest with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return message digest with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static MessageDigest messageDigest(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? MessageDigest.getInstance(algorithm) : MessageDigest.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns MAC with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return MAC with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static Mac mac(String algorithm) throws GekCodecException {
        try {
            return Mac.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns MAC with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return MAC with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static Mac mac(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? Mac.getInstance(algorithm) : Mac.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns signature with specified algorithm.
     *
     * @param algorithm specified algorithm
     * @return signature with specified algorithm
     * @throws GekCodecException codec exception
     */
    public static Signature signature(String algorithm) throws GekCodecException {
        try {
            return Signature.getInstance(algorithm);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Returns signature with specified algorithm and provider.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return signature with specified algorithm and provider
     * @throws GekCodecException codec exception
     */
    public static Signature signature(String algorithm, @Nullable Provider provider) throws GekCodecException {
        try {
            return provider == null ? Signature.getInstance(algorithm) : Signature.getInstance(algorithm, provider);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
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
                ByteBuffer r = GekIO.readSlice(in, inSize);
                outSize += cipher.doFinal(r, out);
                remaining -= inSize;
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
                byte[] result = doCipher(cipher, in);
                if (result == null) {
                    return 0;
                }
                out.write(result);
                return result.length;
            }
            int remaining = in.remaining();
            long outSize = 0;
            while (remaining > 0) {
                int inSize = Math.min(remaining, blockSize);
                ByteBuffer r = GekIO.readSlice(in, inSize);
                byte[] outBytes = doCipher(cipher, r);
                if (outBytes != null) {
                    out.write(outBytes);
                    outSize += outBytes.length;
                    remaining -= inSize;
                }
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
                if (inBytes == null) {
                    return 0;
                }
                return cipher.doFinal(ByteBuffer.wrap(inBytes), out);
            }
            int outSize = 0;
            byte[] inBytes = new byte[blockSize];
            while (true) {
                int readCount = GekIO.readTo(in, inBytes);
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
                if (inBytes == null) {
                    return 0;
                }
                byte[] result = cipher.doFinal(inBytes);
                out.write(result);
                return result.length;
            }
            int outSize = 0;
            byte[] inBytes = new byte[blockSize];
            while (true) {
                int readCount = GekIO.readTo(in, inBytes);
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

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input buffer into an array and returns.
     * If input is ended without any byte read, return null.
     * This method will encrypt/decrypt all the data at once without blocking.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher given {@link Cipher}
     * @param in     input buffer
     * @return an array, or null if input is ended without any byte read
     * @throws GekCodecException codec exception
     */
    @Nullable
    public static byte[] doCipher(Cipher cipher, ByteBuffer in) throws GekCodecException {
        try {
            if (!in.hasRemaining()) {
                return null;
            }
            if (in.hasArray()) {
                return cipher.doFinal(in.array(), in.arrayOffset() + in.position(), in.remaining());
            }
            byte[] inBytes = GekIO.read(in);
            return cipher.doFinal(inBytes);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    /**
     * Encrypts/Decrypts with given {@link Cipher} from input stream into an array and returns.
     * If input is ended without any byte read, return null.
     * This method will encrypt/decrypt all the data at once without blocking.
     * This method does not initialize the cipher, so it needs to be initialized before calling this method.
     *
     * @param cipher given {@link Cipher}
     * @param in     input stream
     * @return an array, or null if input is ended without any byte read
     * @throws GekCodecException codec exception
     */
    @Nullable
    public static byte[] doCipher(Cipher cipher, InputStream in) throws GekCodecException {
        try {
            byte[] inBytes = GekIO.read(in);
            if (inBytes == null) {
                return null;
            }
            return cipher.doFinal(inBytes);
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
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
     * @param digest given {@link MessageDigest}
     * @param in     input stream
     * @return digest result
     * @throws GekCodecException codec exception
     */
    public static byte[] doDigest(MessageDigest digest, InputStream in) throws GekCodecException {
        return doDigest(digest, in, GekIO.IO_BUFFER_SIZE);
    }

    /**
     * Digests with given {@link MessageDigest} from input stream, and return digest result.
     * This method does not initialize the digest, so it needs to be initialized before calling this method.
     *
     * @param digest     given {@link MessageDigest}
     * @param in         input stream
     * @param bufferSize buffer size, may &lt;= 0 to read all bytes at once
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
                int readCount = GekIO.readTo(in, inBytes);
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
     * @param mac given {@link Mac}
     * @param in  input stream
     * @return MAC result
     * @throws GekCodecException codec exception
     */
    public static byte[] doMac(Mac mac, InputStream in) throws GekCodecException {
        return doMac(mac, in, GekIO.IO_BUFFER_SIZE);
    }

    /**
     * Generates MAC with given {@link Mac} from input stream, and return MAC result.
     * This method does not initialize the mac, so it needs to be initialized before calling this method.
     *
     * @param mac        given {@link Mac}
     * @param in         input stream
     * @param bufferSize buffer size, may &lt;= 0 to read all bytes at once
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
                int readCount = GekIO.readTo(in, inBytes);
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
     * @param sign given {@link Signature}
     * @param in   input stream
     * @return signature result
     * @throws GekCodecException codec exception
     */
    public static byte[] doSign(Signature sign, InputStream in) throws GekCodecException {
        return doSign(sign, in, GekIO.IO_BUFFER_SIZE);
    }

    /**
     * Generates signature with given {@link Signature} from input stream, and return signature result.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign       given {@link Signature}
     * @param in         input stream
     * @param bufferSize buffer size, may &lt;= 0 to read all bytes at once
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
                int readCount = GekIO.readTo(in, inBytes);
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
     * Verifies signature with given {@link Signature} from input stream and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign      given {@link Signature}
     * @param in        input stream
     * @param signature signature
     * @return result of verifying
     * @throws GekCodecException codec exception
     */
    public static boolean doVerify(Signature sign, InputStream in, byte[] signature) throws GekCodecException {
        return doVerify(sign, in, GekIO.IO_BUFFER_SIZE, signature);
    }

    /**
     * Verifies signature with given {@link Signature} from input buffer and signature.
     * This method does not initialize the sign, so it needs to be initialized before calling this method.
     *
     * @param sign       given {@link Signature}
     * @param in         input stream
     * @param bufferSize buffer size, may &lt;= 0 to read all bytes at once
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
                int readCount = GekIO.readTo(in, inBytes);
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
}
