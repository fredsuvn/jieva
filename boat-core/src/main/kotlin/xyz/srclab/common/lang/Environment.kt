package xyz.srclab.common.lang

import org.apache.commons.lang3.SystemUtils
import java.util.*

/**
 * @author sunqian
 */
object Environment {

    @JvmField
    val KEY_JAVA_VERSION = "java.version"

    @JvmField
    val KEY_JAVA_VENDOR = "java.vendor"

    @JvmField
    val KEY_JAVA_VENDOR_URL = "java.vendor.url"

    @JvmField
    val KEY_JAVA_HOME = "java.home"

    @JvmField
    val KEY_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version"

    @JvmField
    val KEY_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor"

    @JvmField
    val KEY_JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name"

    @JvmField
    val KEY_JAVA_VM_VERSION = "java.vm.version"

    @JvmField
    val KEY_JAVA_VM_VENDOR = "java.vm.vendor"

    @JvmField
    val KEY_JAVA_VM_NAME = "java.vm.name"

    @JvmField
    val KEY_JAVA_SPECIFICATION_VERSION = "java.specification.version"

    @JvmField
    val KEY_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor"

    @JvmField
    val KEY_JAVA_SPECIFICATION_NAME = "java.specification.name"

    @JvmField
    val KEY_JAVA_CLASS_VERSION = "java.class.version"

    @JvmField
    val KEY_JAVA_CLASS_PATH = "java.class.path"

    @JvmField
    val KEY_JAVA_LIBRARY_PATH = "java.library.path"

    @JvmField
    val KEY_JAVA_IO_TMPDIR = "java.io.tmpdir"

    @JvmField
    val KEY_JAVA_COMPILER = "java.compiler"

    @JvmField
    val KEY_JAVA_EXT_DIRS = "java.ext.dirs"

    @JvmField
    val KEY_OS_NAME = "os.name"

    @JvmField
    val KEY_OS_ARCH = "os.arch"

    @JvmField
    val KEY_OS_VERSION = "os.version"

    @JvmField
    val KEY_FILE_SEPARATOR = "file.separator"

    @JvmField
    val KEY_PATH_SEPARATOR = "path.separator"

    @JvmField
    val KEY_LINE_SEPARATOR = "line.separator"

    @JvmField
    val KEY_USER_NAME = "user.name"

    @JvmField
    val KEY_USER_HOME = "user.home"

    @JvmField
    val KEY_USER_DIR = "user.dir"

    @JvmStatic
    val javaVersion: String
        @JvmName("javaVersion") get() {
            return getPropertyAsNotNull(KEY_JAVA_VERSION)
        }

    @JvmStatic
    val javaVendor: String
        @JvmName("javaVendor") get() {
            return getPropertyAsNotNull(KEY_JAVA_VENDOR)
        }

    @JvmStatic
    val javaVendorUrl: String
        @JvmName("javaVendorUrl") get() {
            return getPropertyAsNotNull(KEY_JAVA_VENDOR_URL)
        }

    @JvmStatic
    val javaHome: String
        @JvmName("javaHome") get() {
            return getPropertyAsNotNull(KEY_JAVA_HOME)
        }

