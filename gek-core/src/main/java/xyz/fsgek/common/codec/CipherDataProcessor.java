package xyz.fsgek.common.codec;

import org.springframework.core.codec.CodecException;
import xyz.fsgek.common.base.GekArray;
import xyz.fsgek.common.data.GekDataProcessor;
import xyz.fsgek.common.io.GekIO;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * {@link GekDataProcessor} to encrypt/decrypt with {@link Cipher}.
 *
 * @author fredsuvn
 */
public class CipherDataProcessor implements GekDataProcessor<CipherDataProcessor> {

    private final Cipher cipher;
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
     * Constructs with given {@link Cipher}.
     *
     * @param cipher given {@link Cipher}
     */
    public CipherDataProcessor(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public CipherDataProcessor input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public CipherDataProcessor input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public CipherDataProcessor output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public CipherDataProcessor output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets block size.
     *
     * @param blockSize block size
     * @return this
     */
    public CipherDataProcessor blockSize(int blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * Sets secure random.
     *
     * @param secureRandom secure random
     * @return this
     */
    public CipherDataProcessor secureRandom(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        return this;
    }

    /**
     * Sets certificate.
     *
     * @param certificate certificate
     * @return this
     */
    public CipherDataProcessor certificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    /**
     * Sets algorithm parameters.
     *
     * @param algorithmParameters algorithm parameters
     * @return this
     */
    public CipherDataProcessor algorithmParameters(AlgorithmParameters algorithmParameters) {
        this.algorithmParameters = algorithmParameters;
        return this;
    }

    /**
     * Sets algorithm parameter spec.
     *
     * @param algorithmParameterSpec algorithm parameter spec
     * @return this
     */
    public CipherDataProcessor algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#ENCRYPT_MODE}.
     *
     * @return this
     */
    public CipherDataProcessor encrypt() {
        this.mode = Cipher.ENCRYPT_MODE;
        return this;
    }

    /**
     * Sets mode to {@link Cipher#DECRYPT_MODE}.
     *
     * @return this
     */
    public CipherDataProcessor decrypt() {
        this.mode = Cipher.DECRYPT_MODE;
        return this;
    }

    /**
     * Sets key.
     *
     * @param key key
     * @return this
     */
    public CipherDataProcessor key(Key key) {
        this.key = key;
        return this;
    }

    @Override
    public long process() {
        try {
            initCipher();
            if (input instanceof ByteBuffer) {
                if (output instanceof ByteBuffer) {
                    return bufferToBuffer((ByteBuffer) input, (ByteBuffer) output);
                } else if (output instanceof OutputStream) {
                    return bufferToStream((ByteBuffer) input, (OutputStream) output);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                if (output instanceof ByteBuffer) {
                    return streamToBuffer((InputStream) input, (ByteBuffer) output);
                } else if (output instanceof OutputStream) {
                    return streamToStream((InputStream) input, (OutputStream) output);
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

    private void initCipher() throws InvalidKeyException, InvalidAlgorithmParameterException {
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

    private int bufferToBuffer(ByteBuffer in, ByteBuffer out)
        throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (blockSize <= 0) {
            return cipher.doFinal(in, out);
        }
        int remaining = in.remaining();
        int outSize = 0;
        while (remaining > 0) {
            int inSize = Math.min(remaining, blockSize);
            ByteBuffer r = GekIO.slice(in, inSize);
            outSize += cipher.doFinal(r, out);
            remaining -= inSize;
            in.position(in.position() + inSize);
        }
        return outSize;
    }

    private long bufferToStream(ByteBuffer in, OutputStream out)
        throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (blockSize <= 0) {
            byte[] result = doFinal(in);
            out.write(result);
            return result.length;
        }
        int remaining = in.remaining();
        long outSize = 0;
        while (remaining > 0) {
            int inSize = Math.min(remaining, blockSize);
            ByteBuffer r = GekIO.slice(in, inSize);
            byte[] outBytes = doFinal(r);
            out.write(outBytes);
            outSize += outBytes.length;
            remaining -= inSize;
            in.position(in.position() + inSize);
        }
        return outSize;
    }

    private int streamToBuffer(InputStream in, ByteBuffer out)
        throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (blockSize <= 0) {
            byte[] inBytes = GekIO.read(in);
            return cipher.doFinal(ByteBuffer.wrap(inBytes), out);
        }
        int outSize = 0;
        while (true) {
            byte[] inBytes = GekIO.read(in, blockSize);
            if (GekArray.isEmpty(inBytes)) {
                break;
            }
            ByteBuffer r = ByteBuffer.wrap(inBytes);
            outSize += cipher.doFinal(r, out);
            if (inBytes.length < blockSize) {
                break;
            }
        }
        return outSize;
    }

    private long streamToStream(InputStream in, OutputStream out)
        throws IllegalBlockSizeException, BadPaddingException, IOException {
        if (blockSize <= 0) {
            byte[] inBytes = GekIO.read(in);
            byte[] result = cipher.doFinal(inBytes);
            out.write(result);
            return result.length;
        }
        int outSize = 0;
        while (true) {
            byte[] inBytes = GekIO.read(in, blockSize);
            if (GekArray.isEmpty(inBytes)) {
                break;
            }
            byte[] outBytes = cipher.doFinal(inBytes);
            out.write(outBytes);
            outSize += outBytes.length;
            if (inBytes.length < blockSize) {
                break;
            }
        }
        return outSize;
    }

    private byte[] doFinal(ByteBuffer in) throws IllegalBlockSizeException, BadPaddingException {
        if (in.hasArray()) {
            return cipher.doFinal(in.array(), in.arrayOffset() + in.position(), in.remaining());
        }
        byte[] inBytes = GekIO.read(in);
        return cipher.doFinal(inBytes);
    }
}
