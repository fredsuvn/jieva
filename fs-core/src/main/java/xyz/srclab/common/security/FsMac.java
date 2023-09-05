package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.Mac;

/**
 * Denotes cipher for encrypting/decrypting, maybe has a back {@link Mac}.
 *
 * @author fredsuvn
 * @see Cipher
 */
public interface FsMac extends Prepareable{

    /**
     * Returns back {@link Mac} if it has, or null if it doesn't have one.
     * The back {@link Mac} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    Mac getMac();
}
