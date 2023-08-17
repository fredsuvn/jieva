package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTest {

    private static final String DATA = "1qaz2wsx3edc4rfv5tgb6yhn7ujm8ik,!@#$%^&**())$%^&*(*&^<?:LKJHGFDFVGBN" +
        "阿萨法师房间卡死灵法师福卡上积分算法来释放IE覅偶就偶尔见佛耳机佛诶or";

    @Test
    public void testRead() throws IOException {
        String data = DATA;
        File file = new File("FileTest-testRead.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        fileOutputStream.write(DATA.getBytes(FsString.CHARSET));
        fileOutputStream.close();
        Assert.assertEquals(FsFile.readString(file.toPath()), data);
        Assert.assertEquals(
            FsFile.readString(file.toPath(), 18, 36),
            new String(data.getBytes(FsString.CHARSET), 18, 36)
        );
        file.delete();
    }

    @Test
    public void testWrite() throws IOException {
        File file = new File("FileTest-testWrite.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        FsFile.writeString(file.toPath(), "lalala");
        FsFile.writeString(file.toPath(), 6, 18, "222");
        Assert.assertEquals(FsFile.readString(file.toPath()), "lalala222");
        FsFile.writeString(file.toPath(), 6, 7, "1");
        Assert.assertEquals(FsFile.readString(file.toPath()), "lalala122");
        FsFile.writeString(file.toPath(), 7, 100, "3333中文中文");
        Assert.assertEquals(FsFile.readString(file.toPath()), "lalala13333中文中文");
        file.delete();
    }
}
