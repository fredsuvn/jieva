package xyz.srclab.common.codec

/**
 * Symmetric cipher.
 *
 * @param [K] secret key
 * @author sunqian
 */
interface SymmetricCipher<K> : ReversibleCipher<K, K>