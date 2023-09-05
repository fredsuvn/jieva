package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsArray;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.data.FsData;
import xyz.srclab.common.io.FsIO;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

final class CipherImpl implements FsCipher {

    private final ThreadLocal<Cipher> localCipher;

    CipherImpl(Supplier<Cipher> supplier) {
        this.localCipher = ThreadLocal.withInitial(supplier);
    }

    @Override
    public @Nullable Cipher getCipher() {
        return localCipher.get();
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
        public FsData encrypt(Key key) {
            Cipher cipher = localCipher.get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FsCrypto.encrypt(
                cipher, key, FsIO.toInputStream(source, offset, length), out, getBlockSize(cipher), params);
            return FsData.wrap(out.toByteArray());
        }

        @Override
        public FsData decrypt(Key key) {
            Cipher cipher = localCipher.get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FsCrypto.decrypt(
                cipher, key, FsIO.toInputStream(source, offset, length), out, getBlockSize(cipher), params);
            return FsData.wrap(out.toByteArray());
        }
    }

    private final class BufferCryptoProcess extends AbstractCryptoProcess {

        private final ByteBuffer source;

        private BufferCryptoProcess(ByteBuffer source) {
            this.source = source;
        }

        @Override
        public FsData encrypt(Key key) {
            Cipher cipher = localCipher.get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FsCrypto.encrypt(
                cipher, key, FsIO.toInputStream(source), out, getBlockSize(cipher), params);
            return FsData.wrap(out.toByteArray());
        }

        @Override
        public FsData decrypt(Key key) {
            Cipher cipher = localCipher.get();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            FsCrypto.decrypt(
                cipher, key, FsIO.toInputStream(source), out, getBlockSize(cipher), params);
            return FsData.wrap(out.toByteArray());
        }
    }

    private final class StreamCryptoProcess extends AbstractCryptoProcess {

        private final InputStream in;

        private StreamCryptoProcess(InputStream in) {
            this.in = in;
        }

        @Override
        public FsData encrypt(Key key) {
            Cipher cipher = localCipher.get();
            int blockSize = getBlockSize(cipher);
            if (blockSize <= 0) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FsCrypto.encrypt(cipher, key, in, out, getBlockSize(cipher), params);
                return FsData.wrap(out.toByteArray());
            }
            return FsData.from(new EncryptStream(cipher, key, blockSize, Cipher.ENCRYPT_MODE));
        }

        @Override
        public FsData decrypt(Key key) {
            Cipher cipher = localCipher.get();
            int blockSize = getBlockSize(cipher);
            if (blockSize <= 0) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                FsCrypto.decrypt(cipher, key, in, out, getBlockSize(cipher), params);
                return FsData.wrap(out.toByteArray());
            }
            return FsData.from(new EncryptStream(cipher, key, blockSize, Cipher.DECRYPT_MODE));
        }

        private final class EncryptStream extends InputStream {

            private final Cipher cipher;
            private final Key key;
            private final int blockSize;
            private final int mode;

            private byte[] buffer;
            private int pos = 0;

            private EncryptStream(Cipher cipher, Key key, int blockSize, int mode) {
                this.cipher = cipher;
                this.key = key;
                this.blockSize = blockSize;
                this.mode = mode;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                try {
                    FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
                    if (len == 0) {
                        return 0;
                    }
                    FsCrypto.initCipher(cipher, mode, key, params);
                    int count = 0;
                    int bOff = off;
                    int bLen = len;
                    while (bLen > 0) {
                        if (buffer == null) {
                            byte[] nextIn = FsIO.readBytes(in, blockSize);
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
    }

    private abstract static class AbstractCryptoProcess implements CryptoProcess {

        protected AlgorithmParams params;
        protected int blockSize;

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
}
