package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.base.ref.LongRef;
import xyz.srclab.common.io.FsFile;
import xyz.srclab.common.io.FsIO;
import xyz.srclab.common.io.FsIOException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTest {

    private static final String DATA = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,!@#$%^&**())$%^&*(*&^<?:LKJHGFDFVGBN" +
        "阿萨法师房间卡死灵法师福卡上积分算法来释放IE覅偶就偶尔见佛耳机佛诶or";

    public static File createFile(String path, String data) throws IOException {
        File file = new File(path);
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(data.getBytes(FsString.CHARSET));
        fileOutputStream.close();
        return file;
    }

    @Test
    public void testFile() throws IOException {
        String data = DATA;
        byte[] bytes = data.getBytes(FsString.CHARSET);
        File file = createFile("FileTest-testFile.txt", data);
        FsFile fsFile = FsFile.from(file.toPath());
        Assert.expectThrows(FsIOException.class, () -> fsFile.bindInputStream());
        fsFile.open("r");
        fsFile.position(3);
        InputStream bin = fsFile.bindInputStream();
        IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
        fsFile.position(2);
        IOTest.testInputStream(data, 2, 30, FsIO.limit(fsFile.bindInputStream(), 30), false);
        fsFile.close();
        Assert.expectThrows(FsIOException.class, () -> fsFile.bindInputStream());
        Assert.expectThrows(FsIOException.class, () -> bin.read());
        fsFile.open("rw");
        fsFile.position(3);
        IOTest.testInputStream(data, 3, bytes.length - 3, bin, false);
        fsFile.position(2);
        IOTest.testInputStream(data, 2, 30, FsIO.limit(fsFile.bindInputStream(), 30), false);
        fsFile.position(4);
        IOTest.testOutStream(-1, fsFile.bindOutputStream(), (offset, length) -> FsIO.readBytes(file.toPath(), offset + 4, length));
        long fileLength = fsFile.length();
        fsFile.position(fileLength);
        LongRef newLength = new LongRef(fileLength);
        IOTest.testOutStream(-1, fsFile.bindOutputStream(), (offset, length) -> {
            newLength.incrementAndGet(length);
            return FsIO.readBytes(file.toPath(), offset + fileLength, length);
        });
        Assert.assertEquals(fsFile.length(), newLength.get());
        file.delete();
    }

    // @Test
    // public void testFileCache() throws IOException {
    //     String data = "0123456789" +
    //         "0123456789" +
    //         "0123456789" +
    //         "0123456789" +
    //         "0123456789";
    //     File file = new File("FileTest-testFileCache.txt");
    //     FileOutputStream fileOutputStream = new FileOutputStream(file, false);
    //     fileOutputStream.write(data.getBytes(FsString.CHARSET));
    //     fileOutputStream.close();
    //     FsFileCache fileCache = FsFileCache.newBuilder()
    //         .chunkSize(3)
    //         .bufferSize(16)
    //         .build();
    //     InputStream in = FsIO.limit(fileCache.getInputStream(file.toPath(), 5), 30);
    //     IOTest.testInputStream(data, 5, 30, in, false);
    //     file.delete();
    // }
}
