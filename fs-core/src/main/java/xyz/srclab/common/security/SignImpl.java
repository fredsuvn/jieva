package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

final class SignImpl implements FsSign {

    private final String algorithm;
    private final ThreadLocal<Signature> local;

    SignImpl(String algorithm, Supplier<Signature> supplier) {
        this.algorithm = algorithm;
        this.local = ThreadLocal.withInitial(supplier);
    }

    @Override
    public @Nullable Signature getSignature() {
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
                ByteBuffer src = ByteBuffer.wrap(source, this.offset, this.length);
                Signature signature = local.get();
                return FsCrypto.sign(signature, key, src, params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public boolean verify(byte[] sign, int offset, int length) {
            ByteBuffer src = ByteBuffer.wrap(source, this.offset, this.length);
            Signature signature = local.get();
            return FsCrypto.verify(signature, key, src, sign, offset, length, params);
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
                Signature signature = local.get();
                return FsCrypto.sign(signature, key, source, params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public boolean verify(byte[] sign, int offset, int length) {
            Signature signature = local.get();
            return FsCrypto.verify(signature, key, source, sign, offset, length, params);
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
                Signature signature = local.get();
                return FsCrypto.sign(signature, key, in, bufferSize, params);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public boolean verify(byte[] sign, int offset, int length) {
            Signature signature = local.get();
            return FsCrypto.verify(signature, key, in, bufferSize, sign, offset, length, params);
        }
    }

    private abstract class AbstractCryptoProcess implements CryptoProcess {

        protected AlgorithmParams params;
        protected Key key;
        protected int bufferSize;

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
            return this;
        }

        @Override
        public CryptoProcess bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        @Override
        public CryptoProcess sign() {
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

        @Override
        public int doFinal(byte[] dest, int offset) {
            try {
                byte[] en = doFinal();
                if (dest.length - offset < en.length) {
                    throw new FsSecurityException("length of dest remaining is not enough.");
                }
                System.arraycopy(en, 0, dest, offset, en.length);
                return en.length;
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public int doFinal(ByteBuffer dest) {
            try {
                byte[] en = doFinal();
                if (dest.remaining() < en.length) {
                    throw new FsSecurityException("length of dest remaining is not enough.");
                }
                dest.put(en);
                return en.length;
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public long doFinal(OutputStream dest) {
            try {
                byte[] en = doFinal();
                dest.write(en);
                return en.length;
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }

        @Override
        public InputStream doFinalStream() {
            try {
                byte[] en = doFinal();
                return FsIO.toInputStream(en);
            } catch (FsSecurityException e) {
                throw e;
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        }
    }
}
