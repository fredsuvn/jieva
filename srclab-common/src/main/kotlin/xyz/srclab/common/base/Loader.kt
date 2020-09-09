package xyz.srclab.common.base

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

object Loader {

    @JvmStatic
    fun currentClassLoader(): ClassLoader {
        return Current.classLoader()
    }

    @JvmStatic
    fun <T> findClass(className: String): Class<T>? {
        return try {
            As.notNull(Class.forName(className))
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    @JvmStatic
    fun <T> findClass(className: String, classLoader: ClassLoader): Class<T>? {
        return try {
            As.notNull(Class.forName(className, true, classLoader))
        } catch (e: ClassNotFoundException) {
            null
        }
    }

    @JvmStatic
    @JvmOverloads
    fun <T> loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset): Class<T> {
        return As.notNull(BytesClassLoader.loadClass(bytes, offset, length))
    }

    @JvmStatic
    fun <T> loadClass(inputStream: InputStream): Class<T> {
        return As.notNull(BytesClassLoader.loadClass(inputStream))
    }

    @JvmStatic
    fun <T> loadClass(byteBuffer: ByteBuffer): Class<T> {
        return As.notNull(BytesClassLoader.loadClass(byteBuffer))
    }

    @JvmStatic
    fun findResource(resourceName: String): URL? {
        return findResource(resourceName, currentClassLoader())
    }

    @JvmStatic
    fun findResource(resourceName: String, classLoader: ClassLoader): URL? {
        return classLoader.getResource(resourceName)
    }

    @JvmStatic
    fun findResources(resourceName: String): List<URL> {
        return findResources(resourceName, currentClassLoader())
    }

    @JvmStatic
    fun findResources(resourceName: String?, classLoader: ClassLoader): List<URL> {
        return try {
            val urlEnumeration = classLoader.getResources(resourceName)
            urlEnumeration.toList()
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    private object BytesClassLoader : ClassLoader() {

        fun loadClass(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): Class<*> {
            return super.defineClass(null, bytes, offset, length)
        }

        fun loadClass(inputStream: InputStream): Class<*> {
            return try {
                loadClass(inputStream.readBytes())
            } catch (e: IOException) {
                throw IllegalStateException(e)
            }
        }

        fun loadClass(byteBuffer: ByteBuffer): Class<*> {
            return super.defineClass(null, byteBuffer, null)
        }
    }
}