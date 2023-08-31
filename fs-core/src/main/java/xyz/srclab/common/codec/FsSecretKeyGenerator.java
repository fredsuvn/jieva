package xyz.srclab.common.codec;

import xyz.srclab.common.io.FsIO;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Secret key interface, commonly used for {@link Key}, {@link PublicKey} and {@link PrivateKey}.
 *
 * @author fredsuvn
 */
public interface FsSecretKeyGenerator {



    PublicKey sss();

    PrivateKey ssss();
}
