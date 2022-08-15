/**
 * Resource utilities.
 */
package xyz.srclab.common.base

import xyz.srclab.common.collect.toStringMap
import xyz.srclab.common.io.readString
import xyz.srclab.common.readProperties
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.nio.charset.Charset
import java.util.*

/**
 * Loads resource in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmName("load")
@JvmOverloads
fun loadResource(path: CharSequence, classLoader: ClassLoader = BtProps.classLoader()): URL? {
    return classLoader.getResource(path.removeAbsolute())
}

/**
 * Loads resource as [InputStream] in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadStream(path: CharSequence, classLoader: ClassLoader = BtProps.classLoader()): InputStream? {
    return classLoader.getResource(path.removeAbsolute())?.openStream()
}

/**
 * Loads content of resource as string in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadString(
    path: CharSequence,
    charset: Charset = BtProps.charset(),
    classLoader: ClassLoader = BtProps.classLoader()
): String? {
    return loadResource(path.removeAbsolute(), classLoader)?.openStream()?.readString(charset, true)
}

/**
 * Loads content of resource as properties in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadProperties(
    path: CharSequence,
    charset: Charset = BtProps.charset(),
    classLoader: ClassLoader = BtProps.classLoader()
): Map<String, String>? {
    return loadResource(path.removeAbsolute(), classLoader)?.openStream()?.readProperties(charset, true)
}

/**
 * Loads all same-path resources in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadResources(path: CharSequence, classLoader: ClassLoader = BtProps.classLoader()): List<URL> {
    return classLoader.getResources(path.removeAbsolute()).toList()
}

/**
 * Loads all same-path contents of resources as strings in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadStrings(
    path: CharSequence,
    charset: Charset = BtProps.charset(),
    classLoader: ClassLoader = BtProps.classLoader()
): List<String> {
    return classLoader.getResources(path.removeAbsolute()).asSequence().map {
        it.openStream().readString(charset, true)
    }.toList()
}

/**
 * Loads all same-path contents of resources as properties list in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadPropertiesList(
    path: CharSequence,
    charset: Charset = BtProps.charset(),
    classLoader: ClassLoader = BtProps.classLoader()
): List<Map<String, String>> {
    return classLoader.getResources(path.removeAbsolute()).asSequence().map {
        it.openStream().readProperties(charset, true)
    }.toList()
}

private fun CharSequence.removeAbsolute(): String {
    return this.removeIfStartWith("/")
}


/**
 * Reads and parses properties.
 */
@JvmOverloads
fun InputStream.readProperties(charset: Charset = BtProps.charset(), close: Boolean = false): Map<String, String> {
    return this.reader(charset).readProperties(close)
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun Reader.readProperties(close: Boolean = false): Map<String, String> {
    val props = Properties()
    props.load(this)
    val map = props.toStringMap()
    if (close) {
        this.close()
    }
    return map
}

/**
 * Reads and parses properties.
 */
@JvmOverloads
fun File.readProperties(charset: Charset = BtProps.charset()): Map<String, String> {
    return this.reader(charset).readProperties(true)
}