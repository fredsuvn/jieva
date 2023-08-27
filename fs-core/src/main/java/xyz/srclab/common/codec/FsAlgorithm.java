package xyz.srclab.common.codec;

import lombok.Getter;

/**
 * Algorithm info for codec.
 */
@Getter
public enum FsAlgorithm {
    BASE64("base64"),
    BASE64_NO_PADDING("base64-no-padding"),
    BASE64_URL("base64-url"),
    BASE64_URL_NO_PADDING("base64-url-no-padding"),
    BASE64_MIME("base64-mime"),
    BASE64_MIME_NO_PADDING("base64-mime-no-padding"),
    HEX("hex"),
    ;

    private final String name;

    FsAlgorithm(String name) {
        this.name = name;
    }
}
