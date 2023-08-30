package xyz.srclab.common.codec;

import lombok.Getter;

/**
 * Common supported algorithm info for codec, just commonly used, not all.
 */
@Getter
public enum FsAlgorithm {

    /**
     * Base64.
     */
    BASE64("Base64"),
    /**
     * Base64.
     */
    BASE64_NO_PADDING("Base64/NoPadding"),
    /**
     * Base64-URL.
     */
    BASE64_URL("Base64/URL"),
    /**
     * Base64-URL without padding.
     */
    BASE64_URL_NO_PADDING("Base64/URL/NoPadding"),
    /**
     * Base64-MIME.
     */
    BASE64_MIME("Base64/MIME"),
    /**
     * Base64-MIME without padding.
     */
    BASE64_MIME_NO_PADDING("Base64/MIME/NoPadding"),
    /**
     * Hex.
     */
    HEX("Hex"),
    ;

    /**
     * Algorithm name.
     */
    private final String name;

    FsAlgorithm(String name) {
        this.name = name;
    }

}
