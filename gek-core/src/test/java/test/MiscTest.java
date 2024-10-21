package test;

import org.testng.annotations.Test;
import test.io.StreamTest;
import xyz.fslabo.common.base.JieRandom;
import xyz.fslabo.test.JieTest;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MiscTest {

    @Test
    public void testMisc() {
        //createInputFile();
    }

    public void createInputFile() {
        byte[] data = new byte[StreamTest.SOURCE_SIZE];
        JieRandom.fill(data);
        Path path = Paths.get("src", "test", "resources", "io", "input.test");
        JieTest.createFile(path, data);
    }
}
