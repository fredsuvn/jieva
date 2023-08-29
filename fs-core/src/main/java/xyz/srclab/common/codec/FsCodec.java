package xyz.srclab.common.codec;

import java.util.Base64;

/**
 * Codec utilities.
 *
 * @author fredsuvn
 */
public class FsCodec {

    /**
     * Returns base64 encoder.
     */
    public static FsEncoder base64() {
        return new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
    }

    /**
     * Returns hex encoder.
     */
    public static FsEncoder hex() {
        return new HexEncoder();
    }

    /**
     * Returns encoder with given algorithm name.
     * The supported name come from {@link FsAlgorithm}.
     *
     * @param name given algorithm name
     */
    public static FsEncoder getEncoder(String name) {
        String lowerName = name.toLowerCase();
        switch (lowerName) {
            case "base64":
                return new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
            case "base64-no-padding":
                return new Base64Encoder(Base64.getEncoder().withoutPadding(), Base64.getDecoder());
            case "base64-url":
                return new Base64Encoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
            case "base64-url-no-padding":
                return new Base64Encoder(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
            case "base64-mime":
                return new Base64Encoder(Base64.getMimeEncoder(), Base64.getMimeDecoder());
            case "base64-mime-no-padding":
                return new Base64Encoder(Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
            case "hex":
                return new HexEncoder();
        }
        throw new FsCodecException("Unsupported algorithm: " + name + ".");
    }

    /**
     * Returns encoder with given algorithm.
     *
     * @param algorithm given algorithm
     */
    public static FsEncoder getEncoder(FsAlgorithm algorithm) {
        switch (algorithm) {
            case BASE64:
                return new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
            case BASE64_NO_PADDING:
                return new Base64Encoder(Base64.getEncoder().withoutPadding(), Base64.getDecoder());
            case BASE64_URL:
                return new Base64Encoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
            case BASE64_URL_NO_PADDING:
                return new Base64Encoder(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
            case BASE64_MIME:
                return new Base64Encoder(Base64.getMimeEncoder(), Base64.getMimeDecoder());
            case BASE64_MIME_NO_PADDING:
                return new Base64Encoder(Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
            case HEX:
                return new HexEncoder();
        }
        throw new FsCodecException("Unsupported algorithm: " + algorithm.getName() + ".");
    }

    static FsAlgorithm newAlgorithm(String name) {
        return () -> name;
    }
}
