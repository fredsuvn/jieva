package xyz.srclab.common.serialize

import xyz.srclab.annotations.Immutable
import xyz.srclab.common.base.ByteArrayRef
import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.io.asOutputStream
import xyz.srclab.common.io.asWriter
import xyz.srclab.common.io.toReader
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.serialize.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * Serial represents a middle object between binary sequence and java object. There are two type of methods:
 *
 * * To methods: Includes pattern of toXxx() and writeToXxx(),
 * used to convert serial to binary sequence such as [toInputStream];
 * * Parse methods: used to convert serial to java object such as [parseMap];
 *
 * @see Serializer
 * @see Json
 */
@Immutable
interface Serial {

    // To methods: Serial -> Binary Sequence

    fun toInputStream(): InputStream

    fun toReader(): Reader {
        return toReader(DEFAULT_CHARSET)
    }

    fun toReader(charset: Charset): Reader {
        return toInputStream().toReader(charset)
    }

    fun toBytes(): ByteArray {
        return toInputStream().readBytes()
    }

    fun toByteBuffer(): ByteBuffer {
        return ByteBuffer.wrap(toBytes())
    }

    fun toText(): String {
        return toReader().readText()
    }

    fun toText(charset: Charset): String {
        return toReader(charset).readText()
    }

    fun writeTo(outputStream: OutputStream) {
        toInputStream().copyTo(outputStream)
    }

    fun writeTo(writer: Writer) {
        toReader().copyTo(writer)
    }

    fun writeTo(writer: Writer, charset: Charset) {
        toReader(charset).copyTo(writer)
    }

    fun writeTo(byteArray: ByteArray) {
        toInputStream().read(byteArray)
    }

    fun writeTo(byteArray: ByteArray, offset: Int) {
        writeTo(byteArray, offset, byteArray.size - offset)
    }

    fun writeTo(byteArray: ByteArray, offset: Int, length: Int) {
        toInputStream().read(byteArray, offset, length)
    }

    fun writeTo(byteArrayRef: ByteArrayRef) {
        toInputStream().read(byteArrayRef.array, byteArrayRef.startIndex, byteArrayRef.length)
    }

    fun writeTo(byteBuffer: ByteBuffer) {
        //byteBuffer.put(toBytes())
        toInputStream().copyTo(byteBuffer.asOutputStream())
    }

    fun writeTo(appendable: Appendable) {
        //appendable.append(toReader().readText())
        toReader().copyTo(appendable.asWriter())
    }

    fun writeTo(appendable: Appendable, charset: Charset) {
        //appendable.append(toReader(charset).readText())
        toReader(charset).copyTo(appendable.asWriter())
    }

    // Parse methods: Serial -> Java Object

    fun <T> parse(type: Class<T>): T {
        return parseOrNull(type) ?: throw IllegalArgumentException("Failed parse to: $type")
    }

    fun <T> parse(type: Type): T {
        return parseOrNull(type) ?: throw IllegalArgumentException("Failed parse to: $type")
    }

    fun <T> parse(typeRef: TypeRef<T>): T {
        return parseOrNull(typeRef) ?: throw IllegalArgumentException("Failed parse to: ${typeRef.type}")
    }

    fun <T> parseOrNull(type: Class<T>): T? {
        return parseOrNull(type as Type)
    }

    fun <T> parseOrNull(type: Type): T?

    fun <T> parseOrNull(typeRef: TypeRef<T>): T? {
        return parseOrNull(typeRef.type)
    }

    fun parseString(): String? {
        return parse(String::class.java)
    }

    fun parseStringOrNull(): String? {
        return parseOrNull(String::class.java)
    }

    fun parseBoolean(): Boolean {
        return parse(Boolean::class.javaPrimitiveType!!)
    }

    fun parseByte(): Byte {
        return parse(Byte::class.javaPrimitiveType!!)
    }

    fun parseShort(): Short {
        return parse(Short::class.javaPrimitiveType!!)
    }

    fun parseChar(): Char {
        return parse(Char::class.javaPrimitiveType!!)
    }

    fun parseInt(): Int {
        return parse(Int::class.javaPrimitiveType!!)
    }

    fun parseLong(): Long {
        return parse(Long::class.javaPrimitiveType!!)
    }

    fun parseFloat(): Float {
        return parse(Float::class.javaPrimitiveType!!)
    }

    fun parseDouble(): Double {
        return parse(Double::class.javaPrimitiveType!!)
    }

    fun parseBigInteger(): BigInteger {
        return parse(BigInteger::class.java)
    }

    fun parseBigDecimal(): BigDecimal {
        return parse(BigDecimal::class.java)
    }

    fun parseMap(): Map<String, Any?> {
        return parse(object : TypeRef<Map<String, Any?>>() {})
    }

    fun parseArray(): Array<Any?> {
        return parse(Array<Any?>::class.java)
    }
}