# <span class="image">![Boat Codec](../../logo.svg)</span> `boat-codec`: Boat Codec — Codec Lib of [Boat](../../README.md)

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

Table of Contents

-   [Introduction](#_introduction)
-   [Usage](#_usage)
    -   [Static Style:](#_static_style)
    -   [Chain Style:](#_chain_style)
-   [Samples](#_samples)

## Introduction

Boat Codec provides Unified interfaces (`Codec`, `Codecing`) to support
various codec operations sucu as HEX, BASE64, MD5, HmacMD5, AES, RSA,
SM2 ,etc.

## Usage

Core utilities of boat-codec is `Codecs`:

Java Examples

    class Example{
        @Test
        public void test() {
            RsaCodec rsaCodec = Codecs.rsaCodec();
            RsaKeyPair rsaKeyPair = rsaCodec.newKeyPair();
            String data = random(512);
            byte[] bytes = rsaCodec.encrypt(rsaKeyPair.publicKey(), data);
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val rsaCodec = rsaCodec()
            val rsaKeyPair = rsaCodec.newKeyPair()
            val data: String = random(512)
            val bytes = rsaCodec.encrypt(rsaKeyPair.publicKey, data)
        }
    }

Boat Codec has 4 type of codec processor:

-   `Codec`: Core codec interface, represents a type of codec way;

-   `EncodeCodec`: Encode code such as HEX, BASE64;

-   `DigestCodec`: Digest codec such as MD5;

-   `MacCodec`: MAC codec such as HmacMD5;

-   `CipherCodec`: Cipher codec such as `AES`, `RSA`, `SM2`;

There are two styles to do codec operation: `Static` or `Chain`

### Static Style:

Java Examples

    class Example{
        @Test
        public void test() {
            RsaCodec rsaCodec = Codecs.rsaCodec();
            byte[] bytes = rsaCodec.encrypt(rsaKeyPair.publicKey(), data);
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            val rsaCodec = rsaCodec()
            val bytes = rsaCodec.encrypt(rsaKeyPair.publicKey, data)
        }
    }

### Chain Style:

Using `Codecing`: Codec processing for chain operation.

Java Examples

    class Example{
        @Test
        public void test() {
            Codecing.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            messageBase64.startCodec().decodeBase64().encryptAes(secretKey).doFinal()
        }
    }

## Samples

Java Examples

    package sample.java.xyz.srclab.codec;

    import org.testng.annotations.Test;
    import xyz.srclab.common.codec.Codecing;
    import xyz.srclab.common.codec.Codecs;
    import xyz.srclab.common.codec.EncodeCodec;
    import xyz.srclab.common.codec.aes.AesKeys;
    import xyz.srclab.common.test.TestLogger;

    import javax.crypto.SecretKey;

    public class CodecSample {

        private static final TestLogger logger = TestLogger.DEFAULT;

        @Test
        public void testCodec() {
            String password = "hei, xiongdi, womenhaojiubujiannizainali";
            String messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1";
            SecretKey secretKey = AesKeys.newKey(password);

            //Use static
            String message = EncodeCodec.base64().decodeToString(messageBase64);
            byte[] encrypt = Codecs.aesCodec().encrypt(secretKey, message);
            String decrypt = Codecs.aesCodec().decryptToString(secretKey, encrypt);
            //hei, pengyou, ruguozhendeshiniqingdazhaohu
            logger.log("decrypt: {}", decrypt);

            //Use chain
            encrypt = Codecing.forData(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
            decrypt = Codecing.forData(encrypt).decryptAes(secretKey).doFinalString();
            //hei, pengyou, ruguozhendeshiniqingdazhaohu
            logger.log("decrypt: {}", decrypt);
        }

        @Test
        public void testEncode() {
            logger.log(Codecs.hexString("123456789"));
            logger.log(Codecs.base64String("123456789"));
        }
    }

Kotlin Examples

    package sample.kotlin.xyz.srclab.codec

    import org.testng.annotations.Test
    import xyz.srclab.common.codec.Base64Codec
    import xyz.srclab.common.codec.Codecing.Companion.codec
    import xyz.srclab.common.codec.aes.toAesKey
    import xyz.srclab.common.codec.aesCodec
    import xyz.srclab.common.codec.toBase64String
    import xyz.srclab.common.codec.toHexString
    import xyz.srclab.common.test.TestLogger

    class CodecSample {

        @Test
        fun testCodec() {
            val password = "hei, xiongdi, womenhaojiubujiannizainali"
            val messageBase64 = "aGVpLCBwZW5neW91LCBydWd1b3poZW5kZXNoaW5pcWluZ2Rhemhhb2h1"
            val secretKey = password.toAesKey()

            //Use static
            val message: String = Base64Codec.decodeToString(messageBase64)
            var encrypt = aesCodec().encrypt(secretKey, message)
            var decrypt = aesCodec().decryptToString(secretKey, encrypt)
            //hei, pengyou, ruguozhendeshiniqingdazhaohu
            logger.log("decrypt: {}", decrypt)

            //Use chain
            encrypt = messageBase64.codec().decodeBase64().encryptAes(secretKey).doFinal()
            decrypt = encrypt.codec().decryptAes(secretKey).doFinalString()
            //hei, pengyou, ruguozhendeshiniqingdazhaohu
            logger.log("decrypt: {}", decrypt)
        }

        @Test
        fun testEncode() {
            logger.log("123456789".toHexString())
            logger.log("123456789".toBase64String())
        }

        companion object {
            private val logger = TestLogger.DEFAULT
        }
    }
