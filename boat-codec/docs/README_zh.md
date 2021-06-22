# <span class="image">![Boat Codec](../../logo.svg)</span> `boat-codec`: Boat Codec — [Boat](../../README.md) 编码库

<span id="author" class="author">Sun Qian</span>  
<span id="email" class="email"><fredsuvn@163.com></span>  

目录

-   [简介](#_简介)
-   [用法](#_用法)
    -   [静态调用:](#_静态调用)
    -   [链式调用:](#_链式调用)
-   [样例](#_样例)

## 简介

Boat Codec 提供了统一的接口 (`Codec`, `Codecing`) 来支持大量的编码操作,
如 HEX, BASE64, MD5, HmacMD5, AES, RSA, SM2 ,etc.

## 用法

boat-codec 的核心工具类和接口是 `Codecs` 和 `Codecing`:

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

Boat Codec 有四种编码器:

-   `Codec`: 核心编码器接口;

-   `EncodeCodec`: 普通编码器如 HEX, BASE64;

-   `DigestCodec`: 摘要算法编码器如 MD5;

-   `MacCodec`: 加密摘要编码器如 HmacMD5;

-   `CipherCodec`: 加密算法编码器如 `AES`, `RSA`, `SM2`;

有两种编码风格: `静态调用风格` 或者 `链式调用风格`

### 静态调用:

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

### 链式调用:

Using `Codecing`: Codec processing for chain operation.

Java Examples

    class Example{
        @Test
        public void test() {
            Codecs.collect(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
        }
    }

Kotlin Examples

    class Example {
        @Test
        fun test() {
            messageBase64.codec().decodeBase64().encryptAes(secretKey).doFinal()
        }
    }

## 样例

Java Examples

    package sample.java.xyz.srclab.codec;

    import org.testng.annotations.Test;
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
            encrypt = Codecs.codec(messageBase64).decodeBase64().encryptAes(secretKey).doFinal();
            decrypt = Codecs.codec(encrypt).decryptAes(secretKey).doFinalString();
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
    import xyz.srclab.common.codec.*
    import xyz.srclab.common.codec.aes.toAesKey
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
