package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.function.Supplier;

final class KeyGenImpl implements FsKeyGen {

    private final ThreadLocal<KeyGenerator> local;

    KeyGenImpl(Supplier<KeyGenerator> supplier) {
        this.local = ThreadLocal.withInitial(supplier);
    }

    @Override
    public @Nullable KeyGenerator getKeyGenerator() {
        return local.get();
    }

    @Override
    public Key generateKey() {
        try {
            KeyGenerator generator = local.get();
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public Key generateKey(int size) {
        try {
            KeyGenerator generator = local.get();
            generator.init(size);
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public Key generateKey(int size, SecureRandom secureRandom) {
        try {
            KeyGenerator generator = local.get();
            generator.init(size, secureRandom);
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public Key generateKey(AlgorithmParameterSpec spec) {
        try {
            KeyGenerator generator = local.get();
            generator.init(spec);
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public Key generateKey(SecureRandom secureRandom) {
        try {
            KeyGenerator generator = local.get();
            generator.init(secureRandom);
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    @Override
    public Key generateKey(AlgorithmParameterSpec spec, SecureRandom secureRandom) {
        try {
            KeyGenerator generator = local.get();
            generator.init(spec, secureRandom);
            return generator.generateKey();
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }
}
