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
    val javaVersion: String
        @JvmName("javaVersion") get() = getPropertyNotNull(KEY_OF_JAVA_VERSION)

    @JvmStatic
    val javaVendor: String
        @JvmName("javaVendor") get() = getPropertyNotNull(KEY_OF_JAVA_VENDOR)

    @JvmStatic
    val javaVendorUrl: String
        @JvmName("javaVendorUrl") get() = getPropertyNotNull(KEY_OF_JAVA_VENDOR_URL)

    @JvmStatic
    val javaHome: String
        @JvmName("javaHome") get() = getPropertyNotNull(KEY_OF_JAVA_HOME)

    @JvmStatic
    val javaVmSpecificationVersion: String
        @JvmName("javaVmSpecificationVersion") get() = getPropertyNotNull(KEY_OF_JAVA_VM_SPECIFICATION_VERSION)

    @JvmStatic
    val javaVmSpecificationVendor: String
        @JvmName("javaVmSpecificationVendor") get() = getPropertyNotNull(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR)

    @JvmStatic
    val javaVmSpecificationName: String
        @JvmName("javaVmSpecificationName") get() = getPropertyNotNull(KEY_OF_JAVA_VM_SPECIFICATION_NAME)

    @JvmStatic
    val javaVmVersion: String
        @JvmName("javaVmVersion") get() = getPropertyNotNull(KEY_OF_JAVA_VM_VERSION)

    @JvmStatic
    val javaVmVendor: String
        @JvmName("javaVmVendor") get() = getPropertyNotNull(KEY_OF_JAVA_VM_VENDOR)

    @JvmStatic
    val javaVmName: String
        @JvmName("javaVmName") get() = getPropertyNotNull(KEY_OF_JAVA_VM_NAME)

    @JvmStatic
    val javaSpecificationVersion: String
        @JvmName("javaSpecificationVersion") get() = getPropertyNotNull(KEY_OF_JAVA_SPECIFICATION_VERSION)

    @JvmStatic
    val javaSpecificationVendor: String
        @JvmName("javaSpecificationVendor") get() = getPropertyNotNull(KEY_OF_JAVA_SPECIFICATION_VENDOR)

    @JvmStatic
    val javaSpecificationName: String
        @JvmName("javaSpecificationName") get() = getPropertyNotNull(KEY_OF_JAVA_SPECIFICATION_NAME)

    @JvmStatic
    val javaClassVersion: String
        @JvmName("javaClassVersion") get() = getPropertyNotNull(KEY_OF_JAVA_CLASS_VERSION)

    @JvmStatic
    val javaClassPath: String
        @JvmName("javaClassPath") get() = getPropertyNotNull(KEY_OF_JAVA_CLASS_PATH)

    @JvmStatic
    val javaLibraryPath: String
        @JvmName("javaLibraryPath") get() = getPropertyNotNull(KEY_OF_JAVA_LIBRARY_PATH)

    @JvmStatic
    val javaIoTmpdir: String
        @JvmName("javaIoTmpdir") get() = getPropertyNotNull(KEY_OF_JAVA_IO_TMPDIR)

    @JvmStatic
    val javaCompiler: String
        @JvmName("javaCompiler") get() = getPropertyNotNull(KEY_OF_JAVA_COMPILER)

    @JvmStatic
    val javaExtDirs: String
        @JvmName("javaExtDirs") get() = getPropertyNotNull(KEY_OF_JAVA_EXT_DIRS)

    @JvmStatic
    val osName: String
        @JvmName("osName") get() = getPropertyNotNull(KEY_OF_OS_NAME)

    @JvmStatic
    val osArch: String
        @JvmName("osArch") get() = getPropertyNotNull(KEY_OF_OS_ARCH)

    @JvmStatic
    val osVersion: String
        @JvmName("osVersion") get() = getPropertyNotNull(KEY_OF_OS_VERSION)

    @JvmStatic
    val fileSeparator: String
        @JvmName("fileSeparator") get() = getPropertyNotNull(KEY_OF_FILE_SEPARATOR)

    @JvmStatic
    val pathSeparator: String
        @JvmName("pathSeparator") get() = getPropertyNotNull(KEY_OF_PATH_SEPARATOR)

    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() = getPropertyNotNull(KEY_OF_LINE_SEPARATOR)

    @JvmStatic
    val userName: String
        @JvmName("userName") get() = getPropertyNotNull(KEY_OF_USER_NAME)

    @JvmStatic
    val userHome: String
        @JvmName("userHome") get() = getPropertyNotNull(KEY_OF_USER_HOME)

    @JvmStatic
    val userDir: String
        @JvmName("userDir") get() = getPropertyNotNull(KEY_OF_USER_DIR)

    @JvmStatic
    val availableProcessors: Int
        @JvmName("availableProcessors") get() = Runtime.getRuntime().availableProcessors()

    private fun getPropertyNotNull(key: String): String {
        return getProperty(key).asNotNull()
    }

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
}