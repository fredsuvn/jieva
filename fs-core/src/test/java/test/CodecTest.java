package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.codec.FsCodec;
import xyz.srclab.common.codec.FsEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CodecTest {

    @Test
    public void testBase64() {
        testEncoder(FsCodec.base64(), "123456中文中文", "MTIzNDU25Lit5paH5Lit5paH", 3, 4);
    }

    private void testEncoder(FsEncoder encoder, String source, String dest, int sourceChunkSize, int destChunkSize) {
        byte[] srcBytes = source.getBytes(FsString.CHARSET);
        byte[] destBytes = dest.getBytes(StandardCharsets.ISO_8859_1);

        Assert.assertEquals(encoder.encodeToString(source), dest);

        InputStream in = new ByteArrayInputStream(srcBytes);
        OutputStream out = new ByteArrayOutputStream();
        Assert.assertEquals(encoder.encode(in, out), (long) Fs.chunkCount(srcBytes.length, sourceChunkSize) * destChunkSize);
    }
}
