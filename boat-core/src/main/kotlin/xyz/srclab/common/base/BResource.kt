@file:JvmName("BResource")

package xyz.srclab.common.base

import xyz.srclab.common.io.readString
import xyz.srclab.common.reflect.defaultClassLoader
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

/**
 * Loads resource in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathResource(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): URL? {
    return classLoader.getResource(path.removeAbsolute().removeAbsolute())
}

/**
 * Loads resource as [InputStream] in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathStream(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): InputStream? {
    return classLoader.getResource(path.removeAbsolute().removeAbsolute())?.openStream()
}

/**
 * Loads content of resource as string in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathString(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): String? {
    return loadClasspathResource(path.removeAbsolute(), classLoader)?.openStream()?.readString(charset, true)
}

/**
 * Loads content of resource as properties in current classpath.
 * It finds first resource specified by the [path] (generally in current jar).
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathProperties(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): Map<String, String>? {
    return loadClasspathResource(path.removeAbsolute(), classLoader)?.openStream()?.readProperties(charset, true)
}

/**
 * Loads all same-path resources in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathResources(path: CharSequence, classLoader: ClassLoader = defaultClassLoader()): List<URL> {
    return classLoader.getResources(path.removeAbsolute()).toList()
}

/**
 * Loads all same-path contents of resources as strings in current classpath.
 * The [path] may start with `/` or not, they are equivalent.
 */
@JvmOverloads
fun loadClasspathStrings(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
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
fun loadClasspathPropertiesList(
    path: CharSequence,
    charset: Charset = defaultCharset(),
    classLoader: ClassLoader = defaultClassLoader()
): List<Map<String, String>> {
    return classLoader.getResources(path.removeAbsolute()).asSequence().map {
        it.openStream().readProperties(charset, true)
    }.toList()
}

private fun CharSequence.removeAbsolute(): String {
    return this.removeIfStartWith("/")
}