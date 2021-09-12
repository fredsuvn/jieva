package xyz.srclab.common.base

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

fun javaVersion(): String {
    return getSystemProperty(JAVA_VERSION_KEY)
}

fun javaVendor(): String {
    return getSystemProperty(JAVA_VENDOR_KEY)
}

fun javaVendorUrl(): String {
    return getSystemProperty(JAVA_VENDOR_URL_KEY)
}

fun javaHome(): String {
    return getSystemProperty(JAVA_HOME_KEY)
}

fun javaVmSpecificationVersion(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_VERSION_KEY)
}

fun javaVmSpecificationVendor(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY)
}

fun javaVmSpecificationName(): String {
    return getSystemProperty(JAVA_VM_SPECIFICATION_NAME_KEY)
}

fun javaVmVersion(): String {
    return getSystemProperty(JAVA_VM_VERSION_KEY)
}

fun javaVmVendor(): String {
    return getSystemProperty(JAVA_VM_VENDOR_KEY)
}

fun javaVmName(): String {
    return getSystemProperty(JAVA_VM_NAME_KEY)
}

fun javaSpecificationVersion(): String {
    return getSystemProperty(JAVA_SPECIFICATION_VERSION_KEY)
}

fun javaSpecificationVendor(): String {
    return getSystemProperty(JAVA_SPECIFICATION_VENDOR_KEY)
}

fun javaSpecificationName(): String {
    return getSystemProperty(JAVA_SPECIFICATION_NAME_KEY)
}

fun javaClassVersion(): String {
    return getSystemProperty(JAVA_CLASS_VERSION_KEY)
}

fun javaClassPath(): String {
    return getSystemProperty(JAVA_CLASS_PATH_KEY)
}

fun javaLibraryPath(): String {
    return getSystemProperty(JAVA_LIBRARY_PATH_KEY)
}

fun javaIoTmpdir(): String {
    return getSystemProperty(JAVA_IO_TMPDIR_KEY)
}

fun javaCompiler(): String {
    return getSystemProperty(JAVA_COMPILER_KEY)
}

fun javaExtDirs(): String {
    return getSystemProperty(JAVA_EXT_DIRS_KEY)
}

fun osName(): String {
    return getSystemProperty(OS_NAME_KEY)
}

fun osArch(): String {
    return getSystemProperty(OS_ARCH_KEY)
}

fun osVersion(): String {
    return getSystemProperty(OS_VERSION_KEY)
}

fun fileSeparator(): String {
    return getSystemProperty(FILE_SEPARATOR_KEY)
}

fun pathSeparator(): String {
    return getSystemProperty(PATH_SEPARATOR_KEY)
}

fun lineSeparator(): String {
    return getSystemProperty(LINE_SEPARATOR_KEY)
}

fun userName(): String {
    return getSystemProperty(USER_NAME_KEY)
}

fun userHome(): String {
    return getSystemProperty(USER_HOME_KEY)
}

fun userDir(): String {
    return getSystemProperty(USER_DIR_KEY)
}

/**
 * Gets the system property indicated by the specified key.
 *
 * @see System.getProperty
 */
@Throws(NullPointerException::class)
fun getSystemProperty(key: String): String {
    return getSystemPropertyOrNull(key)!!
}

/**
 * Gets the system property indicated by the specified key, or null if not found.
 *
 * @see System.getProperty
 */
fun getSystemPropertyOrNull(key: String): String? {
    return System.getProperty(key)
}

/**
 * Sets the system property indicated by the specified key.
 *
 * @see System.setProperty
 */
fun setSystemProperty(key: String, value: String) {
    System.setProperty(key, value)
}

/**
 * Gets the system properties.
 *
 * @see System.getProperties
 */
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
@Throws(NullPointerException::class)
fun getEnvironmentVariable(key: String): String {
    return getEnvironmentVariableOrNull(key)!!
}

/**
 * Gets the value of the specified environment variable, or null if not found.
 *
 * @see System.getenv
 */
fun getEnvironmentVariableOrNull(key: String): String? {
    return System.getenv(key)
}

/**
 * Gets environment variables.
 *
 * @see System.getenv
 */
fun getEnvironmentVariables(): Map<String, String> {
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
fun javaMajorVersion(): Int {
    val javaVersion = javaVersion()
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