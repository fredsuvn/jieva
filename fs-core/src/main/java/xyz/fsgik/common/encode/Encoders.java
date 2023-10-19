package xyz.fsgik.common.encode;

import java.util.Base64;

final class Encoders {

    static FsEncoder BASE64 = new Base64Encoder(Base64.getEncoder(), Base64.getDecoder());
    static FsEncoder BASE64_NO_PADDING = new Base64Encoder(Base64.getEncoder().withoutPadding(), Base64.getDecoder());
    static FsEncoder BASE64_URL = new Base64Encoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
    static FsEncoder BASE64_URL_NO_PADDING = new Base64Encoder(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
    static FsEncoder BASE64_MIME = new Base64Encoder(Base64.getMimeEncoder(), Base64.getMimeDecoder());
    static FsEncoder BASE64_MIME_NO_PADDING = new Base64Encoder(Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
    static FsEncoder HEX = new HexEncoder();
}
