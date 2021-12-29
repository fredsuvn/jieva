@file:JvmName("BSystems")

package xyz.srclab.common.base

import org.apache.commons.lang3.SystemUtils
import java.io.File

const val JAVA_VERSION_KEY = "java.version"

const val JAVA_VENDOR_KEY = "java.vendor"

const val JAVA_VENDOR_URL_KEY = "java.vendor.url"

const val JAVA_HOME_KEY = "java.home"

const val JAVA_VM_SPECIFICATION_VERSION_KEY = "java.vm.specification.version"

const val JAVA_VM_SPECIFICATION_VENDOR_KEY = "java.vm.specification.vendor"

const val JAVA_VM_SPECIFICATION_NAME_KEY = "java.vm.specification.name"

const val JAVA_VM_VERSION_KEY = "java.vm.version"

const val JAVA_VM_VENDOR_KEY = "java.vm.vendor"

const val JAVA_VM_NAME_KEY = "java.vm.name"

const val JAVA_SPECIFICATION_VERSION_KEY = "java.specification.version"

const val JAVA_SPECIFICATION_VENDOR_KEY = "java.specification.vendor"

const val JAVA_SPECIFICATION_NAME_KEY = "java.specification.name"

const val JAVA_CLASS_VERSION_KEY = "java.class.version"

const val JAVA_CLASS_PATH_KEY = "java.class.path"

const val JAVA_LIBRARY_PATH_KEY = "java.library.path"

const val JAVA_IO_TMPDIR_KEY = "java.io.tmpdir"

const val JAVA_COMPILER_KEY = "java.compiler"

const val JAVA_EXT_DIRS_KEY = "java.ext.dirs"

const val OS_NAME_KEY = "os.name"

const val OS_ARCH_KEY = "os.arch"

const val OS_VERSION_KEY = "os.version"

const val FILE_SEPARATOR_KEY = "file.separator"

const val PATH_SEPARATOR_KEY = "path.separator"

const val LINE_SEPARATOR_KEY = "line.separator"

const val USER_NAME_KEY = "user.name"

const val USER_HOME_KEY = "user.home"

const val USER_DIR_KEY = "user.dir"

/**
 * Default file separator: [File.separator].
 * "/" on Unix, "\\" on Windows.
 */
@JvmField
val FILE_SEPARATOR: String = File.separator

/**
 * Default path separator: [File.pathSeparator].
 * ":" on Unix, ";" on Windows.
 */
@JvmField
val PATH_SEPARATOR: String = File.pathSeparator

/**
 * Default line separator: [System.lineSeparator].
 * "\n" on Unix, "\r\n" on Windows, "\r" on Mac.
 */
@JvmField
val LINE_SEPARATOR: String = System.lineSeparator()

fun getJavaVersion(): String {
    return getSystemProperty(JAVA_VERSION_KEY)
}

fun getJavaVendor(): String {
    return getSystemProperty(JAVA_VENDOR_KEY)
}

fun getJavaVendorUrl(): String {
    return getSystemProperty(JAVA_VENDOR_URL_KEY)
}

fun getJavaHome(): String {
    return getSystemProperty(JAVA_HOME_KEY)
}

fun getJavaVmSpecificationVersion(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_VERSION_KEY)
}

fun getJavaVmSpecificationVendor(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY)
}

fun getJavaVmSpecificationName(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_NAME_KEY)
}

fun getJavaVmVersion(): String {
    return getSystemProperty(JAVA_VM_VERSION_KEY)
}

fun getJavaVmVendor(): String {
    return getSystemProperty(JAVA_VM_VENDOR_KEY)
}

fun getJavaVmName(): String {
    return getSystemProperty(JAVA_VM_NAME_KEY)
}

fun getJavaSpecificationVersion(): String {
    return getSystemProperty(JAVA_SPECIFICATION_VERSION_KEY)
}

fun getJavaSpecificationVendor(): String {
    return getSystemProperty(JAVA_SPECIFICATION_VENDOR_KEY)
}

fun getJavaSpecificationName(): String {
    return getSystemProperty(JAVA_SPECIFICATION_NAME_KEY)
}

fun getJavaClassVersion(): String {
    return getSystemProperty(JAVA_CLASS_VERSION_KEY)
}

fun getJavaClassPath(): String {
    return getSystemProperty(JAVA_CLASS_PATH_KEY)
}

fun getJavaLibraryPath(): String {
    return getSystemProperty(JAVA_LIBRARY_PATH_KEY)
}

fun getJavaIoTmpdir(): String {
    return getSystemProperty(JAVA_IO_TMPDIR_KEY)
}

