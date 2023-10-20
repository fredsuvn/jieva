package xyz.fsgek.common.security;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.io.FsIO;
import xyz.fsgek.common.base.FsArray;
import xyz.fsgek.common.base.FsCheck;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

final class CipherImpl implements FsCipher {

    private final String algorithm;
    private final ThreadLocal<Cipher> local;

    CipherImpl(String algorithm, Supplier<Cipher> supplier) {
        this.algorithm = algorithm;
        this.local = ThreadLocal.withInitial(supplier);
    }

    @Override
    public @Nullable Cipher getCipher() {
        return local.get();
    }

    @Override
    public CryptoProcess prepare(byte[] source, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, source.length);
        return new ByteArrayCryptoProcess(source, offset, length);
    }

    @Override
    public CryptoProcess prepare(ByteBuffer source) {
        return new BufferCryptoProcess(source);
    }

    @Override
    public CryptoProcess prepare(InputStream source) {
        return new StreamCryptoProcess(source);
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    private static final class EncryptStream extends InputStream {

        private final Cipher cipher;
        private final AbstractCryptoProcess process;
        private final InputStream in;

        private byte[] buffer;
        private int pos = 0;

        private EncryptStream(Cipher cipher, AbstractCryptoProcess process, InputStream in) {
            this.cipher = cipher;
            this.process = process;
            this.in = in;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            try {
                FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
                if (len == 0) {
                    return 0;
                }
                FsCrypto.initCipher(cipher, process.mode, process.key, process.params);
                int count = 0;
                int bOff = off;
                int bLen = len;
                while (bLen > 0) {
                    if (buffer == null) {
                        byte[] nextIn = FsIO.readBytes(in, process.blockSize);
                        if (FsArray.isEmpty(nextIn)) {
                            if (count == 0) {
                                return -1;
                            } else {
                                return count;
                            }
                        }
                        buffer = cipher.doFinal(nextIn);
                        pos = 0;
                    }
                    int inRemaining = buffer.length - pos;
                    if (inRemaining == bLen) {
                        System.arraycopy(buffer, pos, b, bOff, bLen);
                        count += bLen;
                        return count;
                    }
                    if (inRemaining < bLen) {
                        System.arraycopy(buffer, pos, b, bOff, inRemaining);
                        count += inRemaining;
                        bLen -= inRemaining;
                        bOff += inRemaining;
                        buffer = null;
                        continue;
                    }
                    // inRemaining > bLen
                    System.arraycopy(buffer, pos, b, bOff, bLen);
                    count += bLen;
                    pos += bLen;
                    return count;
                }
                return count;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read() throws IOException {
            byte[] b = new byte[1];
            int r = read(b);
            return r == -1 ? -1 : b[0];
        }
    }

    private abstract static class AbstractCryptoProcess implements CryptoProcess {

        protected AlgorithmParams params;
        protected Key key;
        protected int blockSize;
        protected int mode;

        @Override
        public CryptoProcess key(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public CryptoProcess algorithmParameterSpec(AlgorithmParameterSpec parameterSpec) {
            getParams().setAlgorithmParameterSpec(parameterSpec);
            return this;
        }

        @Override
        public CryptoProcess algorithmParameters(AlgorithmParameters parameters) {
            getParams().setAlgorithmParameters(parameters);
            return this;
        }

        @Override
        public CryptoProcess secureRandom(SecureRandom secureRandom) {
            getParams().setSecureRandom(secureRandom);
            return this;
        }

        @Override
        public CryptoProcess certificate(Certificate certificate) {
            getParams().setCertificate(certificate);
            return this;
        }

        @Override
        public CryptoProcess keySize(int keySize) {
            return this;
        }

        @Override
        public CryptoProcess blockSize(int blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        @Override
        public CryptoProcess bufferSize(int bufferSize) {
            return this;
        }

        @Override
        public CryptoProcess encrypt() {
            this.mode = Cipher.ENCRYPT_MODE;
            return this;
        }

        @Override
        public CryptoProcess decrypt() {
            this.mode = Cipher.DECRYPT_MODE;
            return this;
        }

        protected AlgorithmParams getParams() {
            if (params != null) {
                return params;
            }
            AlgorithmParams newParams = new AlgorithmParams();
            params = newParams;
            return newParams;
        }

        protected int getBlockSize(Cipher cipher) {
            if (blockSize > 0) {
                return blockSize;
            }
            return cipher.getBlockSize();
        }
    }

    private final class ByteArrayCryptoProcess extends AbstractCryptoProcess {

        private final byte[] source;
        private final int offset;
        private final int length;

        private ByteArrayCryptoProcess(byte[] source, int offset, int length) {
            this.source = source;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public byte[] doFinal() {
            try {
                InputStream in = FsIO.toInputStream(source, offset, length);
                Cipher cipher = local.get();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FsCrypto.doEncrypt(cipher, mode, key, in, out, getBlockSize(cipher), params);
                return out.toByteArray();
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(byte[] dest, int offset) {
            try {
                ByteBuffer src = ByteBuffer.wrap(source, this.offset, this.length);
                ByteBuffer out = ByteBuffer.wrap(dest, offset, dest.length - offset);
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(cipher, mode, key, src, out, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(ByteBuffer dest) {
            try {
                ByteBuffer src = ByteBuffer.wrap(source, this.offset, this.length);
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(cipher, mode, key, src, dest, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public long doFinal(OutputStream dest) {
            try {
                InputStream in = FsIO.toInputStream(source, offset, length);
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(cipher, mode, key, in, dest, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public InputStream doFinalStream() {
            try {
                Cipher cipher = local.get();
                return new EncryptStream(cipher, this, FsIO.toInputStream(source, offset, length));
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }
    }

    private final class BufferCryptoProcess extends AbstractCryptoProcess {

        private final ByteBuffer source;

        private BufferCryptoProcess(ByteBuffer source) {
            this.source = source;
        }

        @Override
        public byte[] doFinal() {
            try {
                Cipher cipher = local.get();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FsCrypto.doEncrypt(
                    cipher, mode, key, FsIO.toInputStream(source), out, getBlockSize(cipher), params);
                return out.toByteArray();
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(byte[] dest, int offset) {
            try {
                ByteBuffer out = ByteBuffer.wrap(dest, offset, dest.length - offset);
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(
                    cipher, mode, key, source, out, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(ByteBuffer dest) {
            try {
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(
                    cipher, mode, key, source, dest, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public long doFinal(OutputStream dest) {
            try {
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(
                    cipher, mode, key, FsIO.toInputStream(source), dest, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public InputStream doFinalStream() {
            try {
                Cipher cipher = local.get();
                return new EncryptStream(cipher, this, FsIO.toInputStream(source));
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }
    }

    private final class StreamCryptoProcess extends AbstractCryptoProcess {

        private final InputStream in;

        private StreamCryptoProcess(InputStream in) {
            this.in = in;
        }

        @Override
        public byte[] doFinal() {
            try {
                Cipher cipher = local.get();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FsCrypto.doEncrypt(cipher, mode, key, in, out, getBlockSize(cipher), params);
                return out.toByteArray();
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(byte[] dest, int offset) {
            try {
                OutputStream out = FsIO.toOutputStream(dest, offset, dest.length - offset);
                Cipher cipher = local.get();
                return (int) FsCrypto.doEncrypt(cipher, mode, key, in, out, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(ByteBuffer dest) {
            try {
                OutputStream out = FsIO.toOutputStream(dest);
                Cipher cipher = local.get();
                return (int) FsCrypto.doEncrypt(cipher, mode, key, in, out, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public long doFinal(OutputStream dest) {
            try {
                Cipher cipher = local.get();
                return FsCrypto.doEncrypt(cipher, mode, key, in, dest, getBlockSize(cipher), params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public InputStream doFinalStream() {
            try {
                Cipher cipher = local.get();
                return new EncryptStream(cipher, this, in);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }
    }
}
