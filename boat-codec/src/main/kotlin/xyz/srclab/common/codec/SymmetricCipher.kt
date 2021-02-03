package xyz.srclab.common.codec

/**
 * Symmetric cipher.
 *
 * @param <K> secret key
 * @author sunqian
</K> */
interface SymmetricCipher<K> : ReversibleCipher<K, K>