package xyz.fsgek.common.codec;

import org.springframework.core.codec.CodecException;
import xyz.fsgek.common.base.ref.GekRef;
import xyz.fsgek.common.data.GekDataProcess;
import xyz.fsgek.common.io.GekIO;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

/**
 * Codec implementation of {@link GekDataProcess} to encrypt/decrypt with {@link Cipher}.
 * When the process starts, the status of the cipher cannot be changed.
 *
 * @author fredsuvn
 */
public class CipherCodec implements GekDataProcess<CipherCodec> {

    private final Supplier<Cipher> cipher;
    private Object input;
    private Object output;
    private int blockSize = 0;
    private SecureRandom secureRandom;
    private Certificate certificate;
    private AlgorithmParameters algorithmParameters;
    private AlgorithmParameterSpec algorithmParameterSpec;
    private int mode;
    private Key key;

    /**
     * Constructs with given {@link Cipher} supplier.
     * {@link Supplier#get()} will be called before encryption/decryption each time.
     *
     * @param cipher given {@link Cipher} supplier
     */
    public CipherCodec(Supplier<Cipher> cipher) {
        this.cipher = cipher;
    }

    @Override
    public CipherCodec input(byte[] array) {
        return input(ByteBuffer.wrap(array));
    }

    @Override
    public CipherCodec input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public CipherCodec input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public CipherCodec output(byte[] array) {
        return output(ByteBuffer.wrap(array));
    }

    @Override
    public CipherCodec output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public CipherCodec output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets block size for encryption/decryption operations.
     *
     * @param blockSize block size
     * @return this
     */
    public CipherCodec blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * Sets secure random.
     *
     * @param secureRandom secure random
     * @return this
     */
    public CipherCodec secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    /**
     * Sets certificate.
     *
     * @param certificate certificate
     * @return this
     */
    public CipherCodec certificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    /**
     * Sets algorithm parameters.
     *
     * @param algorithmParameters algorithm parameters
     * @return this
     */
    public CipherCodec algorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    /**
     * Sets algorithm parameter spec.
     *
     * @param algorithmParameterSpec algorithm parameter spec
     * @return this
     */
    public CipherCodec algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#ENCRYPT_MODE}.
     *
     * @return this
     */
    public CipherCodec encrypt() {
        this.mode = Cipher.ENCRYPT_MODE;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#DECRYPT_MODE}.
     *
     * @return this
     */
    public CipherCodec decrypt() {
        this.mode = Cipher.DECRYPT_MODE;
        return this;
    }

    /**
     * Sets key.
     *
     * @param key key
     * @return this
     */
    public CipherCodec key(Key key) {
        this.key = key;
        return this;
    }

    @Override
    public long doFinal() {
        try {
            Cipher cipher = this.cipher.get();
            initCipher(cipher);
            if (input instanceof ByteBuffer) {
                if (output instanceof ByteBuffer) {
                    return GekCodec.doCipher(cipher, (ByteBuffer) input, (ByteBuffer) output, blockSize);
                } else if (output instanceof OutputStream) {
                    return GekCodec.doCipher(cipher, (ByteBuffer) input, (OutputStream) output, blockSize);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                if (output instanceof ByteBuffer) {
                    return GekCodec.doCipher(cipher, (InputStream) input, (ByteBuffer) output, blockSize);
                } else if (output instanceof OutputStream) {
                    return GekCodec.doCipher(cipher, (InputStream) input, (OutputStream) output, blockSize);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    @Override
    public InputStream finalStream() {
        InputStream source;
        if (input instanceof ByteBuffer) {
            source = GekIO.toInputStream((ByteBuffer) input);
        } else if (input instanceof InputStream) {
            source = (InputStream) input;
        } else {
            throw new CodecException("Unknown input type: " + input.getClass());
        }
        GekRef<Cipher> cipherRef = GekRef.ofNull();
        return GekIO.transform(source, blockSize, bytes -> {
            Cipher c = cipherRef.get();
            if (c == null) {
                c = cipher.get();
                try {
                    initCipher(c);
                } catch (Exception e) {
                    throw new GekCodecException(e);
                }
                cipherRef.set(c);
            }
            return GekCodec.doCipher(c, ByteBuffer.wrap(bytes));
        });
    }

    private void initCipher(Cipher cipher) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom != null) {
            if (certificate != null) {
                cipher.init(mode, certificate, secureRandom);
            } else if (algorithmParameters != null) {
                cipher.init(mode, key, algorithmParameters, secureRandom);
            } else if (algorithmParameterSpec != null) {
                cipher.init(mode, key, algorithmParameterSpec, secureRandom);
            } else {
                cipher.init(mode, key, secureRandom);
            }
        } else {
            if (certificate != null) {
                cipher.init(mode, certificate);
            } else if (algorithmParameters != null) {
                cipher.init(mode, key, algorithmParameters);
            } else if (algorithmParameterSpec != null) {
                cipher.init(mode, key, algorithmParameterSpec);
            } else {
                cipher.init(mode, key);
            }
        }
    }
}
