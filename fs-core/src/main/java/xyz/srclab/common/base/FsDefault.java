package xyz.srclab.common.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Default configuration for fs.
 *
 * @author fredsuvn
 */
public class FsDefault {

    /**
     * Default charset: {@link StandardCharsets#UTF_8}.
     */
    public static Charset charset() {
        return StandardCharsets.UTF_8;
    }
}
