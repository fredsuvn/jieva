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
    fun javaVersion(): String? = getProperty(KEY_OF_JAVA_VERSION)

    @JvmStatic
    fun javaVendor(): String? = getProperty(KEY_OF_JAVA_VENDOR)

    @JvmStatic
    fun javaVendorUrl(): String? = getProperty(KEY_OF_JAVA_VENDOR_URL)

    @JvmStatic
    fun javaHome(): String? = getProperty(KEY_OF_JAVA_HOME)

    @JvmStatic
    fun javaVmSpecificationVersion(): String? = getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VERSION)

    @JvmStatic
    fun javaVmSpecificationVendor(): String? = getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR)

    @JvmStatic
    fun javaVmSpecificationName(): String? = getProperty(KEY_OF_JAVA_VM_SPECIFICATION_NAME)

    @JvmStatic
    fun javaVmVersion(): String? = getProperty(KEY_OF_JAVA_VM_VERSION)

    @JvmStatic
    fun javaVmVendor(): String? = getProperty(KEY_OF_JAVA_VM_VENDOR)

    @JvmStatic
    fun javaVmName(): String? = getProperty(KEY_OF_JAVA_VM_NAME)

    @JvmStatic
    fun javaSpecificationVersion(): String? = getProperty(KEY_OF_JAVA_SPECIFICATION_VERSION)

    @JvmStatic
    fun javaSpecificationVendor(): String? = getProperty(KEY_OF_JAVA_SPECIFICATION_VENDOR)

    @JvmStatic
    fun javaSpecificationName(): String? = getProperty(KEY_OF_JAVA_SPECIFICATION_NAME)

    @JvmStatic
    fun javaClassVersion(): String? = getProperty(KEY_OF_JAVA_CLASS_VERSION)

    @JvmStatic
    fun javaClassPath(): String? = getProperty(KEY_OF_JAVA_CLASS_PATH)

    @JvmStatic
    fun javaLibraryPath(): String? = getProperty(KEY_OF_JAVA_LIBRARY_PATH)

    @JvmStatic
    fun javaIoTmpdir(): String? = getProperty(KEY_OF_JAVA_IO_TMPDIR)

    @JvmStatic
    fun javaCompiler(): String? = getProperty(KEY_OF_JAVA_COMPILER)

    @JvmStatic
    fun javaExtDirs(): String? = getProperty(KEY_OF_JAVA_EXT_DIRS)

    @JvmStatic
    fun osName(): String? = getProperty(KEY_OF_OS_NAME)

    @JvmStatic
    fun osArch(): String? = getProperty(KEY_OF_OS_ARCH)

    @JvmStatic
    fun osVersion(): String? = getProperty(KEY_OF_OS_VERSION)

    @JvmStatic
    fun fileSeparator(): String? = getProperty(KEY_OF_FILE_SEPARATOR)

    @JvmStatic
    fun pathSeparator(): String? = getProperty(KEY_OF_PATH_SEPARATOR)

    @JvmStatic
    fun lineSeparator(): String? = getProperty(KEY_OF_LINE_SEPARATOR)

    @JvmStatic
    fun userName(): String? = getProperty(KEY_OF_USER_NAME)

    @JvmStatic
    fun userHome(): String? = getProperty(KEY_OF_USER_HOME)

    @JvmStatic
    fun userDir(): String? = getProperty(KEY_OF_USER_DIR)

    @JvmStatic
    fun getProperty(key: String): String? {
        return System.getProperty(key)
    }

    @JvmStatic
    fun setProperty(key: String, value: String) {
        System.setProperty(key, value)
    }

    @JvmStatic
    fun allProperties(): Map<String, String> {
        val properties = System.getProperties() ?: return mapOf()
        return propertiesToMap(properties)
    }

    @JvmStatic
    fun getVariable(key: String): String? {
        return System.getenv(key)
    }

    @JvmStatic
    fun allVariables(): Map<String, String> {
        return System.getenv()
    }

    private fun propertiesToMap(properties: Properties): Map<String, String> {
        val map = mutableMapOf<String, String>()
        properties.forEach { k: Any?, v: Any? -> map[k.toString()] = v.toString() }
        return map
    }

    @JvmStatic
    fun availableProcessors(): Int = Runtime.getRuntime().availableProcessors()
}