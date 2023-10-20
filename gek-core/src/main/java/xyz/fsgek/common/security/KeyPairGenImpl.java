package xyz.fsgek.common.security;

import xyz.fsgek.annotations.Nullable;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

final class KeyPairGenImpl implements FsKeyPairGen {

    private final String algorithm;
    private final ThreadLocal<KeyPairGenerator> local;

    KeyPairGenImpl(String algorithm, Supplier<KeyPairGenerator> supplier) {
        this.algorithm = algorithm;
        this.local = ThreadLocal.withInitial(supplier);
    }

    @Override
    public @Nullable KeyPairGenerator getKeyPairGenerator() {
        return local.get();
    }

    @Override
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = local.get();
            return generator.generateKeyPair();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair(int size) {
        try {
            KeyPairGenerator generator = local.get();
            generator.initialize(size);
            return generator.generateKeyPair();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair(int size, SecureRandom secureRandom) {
        try {
            KeyPairGenerator generator = local.get();
            generator.initialize(size, secureRandom);
            return generator.generateKeyPair();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair(AlgorithmParameterSpec spec) {
        try {
            KeyPairGenerator generator = local.get();
            generator.initialize(spec);
            return generator.generateKeyPair();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public KeyPair generateKeyPair(AlgorithmParameterSpec spec, SecureRandom secureRandom) {
        try {
            KeyPairGenerator generator = local.get();
            generator.initialize(spec, secureRandom);
            return generator.generateKeyPair();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }
}
