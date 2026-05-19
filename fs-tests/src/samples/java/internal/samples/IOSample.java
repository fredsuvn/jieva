package internal.samples;

import space.sunqian.fs.io.ByteIOOperator;
import space.sunqian.fs.io.ByteReader;
import space.sunqian.fs.io.ByteSegment;
import space.sunqian.fs.io.CharIOOperator;
import space.sunqian.fs.io.CharReader;
import space.sunqian.fs.io.CharSegment;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.IOOperator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Sample: IO Utilities Usage
 * <p>
 * Purpose: Demonstrate how to use the IO utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     ByteReader and CharReader usage for reading data
 *   </li>
 *   <li>
 *     ByteIOOperator and CharIOOperator usage for advanced IO operations
 *   </li>
 *   <li>
 *     BytesBuilder and CharsBuilder usage for building data
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ByteReader}: Byte reader for reading byte data from various sources
 *   </li>
 *   <li>
 *     {@link CharReader}: Char reader for reading character data from various sources
 *   </li>
 *   <li>
 *     {@link ByteIOOperator}: Byte IO operator for advanced byte operations like transfer
 *   </li>
 *   <li>
 *     {@link CharIOOperator}: Char IO operator for advanced character operations like transfer
 *   </li>
 * </ul>
 */
public class IOSample {

    public static void main(String[] args) throws Exception {
        demonstrateByteReader();
        demonstrateCharReader();
        demonstrateByteIOOperator();
        demonstrateCharIOOperator();
    }

    /**
     * Demonstrates ByteReader usage for reading byte data.
     */
    public static void demonstrateByteReader() {
        System.out.println("=== ByteReader Usage ===");

        try {
            // Create sample byte data
            byte[] sampleData = "Hello, ByteReader!\nThis is a test with multiple lines.\nLine 3 here.\n".getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(sampleData);

            // Create ByteReader from input stream
            ByteReader byteReader = ByteReader.from(inputStream);

            // Example 1: Read all data at once
            System.out.println("Example 1: Read all data at once");
            byte[] buffer = new byte[1024];
            int totalRead = 0;
            int read;
            while ((read = byteReader.readTo(buffer)) != -1) {
                System.out.write(buffer, 0, read);
                totalRead += read;
            }
            System.out.println("Total bytes read: " + totalRead);

            // Reset input stream for next example
            inputStream.reset();
            byteReader = ByteReader.from(inputStream);

            // Example 2: Read with specified length
            System.out.println("\nExample 2: Read with specified length");
            ByteSegment segment = byteReader.read(20);
            byte[] segmentData = segment.array();
            System.out.println("Read segment: " + new String(segmentData, StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates CharReader usage for reading character data.
     */
    public static void demonstrateCharReader() {
        System.out.println("\n=== CharReader Usage ===");

        try {
            // Create sample char data
            String sampleData = "Hello, CharReader!\nThis is a test with multiple lines.\nLine 3 here.\n";
            StringReader reader = new StringReader(sampleData);

            // Create CharReader from string reader
            CharReader charReader = CharReader.from(reader);

            // Example 1: Read all data at once
            System.out.println("Example 1: Read all data at once");
            char[] buffer = new char[1024];
            int totalRead = 0;
            int read;
            while ((read = charReader.readTo(buffer)) != -1) {
                System.out.print(new String(buffer, 0, read));
                totalRead += read;
            }
            System.out.println("Total chars read: " + totalRead);

            // Reset reader for next example
            reader.reset();
            charReader = CharReader.from(reader);

            // Example 2: Read with specified length
            System.out.println("\nExample 2: Read with specified length");
            CharSegment segment = charReader.read(20);
            char[] segmentData = segment.array();
            System.out.println("Read segment: " + new String(segmentData));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates ByteIOOperator usage for advanced byte operations.
     */
    public static void demonstrateByteIOOperator() {
        System.out.println("\n=== ByteIOOperator Usage ===");

        try {
            // Create sample byte data
            byte[] sampleData = "Hello, ByteIOOperator!\nThis is a test.\n".getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(sampleData);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Create ByteIOOperator
            ByteIOOperator byteIOOperator = IOOperator.get(IOKit.bufferSize());

            // Example 1: Transfer data from input to output
            System.out.println("Example 1: Transfer data from input to output");
            long transferred = byteIOOperator.readTo(inputStream, outputStream);
            System.out.println("Transferred " + transferred + " bytes");
            System.out.println("Transferred data: " + outputStream.toString(StandardCharsets.UTF_8));

            // Reset streams for next example
            inputStream.reset();
            outputStream.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates CharIOOperator usage for advanced character operations.
     */
    public static void demonstrateCharIOOperator() {
        System.out.println("\n=== CharIOOperator Usage ===");

        try {
            // Create sample char data
            String sampleData = "Hello, CharIOOperator!\nThis is a test.\n";
            StringReader reader = new StringReader(sampleData);
            StringWriter writer = new StringWriter();

            // Create CharIOOperator
            CharIOOperator charIOOperator = IOOperator.get(IOKit.bufferSize());

            // Example 1: Transfer data from reader to writer
            System.out.println("Example 1: Transfer data from reader to writer");
            long transferred = charIOOperator.readTo(reader, writer);
            System.out.println("Transferred " + transferred + " chars");
            System.out.println("Transferred data: " + writer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}