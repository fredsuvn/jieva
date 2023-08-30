package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.codec.FsCodecException;
import xyz.srclab.common.codec.FsEncoder;
import xyz.srclab.common.codec.bouncycastle.BouncyCastleCodecProvider;
import xyz.srclab.common.codec.jdk.JdkCodecProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CodecTest {

    @Test
    public void testEncoder() {
        testEncoder(FsEncoder.base64(JdkCodecProvider.INSTANCE), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(FsEncoder.base64(BouncyCastleCodecProvider.INSTANCE), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(FsEncoder.base64(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(FsEncoder.hex(JdkCodecProvider.INSTANCE), "123456中文中文", "313233343536E4B8ADE69687E4B8ADE69687");
        testEncoder(FsEncoder.hex(BouncyCastleCodecProvider.INSTANCE), "123456中文中文", "313233343536E4B8ADE69687E4B8ADE69687");
        testEncoder(FsEncoder.hex(), "123456中文中文", "313233343536E4B8ADE69687E4B8ADE69687");
    }

    private void testEncoder(
        FsEncoder encoder, String source, String dest) {
        byte[] srcBytes = source.getBytes(FsString.CHARSET);
        byte[] destBytes = dest.getBytes(StandardCharsets.ISO_8859_1);
        byte[] srcBytesPadding = padBytes(srcBytes, 10);
        byte[] destBytesPadding = padBytes(destBytes, 10);
        byte[] bytes = new byte[1024];

        //--------------encode

        Assert.assertEquals(encoder.encode(srcBytes), destBytes);
        Assert.assertEquals(encoder.encode(srcBytes, 0, srcBytes.length), destBytes);
        Assert.assertEquals(encoder.encode(srcBytesPadding, 10, srcBytes.length), destBytes);
        Assert.expectThrows(FsCodecException.class, () -> encoder.encode(srcBytes, 0, 9999999));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytes, bytes),
            Fs.chunkCount(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);
        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytes, 0, bytes, 0, srcBytes.length),
            Fs.chunkCount(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytesPadding, 10, bytes, 10, srcBytes.length),
            Fs.chunkCount(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOfRange(bytes, 10, 10 + destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.expectThrows(FsCodecException.class, () ->
            encoder.encode(srcBytesPadding, 999999, bytes, 10, srcBytes.length));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(encoder.encode(ByteBuffer.wrap(srcBytes)), ByteBuffer.wrap(destBytes));
        Assert.assertEquals(
            encoder.encode(ByteBuffer.wrap(srcBytes), ByteBuffer.wrap(bytes)),
            Fs.chunkCount(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        ByteArrayOutputStream destOut = new ByteArrayOutputStream();
        Assert.assertEquals(
            encoder.encode(new ByteArrayInputStream(srcBytes), destOut),
            (long) Fs.chunkCount(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(destOut.toByteArray(), destBytes);

        Assert.assertEquals(encoder.encodeToString(source), dest);
        Assert.assertEquals(encoder.encodeToString(srcBytes), dest);

        //--------------decode

        Assert.assertEquals(encoder.decode(destBytes), srcBytes);
        Assert.assertEquals(encoder.decode(destBytes, 0, destBytes.length), srcBytes);
        Assert.assertEquals(encoder.decode(destBytesPadding, 10, destBytes.length), srcBytes);
        Assert.expectThrows(FsCodecException.class, () -> encoder.decode(destBytes, 0, 9999999));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytes, bytes),
            Fs.chunkCount(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);
        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytes, 0, bytes, 0, destBytes.length),
            Fs.chunkCount(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytesPadding, 10, bytes, 10, destBytes.length),
            Fs.chunkCount(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOfRange(bytes, 10, 10 + srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.expectThrows(FsCodecException.class, () ->
            encoder.decode(destBytesPadding, 999999, bytes, 10, destBytes.length));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(encoder.decode(ByteBuffer.wrap(destBytes)), ByteBuffer.wrap(srcBytes));
        Assert.assertEquals(
            encoder.decode(ByteBuffer.wrap(destBytes), ByteBuffer.wrap(bytes)),
            Fs.chunkCount(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        destOut.reset();
        Assert.assertEquals(
            encoder.decode(new ByteArrayInputStream(destBytes), destOut),
            (long) Fs.chunkCount(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(destOut.toByteArray(), srcBytes);

        Assert.assertEquals(encoder.decode(dest), source);
        Assert.assertEquals(encoder.decodeToString(destBytes), source);
    }

    private byte[] padBytes(byte[] src, int paddingSize) {
        byte[] result = new byte[src.length + paddingSize * 2];
        System.arraycopy(src, 0, result, paddingSize, src.length);
        return result;
    }
}
