package xyz.srclab.common.codec;

/**
 * Algorithm info for codec.
 */
public interface FsAlgorithm {

    /**
     * Base64.
     */
    FsAlgorithm BASE64 = FsCodec.newAlgorithm("Base64");
    /**
     * Base64.
     */
    FsAlgorithm BASE64_NO_PADDING = FsCodec.newAlgorithm("Base64/NoPadding");
    /**
     * Base64-URL.
     */
    FsAlgorithm BASE64_URL = FsCodec.newAlgorithm("Base64/URL");
    /**
     * Base64-URL without padding.
     */
    FsAlgorithm BASE64_URL_NO_PADDING = FsCodec.newAlgorithm("Base64/URL/NoPadding");
    /**
     * Base64-MIME.
     */
    FsAlgorithm BASE64_MIME = FsCodec.newAlgorithm("Base64/MIME");
    /**
     * Base64-MIME without padding.
     */
    FsAlgorithm BASE64_MIME_NO_PADDING = FsCodec.newAlgorithm("Base64/MIME/NoPadding");
    /**
     * Hex.
     */
    FsAlgorithm HEX = FsCodec.newAlgorithm("Hex");

    /**
     * Returns algorithm name.
     */
    String getName();
}
