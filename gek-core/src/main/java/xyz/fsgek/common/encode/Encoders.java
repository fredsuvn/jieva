package xyz.fsgek.common.encode;

import java.util.Base64;

final class Encoders {

    static GekEncoder BASE64 = new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
    static GekEncoder BASE64_NO_PADDING = new Base64Encoder(Base64.getEncoder().withoutPadding(), Base64.getDecoder());
    static GekEncoder BASE64_URL = new Base64Encoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    static GekEncoder BASE64_URL_NO_PADDING = new Base64Encoder(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
    static GekEncoder BASE64_MIME = new Base64Encoder(Base64.getMimeEncoder(), Base64.getMimeDecoder());
    static GekEncoder BASE64_MIME_NO_PADDING = new Base64Encoder(Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
    static GekEncoder HEX = new HexEncoder();
}
