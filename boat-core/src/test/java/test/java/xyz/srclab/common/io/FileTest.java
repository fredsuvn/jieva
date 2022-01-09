package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.io.BFile;
import xyz.srclab.common.io.BIO;

import java.net.URL;
import java.nio.MappedByteBuffer;

public class FileTest {

    @Test
    public void testMappedByteBuffer() {
        URL url = BResource.load("META-INF/test.mapped.txt");
        MappedByteBuffer buffer = BFile.mappedByteBuffer(BFile.toPath(url));
        String content = BIO.readString(BIO.toInputStream(buffer));
        buffer.flip();
        buffer.put("6666666666666666".getBytes());
        Assert.assertEquals(BIO.readString(BIO.toInputStream(buffer)), "6666666666666666");
        buffer.flip();
        buffer.put(content.getBytes());
        Assert.assertEquals(BIO.readString(BIO.toInputStream(buffer)), content);
    }
}
