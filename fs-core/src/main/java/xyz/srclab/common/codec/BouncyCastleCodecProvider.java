package xyz.srclab.common.codec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * JDK codec provider.
 *
 * @author fredsuvn
 */
public class BouncyCastleCodecProvider implements FsCodecProvider {

    public static final BouncyCastleCodecProvider INSTANCE = new BouncyCastleCodecProvider();

    private BouncyCastleProvider bouncyCastleProvider;

    private BouncyCastleCodecProvider() {
        // bouncyCastleProvider = new BouncyCastleProvider();
    }

    @Override
    public FsEncoder getEncoder(String algorithmName) {
        String lowerName = algorithmName.toLowerCase();
        switch (lowerName) {
            case "base64":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64.getName());
            case "base64/nopadding":
            case "base64-no-padding":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64_NO_PADDING.getName());
            case "base64/url":
            case "base64-url":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64_URL.getName());
            case "base64/url/nopadding":
            case "base64-url-no-padding":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64_URL_NO_PADDING.getName());
            case "base64/mime":
            case "base64-mime":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64_MIME.getName());
            case "base64/mime/nopadding":
            case "base64-mime-no-padding":
                return new BouncyCastleBase64Encoder(FsAlgorithm.BASE64_MIME_NO_PADDING.getName());
            case "hex":
                return new JdkHexEncoder();
        }
        throw new FsCodecException("Unsupported algorithm: " + algorithmName + ".");
    }
}
