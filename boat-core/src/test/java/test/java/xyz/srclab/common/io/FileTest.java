package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.io.BFile;
import xyz.srclab.common.io.BIO;

import java.io.File;
import java.net.URL;

public class FileTest {

    @Test
    public void testFile() {
        URL url = BResource.loadClasspathResource("META-INF/test.mapped.txt");
        assert url != null;
        File file = BFile.toFile(url);
        String content1 = BIO.readString(BFile.openReader(file, BDefault.charset()), true);
        BLog.info("File content1: {}", content1);
        String content2 = BIO.readString(BIO.asInputStream(BFile.openRandomAccessFile(file)), true);
        BLog.info("File content2: {}", content2);
        Assert.assertEquals(content1, content2);
    }
}
