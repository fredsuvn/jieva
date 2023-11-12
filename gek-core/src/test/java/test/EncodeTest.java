package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.codec.GekCodec;
import xyz.fsgek.common.data.GekDataProcess;
import xyz.fsgek.common.encode.GekEncodeException;
import xyz.fsgek.common.encode.GekEncoder;
import xyz.fsgek.common.io.GekIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncodeTest {

    @Test
    public void testEncoder() {
        testEncoder(GekEncoder.base64(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.base64NoPadding(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.base64Url(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.base64UrlNoPadding(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.base64Mime(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.base64MimeNoPadding(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH");
        testEncoder(GekEncoder.hex(), "123456中文中文", "313233343536E4B8ADE69687E4B8ADE69687");
    }

    private void testEncoder(
        GekEncoder encoder, String source, String dest) {
        byte[] srcBytes = source.getBytes(GekChars.defaultCharset());
        byte[] destBytes = dest.getBytes(StandardCharsets.ISO_8859_1);
        byte[] srcBytesPadding = padBytes(srcBytes, 10);
        byte[] destBytesPadding = padBytes(destBytes, 10);
        byte[] bytes = new byte[1024];

        //--------------encode

        Assert.assertEquals(encoder.encode(srcBytes), destBytes);
        Assert.assertEquals(encoder.encode(srcBytes, 0, srcBytes.length), destBytes);
        Assert.assertEquals(encoder.encode(srcBytesPadding, 10, srcBytes.length), destBytes);
        Assert.expectThrows(GekEncodeException.class, () -> encoder.encode(srcBytes, 0, 9999999));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytes, bytes),
            Gek.countBlock(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);
        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytes, 0, bytes, 0, srcBytes.length),
            Gek.countBlock(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.encode(srcBytesPadding, 10, bytes, 10, srcBytes.length),
            Gek.countBlock(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOfRange(bytes, 10, 10 + destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.expectThrows(GekEncodeException.class, () ->
            encoder.encode(srcBytesPadding, 999999, bytes, 10, srcBytes.length));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(encoder.encode(ByteBuffer.wrap(srcBytes)), ByteBuffer.wrap(destBytes));
        Assert.assertEquals(
            encoder.encode(ByteBuffer.wrap(srcBytes), ByteBuffer.wrap(bytes)),
            Gek.countBlock(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, destBytes.length), destBytes);

        Arrays.fill(bytes, (byte) 0);
        ByteArrayOutputStream destOut = new ByteArrayOutputStream();
        Assert.assertEquals(
            encoder.encode(new ByteArrayInputStream(srcBytes), destOut),
            (long) Gek.countBlock(srcBytes.length, encoder.encodeBlockSize()) * encoder.decodeBlockSize()
        );
        Assert.assertEquals(destOut.toByteArray(), destBytes);

        Assert.assertEquals(encoder.encodeToString(source), dest);
        Assert.assertEquals(encoder.encodeToString(srcBytes), dest);

        //--------------decode

        Assert.assertEquals(encoder.decode(destBytes), srcBytes);
        Assert.assertEquals(encoder.decode(destBytes, 0, destBytes.length), srcBytes);
        Assert.assertEquals(encoder.decode(destBytesPadding, 10, destBytes.length), srcBytes);
        Assert.expectThrows(GekEncodeException.class, () -> encoder.decode(destBytes, 0, 9999999));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytes, bytes),
            Gek.countBlock(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);
        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytes, 0, bytes, 0, destBytes.length),
            Gek.countBlock(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(
            encoder.decode(destBytesPadding, 10, bytes, 10, destBytes.length),
            Gek.countBlock(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOfRange(bytes, 10, 10 + srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        Assert.expectThrows(GekEncodeException.class, () ->
            encoder.decode(destBytesPadding, 999999, bytes, 10, destBytes.length));

        Arrays.fill(bytes, (byte) 0);
        Assert.assertEquals(encoder.decode(ByteBuffer.wrap(destBytes)), ByteBuffer.wrap(srcBytes));
        Assert.assertEquals(
            encoder.decode(ByteBuffer.wrap(destBytes), ByteBuffer.wrap(bytes)),
            Gek.countBlock(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
        );
        Assert.assertEquals(Arrays.copyOf(bytes, srcBytes.length), srcBytes);

        Arrays.fill(bytes, (byte) 0);
        destOut.reset();
        Assert.assertEquals(
            encoder.decode(new ByteArrayInputStream(destBytes), destOut),
            (long) Gek.countBlock(destBytes.length, encoder.decodeBlockSize()) * encoder.encodeBlockSize()
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

    @Test
    public void testEncodeCodec() {
        testEncodeCodec(GekCodec.base64(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH", true);
        testEncodeCodec(GekCodec.base64().decode(), "MTIzNDU25Lit5paH5Lit5paH", "123456中文中文", false);
    }

    private void testEncodeCodec(
        GekDataProcess<?> codec, String source, String dest, boolean encode) {
        byte[] srcBytes = encode ? GekString.encode(source) : GekString.encode(source, StandardCharsets.ISO_8859_1);
        byte[] destBytes = encode ? GekString.encode(dest, StandardCharsets.ISO_8859_1) : GekString.encode(dest);
        byte[] srcBytesPadding = padBytes(srcBytes, 10);
        byte[] destBytesPadding = padBytes(destBytes, 10);

        byte[] finalBytes = new byte[1024];
        ByteBuffer finalBuffer = ByteBuffer.wrap(new byte[1024]);
        ByteBuffer simpleBuffer;

        //simple byte[]
        int size = codec.input(srcBytes).output(finalBytes).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(srcBytes).output(destBytesPadding, 10).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(srcBytes).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(srcBytes).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(srcBytes).output(GekIO.toOutputStream(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        //byte[]
        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(finalBytes).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(destBytesPadding, 10).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(srcBytesPadding, 10, srcBytes.length).output(GekIO.toOutputStream(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        //simple buffer
        size = codec.input(ByteBuffer.wrap(srcBytes)).output(finalBytes).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytes)).output(destBytesPadding, 10).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytes)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytes)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytes)).output(GekIO.toOutputStream(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        //buffer
        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(finalBytes).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(destBytesPadding, 10).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(ByteBuffer.wrap(srcBytesPadding, 10, srcBytes.length)).output(GekIO.toOutputStream(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        //stream
        size = codec.input(GekIO.toInputStream(srcBytes)).output(finalBytes).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(GekIO.toInputStream(srcBytes)).output(destBytesPadding, 10).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(GekIO.toInputStream(srcBytes)).output(ByteBuffer.wrap(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        size = codec.input(GekIO.toInputStream(srcBytes)).output(ByteBuffer.wrap(destBytesPadding, 10, destBytesPadding.length - 10)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(destBytesPadding, 10, 10 + size),
            destBytes
        );
        size = codec.input(GekIO.toInputStream(srcBytes)).output(GekIO.toOutputStream(finalBytes)).doFinalInt();
        Assert.assertEquals(
            Arrays.copyOfRange(finalBytes, 0, size),
            destBytes
        );
        //final
        Assert.assertEquals(
            codec.input(srcBytes).finalBytes(),
            destBytes
        );
        Assert.assertEquals(
            GekIO.read(codec.input(srcBytes).finalStream()),
            destBytes
        );
        Assert.assertEquals(
            codec.input(srcBytes).finalString(),
            dest
        );
        if (encode) {
            Assert.assertEquals(
                codec.input(srcBytes).finalLatinString(),
                dest
            );
        }
    }
}
