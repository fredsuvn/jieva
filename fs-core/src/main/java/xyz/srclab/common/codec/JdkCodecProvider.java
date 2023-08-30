package xyz.srclab.common.codec;

import java.util.Base64;

/**
 * JDK codec provider.
 *
 * @author fredsuvn
 */
public class JdkCodecProvider implements FsCodecProvider {

    public static final JdkCodecProvider INSTANCE = new JdkCodecProvider();

    private JdkCodecProvider() {
    }

    @Override
    public FsEncoder getEncoder(String algorithmName) {
        String lowerName = algorithmName.toLowerCase();
        switch (lowerName) {
            case "base64":
                return new JdkBase64Encoder(FsAlgorithm.BASE64.getName(), Base64.getEncoder(), Base64.getDecoder());
            case "base64/nopadding":
            case "base64-no-padding":
                return new JdkBase64Encoder(FsAlgorithm.BASE64_NO_PADDING.getName(), Base64.getEncoder().withoutPadding(), Base64.getDecoder());
            case "base64/url":
            case "base64-url":
                return new JdkBase64Encoder(FsAlgorithm.BASE64_URL.getName(), Base64.getUrlEncoder(), Base64.getUrlDecoder());
            case "base64/url/nopadding":
            case "base64-url-no-padding":
                return new JdkBase64Encoder(FsAlgorithm.BASE64_URL_NO_PADDING.getName(), Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
            case "base64/mime":
            case "base64-mime":
                return new JdkBase64Encoder(FsAlgorithm.BASE64_MIME.getName(), Base64.getMimeEncoder(), Base64.getMimeDecoder());
            case "base64/mime/nopadding":
            case "base64-mime-no-padding":
                return new JdkBase64Encoder(FsAlgorithm.BASE64_MIME_NO_PADDING.getName(), Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
            case "hex":
                return new JdkHexEncoder();
        }
        throw new FsCodecException("Unsupported algorithm: " + algorithmName + ".");
    }
}