fun getJavaCompiler(): String {
    return getSystemProperty(JAVA_COMPILER_KEY)
}

fun getJavaExtDirs(): String {
    return getSystemProperty(JAVA_EXT_DIRS_KEY)
}

fun getOsName(): String {
    return getSystemProperty(OS_NAME_KEY)
}

fun getOsArch(): String {
    return getSystemProperty(OS_ARCH_KEY)
}

fun getOsVersion(): String {
    return getSystemProperty(OS_VERSION_KEY)
}

fun getFileSeparator(): String {
    return getSystemProperty(FILE_SEPARATOR_KEY)
}

fun getPathSeparator(): String {
    return getSystemProperty(PATH_SEPARATOR_KEY)
}

fun getLineSeparator(): String {
    return getSystemProperty(LINE_SEPARATOR_KEY)
}

fun getUserName(): String {
    return getSystemProperty(USER_NAME_KEY)
}

fun getUserHome(): String {
    return getSystemProperty(USER_HOME_KEY)
}

fun getUserDir(): String {
    return getSystemProperty(USER_DIR_KEY)
}

/**
 * Gets the system property indicated by the specified key.
 *
 * @see System.getProperty
 */
@JvmName("getProperty")
fun getSystemProperty(key: String): String {
    return getSystemPropertyOrNull(key) ?: throw NullPointerException("Value not found: $key")
}

/**
 * Gets the system property indicated by the specified key, or null if not found.
 *
 * @see System.getProperty
 */
@JvmName("getPropertyOrNull")
fun getSystemPropertyOrNull(key: String): String? {
    return System.getProperty(key)
}

/**
 * Sets the system property indicated by the specified key.
 *
 * @see System.setProperty
 */
@JvmName("setProperty")
fun setSystemProperty(key: String, value: String) {
    System.setProperty(key, value)
}

/**
 * Gets the system properties.
 *
 * @see System.getProperties
 */
@JvmName("getProperties")
fun getSystemProperties(): Map<String, String> {
    val result: MutableMap<String, String> = HashMap()
    System.getProperties().forEach {
        val key = it.key.toString()
        val value = it.value.toString()
        result[key] = value
    }
    return result
}

/**
 * Gets the value of the specified environment variable.
 *
 * @see System.getenv
 */
fun getEnv(key: String): String {
    return getEnvOrNull(key) ?: throw NullPointerException("Env not found: $key")
}

/**
 * Gets the value of the specified environment variable, or null if not found.
 *
 * @see System.getenv
 */
fun getEnvOrNull(key: String): String? {
    return System.getenv(key)
}

/**
 * Gets environment variables.
 *
 * @see System.getenv
 */
fun getEnvs(): Map<String, String> {
    return System.getenv()
}

/**
 * Returns the number of processors available to the Java virtual machine.
 *
 * @see Runtime.availableProcessors
 */
fun availableProcessors(): Int {
    return Runtime.getRuntime().availableProcessors()
}

/**
 * Gets java major version.
 *
 * If java version is equal to or less than 1.8, return second version number such as _6 of 1.6.x_, _8 of 1.8.x_.
 * Else return first number such as _9 of 9.0_.
 */
fun getJavaMajorVersion(): Int {
    val javaVersion = getJavaVersion()
    val dotIndex = DOT_MATCHER.indexIn(javaVersion)
    if (dotIndex <= 0) {
        return -1
    }
    val v = Integer.parseInt(javaVersion.substring(0, dotIndex))
    if (v >= 9) {
        return v
    }
    if (v == 1) {
        val nextDotIndex = DOT_MATCHER.indexIn(javaVersion, dotIndex + 1)
        if (nextDotIndex < 0) {
            return -1
        }
        return Integer.parseInt(javaVersion.substring(dotIndex + 1, nextDotIndex))
    }
    return -1
}

fun isWindows(): Boolean {
    return SystemUtils.IS_OS_WINDOWS
}

fun isLinux(): Boolean {
    return SystemUtils.IS_OS_LINUX
}

fun isMac(): Boolean {
    return SystemUtils.IS_OS_MAC
}

fun isUnix(): Boolean {
    return SystemUtils.IS_OS_UNIX
}

fun isBsd(): Boolean {
    return SystemUtils.IS_OS_FREE_BSD || SystemUtils.IS_OS_OPEN_BSD || SystemUtils.IS_OS_NET_BSD
}

fun isYunfan(): Boolean {
    class YunfanIsComingException : RuntimeException("长风破浪会有时，直挂云帆济沧海。占个坑~")
    throw YunfanIsComingException()
}