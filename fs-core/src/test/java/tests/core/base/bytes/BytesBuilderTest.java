package tests.core.base.bytes;

import internal.utils.DataGen;
import org.junit.jupiter.api.Test;
import space.sunqian.fs.base.bytes.BytesBuilder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BytesBuilderTest implements DataGen {

    @Test
    public void testBytesBuilder() throws Exception {
        testBytesBuilder(new BytesBuilder(), new ByteArrayOutputStream());
        for (int i = 1; i < 8; i++) {
            BytesBuilder builder = new BytesBuilder(i);
            testBytesBuilder(builder, new ByteArrayOutputStream());
            builder.reset();
            builder.flush();
            builder.close();
            testBytesBuilder(builder, new ByteArrayOutputStream());
        }
        testBytesBuilder(new BytesBuilder(666), new ByteArrayOutputStream());
        testBytesBuilder(new BytesBuilder(1024, 16), new ByteArrayOutputStream());
        {
            // Test Exception
            assertThrows(IllegalArgumentException.class, () -> new BytesBuilder(0));
            assertThrows(IllegalArgumentException.class, () -> new BytesBuilder(-1));
            assertThrows(IllegalArgumentException.class, () ->
                new BytesBuilder(1, -2));
            assertThrows(IllegalArgumentException.class, () ->
                new BytesBuilder(1, 0));
            assertThrows(IllegalArgumentException.class, () ->
                new BytesBuilder(-1, 2));
            BytesBuilder builder = new BytesBuilder();
            assertThrows(IndexOutOfBoundsException.class, () -> builder.write(new byte[0], 0, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> builder.append(new byte[0], 0, 1));
        }
        {
            // Special
            BytesBuilder bytesBuilder = new BytesBuilder(8);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] d1 = randomBytes(8);
            for (byte c : d1) {
                bytesBuilder.append(c);
                output.write(c);
            }
            assertArrayEquals(output.toByteArray(), bytesBuilder.toByteArray());
            for (byte c : d1) {
                bytesBuilder.append(c);
                output.write(c);
            }
            bytesBuilder.append(d1[0]);
            output.write(d1[0]);
            assertArrayEquals(output.toByteArray(), bytesBuilder.toByteArray());
        }
        {
            // toString()
            String str = "hello, 中文！";
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            BytesBuilder bytesBuilder = new BytesBuilder();
            bytesBuilder.append(bytes);
            assertEquals(str, bytesBuilder.toString());
        }
    }

    private void testBytesBuilder(BytesBuilder bytesBuilder, ByteArrayOutputStream output) throws Exception {
        bytesBuilder.append((byte) 'a');
        output.write((byte) 'a');
        byte[] d1 = randomBytes(8);
        byte[] d2 = randomBytes(256);
        bytesBuilder.append((int) d1[0]);
        output.write(d1[0]);
        bytesBuilder.append(d1);
        output.write(d1);
        bytesBuilder.append(d2);
        output.write(d2);
        bytesBuilder.append(new byte[0]);
        output.write(new byte[0]);
        bytesBuilder.append(d2, 8, 16);
        output.write(d2, 8, 16);
        bytesBuilder.append(d2, 6, 222);
        output.write(d2, 6, 222);
        bytesBuilder.append(ByteBuffer.allocate(0));
        // output.write(ByteBuffer.allocate(0));
        {
            // buffer
            ByteBuffer buf1 = ByteBuffer.wrap(d1, 1, 5);
            assertEquals(5, buf1.remaining());
            bytesBuilder.append(buf1);
            assertEquals(0, buf1.remaining());
            output.write(d1, 1, 5);
            byte[] bb = "0123456789".getBytes(StandardCharsets.UTF_8);
            ByteBuffer bb1 = ByteBuffer.allocateDirect(bb.length);
            bb1.put(bb);
            bb1.flip();
            assertEquals(10, bb1.remaining());
            bytesBuilder.append(bb1);
            assertEquals(0, bb1.remaining());
            output.write(bb);
        }
        assertEquals(bytesBuilder.length(), output.toByteArray().length);
        assertArrayEquals(bytesBuilder.toByteArray(), output.toByteArray());
        assertEquals(bytesBuilder.toByteBuffer(), ByteBuffer.wrap(output.toByteArray()));
    }
}