    @JvmStatic
    val javaVmSpecificationVersion: String
        @JvmName("javaVmSpecificationVersion") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_SPECIFICATION_VERSION)
        }

    @JvmStatic
    val javaVmSpecificationVendor: String
        @JvmName("javaVmSpecificationVendor") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_SPECIFICATION_VENDOR)
        }

    @JvmStatic
    val javaVmSpecificationName: String
        @JvmName("javaVmSpecificationName") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_SPECIFICATION_NAME)
        }

    @JvmStatic
    val javaVmVersion: String
        @JvmName("javaVmVersion") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_VERSION)
        }

    @JvmStatic
    val javaVmVendor: String
        @JvmName("javaVmVendor") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_VENDOR)
        }

    @JvmStatic
    val javaVmName: String
        @JvmName("javaVmName") get() {
            return getPropertyAsNotNull(KEY_JAVA_VM_NAME)
        }

    @JvmStatic
    val javaSpecificationVersion: String
        @JvmName("javaSpecificationVersion") get() {
            return getPropertyAsNotNull(KEY_JAVA_SPECIFICATION_VERSION)
        }

    @JvmStatic
    val javaSpecificationVendor: String
        @JvmName("javaSpecificationVendor") get() {
            return getPropertyAsNotNull(KEY_JAVA_SPECIFICATION_VENDOR)
        }

    @JvmStatic
    val javaSpecificationName: String
        @JvmName("javaSpecificationName") get() {
            return getPropertyAsNotNull(KEY_JAVA_SPECIFICATION_NAME)
        }

    @JvmStatic
    val javaClassVersion: String
        @JvmName("javaClassVersion") get() {
            return getPropertyAsNotNull(KEY_JAVA_CLASS_VERSION)
        }

    @JvmStatic
    val javaClassPath: String
        @JvmName("javaClassPath") get() {
            return getPropertyAsNotNull(KEY_JAVA_CLASS_PATH)
        }

    @JvmStatic
    val javaLibraryPath: String
        @JvmName("javaLibraryPath") get() {
            return getPropertyAsNotNull(KEY_JAVA_LIBRARY_PATH)
        }

    @JvmStatic
    val javaIoTmpdir: String
        @JvmName("javaIoTmpdir") get() {
            return getPropertyAsNotNull(KEY_JAVA_IO_TMPDIR)
        }

    @JvmStatic
    val javaCompiler: String
        @JvmName("javaCompiler") get() {
            return getPropertyAsNotNull(KEY_JAVA_COMPILER)
        }

    @JvmStatic
    val javaExtDirs: String
        @JvmName("javaExtDirs") get() {
            return getPropertyAsNotNull(KEY_JAVA_EXT_DIRS)
        }

    @JvmStatic
    val osName: String
        @JvmName("osName") get() {
            return getPropertyAsNotNull(KEY_OS_NAME)
        }

    @JvmStatic
    val osArch: String
        @JvmName("osArch") get() {
            return getPropertyAsNotNull(KEY_OS_ARCH)
        }

    @JvmStatic
    val osVersion: String
        @JvmName("osVersion") get() {
            return getPropertyAsNotNull(KEY_OS_VERSION)
        }

    @JvmStatic
    val fileSeparator: String
        @JvmName("fileSeparator") get() {
            return getPropertyAsNotNull(KEY_FILE_SEPARATOR)
        }

    @JvmStatic
    val pathSeparator: String
        @JvmName("pathSeparator") get() {
            return getPropertyAsNotNull(KEY_PATH_SEPARATOR)
        }

    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return getPropertyAsNotNull(KEY_LINE_SEPARATOR)
        }

    @JvmStatic
    val userName: String
        @JvmName("userName") get() {
            return getPropertyAsNotNull(KEY_USER_NAME)
        }

    @JvmStatic
    val userHome: String
        @JvmName("userHome") get() {
            return getPropertyAsNotNull(KEY_USER_HOME)
        }

    @JvmStatic
    val userDir: String
        @JvmName("userDir") get() {
            return getPropertyAsNotNull(KEY_USER_DIR)
        }

    @JvmStatic
    val availableProcessors: Int
        @JvmName("availableProcessors") get() {
            return Runtime.getRuntime().availableProcessors()
        }

    @JvmStatic
    val properties: Map<String, String>
        @JvmName("properties") get() {
            val properties = System.getProperties() ?: return mapOf()
            return propertiesToMap(properties)
        }

    @JvmStatic
    val variables: Map<String, String>
        @JvmName("variables") get() {
            return System.getenv()
        }

    @JvmStatic
    val isOsAix: Boolean
        @JvmName("isOsAix") get() = SystemUtils.IS_OS_AIX

    @JvmStatic
    val isOsHpUx: Boolean
        @JvmName("isOsHpUx") get() = SystemUtils.IS_OS_HP_UX

    @JvmStatic
    val isOsOs400: Boolean
        @JvmName("isOsOs400") get() = SystemUtils.IS_OS_400

    @JvmStatic
    val isOsIrix: Boolean
        @JvmName("isOsIrix") get() = SystemUtils.IS_OS_IRIX

    @JvmStatic
    val isOsLinux: Boolean
        @JvmName("isOsLinux") get() = SystemUtils.IS_OS_LINUX

    @JvmStatic
    val isOsMac: Boolean
        @JvmName("isOsMac") get() = SystemUtils.IS_OS_MAC

    @JvmStatic
    val isOsMacOsX: Boolean
        @JvmName("isOsMacOsX") get() = SystemUtils.IS_OS_MAC_OSX

    @JvmStatic
    val isOsFreeBsd: Boolean
        @JvmName("isOsFreeBsd") get() = SystemUtils.IS_OS_FREE_BSD

    @JvmStatic
    val isOsOpenBsd: Boolean
        @JvmName("isOsOpenBsd") get() = SystemUtils.IS_OS_OPEN_BSD

    @JvmStatic
    val isOsNetBsd: Boolean
        @JvmName("isOsNetBsd") get() = SystemUtils.IS_OS_NET_BSD

    @JvmStatic
    val isOsOs2: Boolean
        @JvmName("isOsOs2") get() = SystemUtils.IS_OS_OS2

    @JvmStatic
    val isOsSolaris: Boolean
        @JvmName("isOsSolaris") get() = SystemUtils.IS_OS_SOLARIS

    @JvmStatic
    val isOsSunOs: Boolean
        @JvmName("isOsSunOs") get() = SystemUtils.IS_OS_SUN_OS

    @JvmStatic
    val isOsUnix: Boolean
        @JvmName("isOsUnix") get() = SystemUtils.IS_OS_UNIX

    @JvmStatic
    val isOsWindows: Boolean
        @JvmName("isOsWindows") get() = SystemUtils.IS_OS_WINDOWS

    @JvmStatic
    val javaVersionNumber: Int
        @JvmName("javaVersionNumber") get() = computeJavaVersionNumber()

    private fun computeJavaVersionNumber(): Int {
        val version = javaSpecificationVersion.split(".")
        if (version.isEmpty()) {
            return -1
        }
        val v0 = version[0].toInt()
        if (v0 >= 9) {
            return v0
        }
        if (version.size < 2) {
            return 1
        }
        return version[1].toInt()
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
    fun getVariable(key: String): String? {
        return System.getenv(key)
    }

    private fun propertiesToMap(properties: Properties): Map<String, String> {
        val map = mutableMapOf<String, String>()
        properties.forEach { k: Any?, v: Any? -> map[k.toString()] = v.toString() }
        return map
    }

    private fun getPropertyAsNotNull(key: String): String {
        return getProperty(key).asNotNull()
    }

    @JvmStatic
    fun isOs(name: String): Boolean {
        return osName.startsWith(name)
    }
}