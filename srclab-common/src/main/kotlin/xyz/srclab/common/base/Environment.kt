package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object Environment {

    const val KEY_OF_JAVA_VERSION = "java.version"
    const val KEY_OF_JAVA_VENDOR = "java.vendor"
    const val KEY_OF_JAVA_VENDOR_URL = "java.vendor.url"
    const val KEY_OF_JAVA_HOME = "java.home"
    const val KEY_OF_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version"
    const val KEY_OF_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor"
    const val KEY_OF_JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name"
    const val KEY_OF_JAVA_VM_VERSION = "java.vm.version"
    const val KEY_OF_JAVA_VM_VENDOR = "java.vm.vendor"
    const val KEY_OF_JAVA_VM_NAME = "java.vm.name"
    const val KEY_OF_JAVA_SPECIFICATION_VERSION = "java.specification.version"
    const val KEY_OF_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor"
    const val KEY_OF_JAVA_SPECIFICATION_NAME = "java.specification.name"
    const val KEY_OF_JAVA_CLASS_VERSION = "java.class.version"
    const val KEY_OF_JAVA_CLASS_PATH = "java.class.path"
    const val KEY_OF_JAVA_LIBRARY_PATH = "java.library.path"
    const val KEY_OF_JAVA_IO_TMPDIR = "java.io.tmpdir"
    const val KEY_OF_JAVA_COMPILER = "java.compiler"
    const val KEY_OF_JAVA_EXT_DIRS = "java.ext.dirs"
    const val KEY_OF_OS_NAME = "os.name"
    const val KEY_OF_OS_ARCH = "os.arch"
    const val KEY_OF_OS_VERSION = "os.version"
    const val KEY_OF_FILE_SEPARATOR = "file.separator"
    const val KEY_OF_PATH_SEPARATOR = "path.separator"
    const val KEY_OF_LINE_SEPARATOR = "line.separator"
    const val KEY_OF_USER_NAME = "user.name"
    const val KEY_OF_USER_HOME = "user.home"
    const val KEY_OF_USER_DIR = "user.dir"

    @JvmStatic
    fun getProperty(key: String): String? = getSystemProperty(key)

    @JvmStatic
    fun setProperty(key: String, value: String) {
        System.setProperty(key, value)
    }

    @JvmStatic
    private fun getSystemProperty(name: String): String? {
        return System.getProperty(name)
    }

    @JvmStatic
    fun properties(): Map<String, String> {
        val properties = System.getProperties() ?: return mapOf()
        return propertiesToMap(properties)
    }

    @JvmStatic
    private fun propertiesToMap(properties: Properties): Map<String, String> {
        val map = mutableMapOf<String, String>()
        properties.forEach { k: Any?, v: Any? -> map[k.toString()] = v.toString() }
        return map
    }

    @JvmStatic
    fun getVariable(key: String): String? {
        return System.getenv(key)
    }

    @JvmStatic
    fun variables(): Map<String, String> {
        return System.getenv()
    }

    @JvmStatic
    fun javaVersion(): String? = getSystemProperty(KEY_OF_JAVA_VERSION)

    @JvmStatic
    fun javaVendor(): String? = getSystemProperty(KEY_OF_JAVA_VENDOR)

    @JvmStatic
    fun javaVendorUrl(): String? = getSystemProperty(KEY_OF_JAVA_VENDOR_URL)

    @JvmStatic
    fun javaHome(): String? = getSystemProperty(KEY_OF_JAVA_HOME)

    @JvmStatic
    fun javaVmSpecificationVersion(): String? = getSystemProperty(KEY_OF_JAVA_VM_SPECIFICATION_VERSION)

    @JvmStatic
    fun javaVmSpecificationVendor(): String? = getSystemProperty(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR)

    @JvmStatic
    fun javaVmSpecificationName(): String? = getSystemProperty(KEY_OF_JAVA_VM_SPECIFICATION_NAME)

    @JvmStatic
    fun javaVmVersion(): String? = getSystemProperty(KEY_OF_JAVA_VM_VERSION)

    @JvmStatic
    fun javaVmVendor(): String? = getSystemProperty(KEY_OF_JAVA_VM_VENDOR)

    @JvmStatic
    fun javaVmName(): String? = getSystemProperty(KEY_OF_JAVA_VM_NAME)

    @JvmStatic
    fun javaSpecificationVersion(): String? = getSystemProperty(KEY_OF_JAVA_SPECIFICATION_VERSION)

    @JvmStatic
    fun javaSpecificationVendor(): String? = getSystemProperty(KEY_OF_JAVA_SPECIFICATION_VENDOR)

    @JvmStatic
    fun javaSpecificationName(): String? = getSystemProperty(KEY_OF_JAVA_SPECIFICATION_NAME)

    @JvmStatic
    fun javaClassVersion(): String? = getSystemProperty(KEY_OF_JAVA_CLASS_VERSION)

    @JvmStatic
    fun javaClassPath(): String? = getSystemProperty(KEY_OF_JAVA_CLASS_PATH)

    @JvmStatic
    fun javaLibraryPath(): String? = getSystemProperty(KEY_OF_JAVA_LIBRARY_PATH)

    @JvmStatic
    fun javaIoTmpdir(): String? = getSystemProperty(KEY_OF_JAVA_IO_TMPDIR)

    @JvmStatic
    fun javaCompiler(): String? = getSystemProperty(KEY_OF_JAVA_COMPILER)

    @JvmStatic
    fun javaExtDirs(): String? = getSystemProperty(KEY_OF_JAVA_EXT_DIRS)

    @JvmStatic
    fun osName(): String? = getSystemProperty(KEY_OF_OS_NAME)

    @JvmStatic
    fun osArch(): String? = getSystemProperty(KEY_OF_OS_ARCH)

    @JvmStatic
    fun osVersion(): String? = getSystemProperty(KEY_OF_OS_VERSION)

    @JvmStatic
    fun fileSeparator(): String? = getSystemProperty(KEY_OF_FILE_SEPARATOR)

    @JvmStatic
    fun pathSeparator(): String? = getSystemProperty(KEY_OF_PATH_SEPARATOR)

    @JvmStatic
    fun lineSeparator(): String? = getSystemProperty(KEY_OF_LINE_SEPARATOR)

    @JvmStatic
    fun userName(): String? = getSystemProperty(KEY_OF_USER_NAME)

    @JvmStatic
    fun userHome(): String? = getSystemProperty(KEY_OF_USER_HOME)

    @JvmStatic
    fun userDir(): String? = getSystemProperty(KEY_OF_USER_DIR)
}