package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.io.BFile;
import xyz.srclab.common.io.BIO;

import java.net.URL;
import java.nio.MappedByteBuffer;

public class FileTest {

    @Test
    public void testReadMappedByteBuffer() {
        URL url = BResource.loadClasspathResource("META-INF/test.mapped.txt");
        MappedByteBuffer buffer = BFile.readByteBuffer(BFile.toPath(url));
        BLog.info("testReadMappedByteBuffer: {}", BIO.readString(BIO.asInputStream(buffer)));
    }

    @Test
    public void testMappedByteBuffer() {
        URL url = BResource.loadClasspathResource("META-INF/test.mapped.txt");
        MappedByteBuffer buffer = BFile.mappedByteBuffer(BFile.toPath(url));
        String content = BIO.readString(BIO.asInputStream(buffer));
        BLog.info("testMappedByteBuffer: {}", content);
        buffer.flip();
        String newContent = "666777888";
        buffer.put(newContent.getBytes());

        Assert.assertEquals(BIO.readString(BIO.toInputStream(url)), newContent + content.substring(newContent.length()));
        buffer.clear();
        buffer.put(content.getBytes());
        Assert.assertEquals(BIO.readString(BIO.toInputStream(url)), content);
    }
}
