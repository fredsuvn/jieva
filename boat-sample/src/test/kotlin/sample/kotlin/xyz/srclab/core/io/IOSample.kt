package sample.kotlin.xyz.srclab.core.io

import org.testng.Assert
import org.testng.annotations.Test
import xyz.srclab.common.io.*
import xyz.srclab.common.test.TestLogger
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * @author sunqian
 */
class IOSample {

    @Test
    @Throws(Exception::class)
    fun testStream() {
        val text = "123456\r\n234567\r\n"
        val input: InputStream = ByteArrayInputStream(text.toByteArray())
        val inputString = input.readString()
        input.reset()
        logger.log("inputString: {}", inputString)
        Assert.assertEquals(inputString, text)
        val bytes = input.readBytes()
        input.reset()
        Assert.assertEquals(bytes, text.toByteArray())
        val inputStrings: List<String?> = input.readLines()
        input.reset()
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567"))
        val output = ByteArrayOutputStream()
        input.readTo(output)
        input.reset()
        Assert.assertEquals(output.toByteArray(), bytes)
    }

    @Test
    @Throws(Exception::class)
    fun testReader() {
        val text = "123456\r\n234567\r\n"
        val input: InputStream = ByteArrayInputStream(text.toByteArray())
        val reader = input.toReader()
        val readString = reader.readString()
        input.reset()
        logger.log("readString: {}", readString)
        Assert.assertEquals(readString, text)
        val chars = reader.readString().toCharArray()
        input.reset()
        Assert.assertEquals(chars, text.toCharArray())
        val readStrings: List<String?> = reader.readLines()
        input.reset()
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567"))
        val output = ByteArrayOutputStream()
        val writer = output.toWriter()
        reader.readTo(writer)
        input.reset()
        writer.flush()
        Assert.assertEquals(output.toByteArray(), text.toByteArray())
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}