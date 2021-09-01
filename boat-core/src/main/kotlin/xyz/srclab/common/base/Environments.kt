package xyz.srclab.common.base

import org.apache.commons.lang3.SystemUtils
import xyz.srclab.common.lang.toInt
import java.util.*

/**
 * Environment infos.
 *
 * @author sunqian
 */
object Environments {

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

    @JvmStatic
    val javaVersion: String
        @JvmName("javaVersion") get() {
            return getProperty(JAVA_VERSION_KEY)
        }

    @JvmStatic
    val javaVendor: String
        @JvmName("javaVendor") get() {
            return getProperty(JAVA_VENDOR_KEY)
        }

    @JvmStatic
    val javaVendorUrl: String
        @JvmName("javaVendorUrl") get() {
            return getProperty(JAVA_VENDOR_URL_KEY)
        }

    @JvmStatic
    val javaHome: String
        @JvmName("javaHome") get() {
            return getProperty(JAVA_HOME_KEY)
        }

    @JvmStatic
    val javaVmSpecificationVersion: String
        @JvmName("javaVmSpecificationVersion") get() {
            return getProperty(JAVA_VM_SPECIFICATION_VERSION_KEY)
        }

    @JvmStatic
    val javaVmSpecificationVendor: String
        @JvmName("javaVmSpecificationVendor") get() {
            return getProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY)
        }

    @JvmStatic
    val javaVmSpecificationName: String
        @JvmName("javaVmSpecificationName") get() {
            return getProperty(JAVA_VM_SPECIFICATION_NAME_KEY)
        }

    @JvmStatic
    val javaVmVersion: String
        @JvmName("javaVmVersion") get() {
            return getProperty(JAVA_VM_VERSION_KEY)
        }

    @JvmStatic
    val javaVmVendor: String
        @JvmName("javaVmVendor") get() {
            return getProperty(JAVA_VM_VENDOR_KEY)
        }

    @JvmStatic
    val javaVmName: String
        @JvmName("javaVmName") get() {
            return getProperty(JAVA_VM_NAME_KEY)
        }

    @JvmStatic
    val javaSpecificationVersion: String
        @JvmName("javaSpecificationVersion") get() {
            return getProperty(JAVA_SPECIFICATION_VERSION_KEY)
        }

    @JvmStatic
    val javaSpecificationVendor: String
        @JvmName("javaSpecificationVendor") get() {
            return getProperty(JAVA_SPECIFICATION_VENDOR_KEY)
        }

    @JvmStatic
    val javaSpecificationName: String
        @JvmName("javaSpecificationName") get() {
            return getProperty(JAVA_SPECIFICATION_NAME_KEY)
        }

    @JvmStatic
    val javaClassVersion: String
        @JvmName("javaClassVersion") get() {
            return getProperty(JAVA_CLASS_VERSION_KEY)
        }

    @JvmStatic
    val javaClassPath: String
        @JvmName("javaClassPath") get() {
            return getProperty(JAVA_CLASS_PATH_KEY)
        }

    @JvmStatic
    val javaLibraryPath: String
        @JvmName("javaLibraryPath") get() {
            return getProperty(JAVA_LIBRARY_PATH_KEY)
        }

    @JvmStatic
    val javaIoTmpdir: String
        @JvmName("javaIoTmpdir") get() {
            return getProperty(JAVA_IO_TMPDIR_KEY)
        }

    @JvmStatic
    val javaCompiler: String
        @JvmName("javaCompiler") get() {
            return getProperty(JAVA_COMPILER_KEY)
        }

    @JvmStatic
    val javaExtDirs: String
        @JvmName("javaExtDirs") get() {
            return getProperty(JAVA_EXT_DIRS_KEY)
        }

    @JvmStatic
    val osName: String
        @JvmName("osName") get() {
            return getProperty(OS_NAME_KEY)
        }

    @JvmStatic
    val osArch: String
        @JvmName("osArch") get() {
            return getProperty(OS_ARCH_KEY)
        }

    @JvmStatic
    val osVersion: String
        @JvmName("osVersion") get() {
            return getProperty(OS_VERSION_KEY)
        }

    @JvmStatic
    val fileSeparator: String
        @JvmName("fileSeparator") get() {
            return getProperty(FILE_SEPARATOR_KEY)
        }

    @JvmStatic
    val pathSeparator: String
        @JvmName("pathSeparator") get() {
            return getProperty(PATH_SEPARATOR_KEY)
        }

    @JvmStatic
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return getProperty(LINE_SEPARATOR_KEY)
        }

    @JvmStatic
    val userName: String
        @JvmName("userName") get() {
            return getProperty(USER_NAME_KEY)
        }

    @JvmStatic
    val userHome: String
        @JvmName("userHome") get() {
            return getProperty(USER_HOME_KEY)
        }

    @JvmStatic
    val userDir: String
        @JvmName("userDir") get() {
            return getProperty(USER_DIR_KEY)
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

    @Throws(NullPointerException::class)
    @JvmStatic
    fun getProperty(key: String): String {
        return getPropertyOrNull(key)!!
    }

    @JvmStatic
    fun getPropertyOrNull(key: String): String? {
        return System.getProperty(key)
    }

    @JvmStatic
    fun setProperty(key: String, value: String) {
        System.setProperty(key, value)
    }

    @Throws(NullPointerException::class)
    @JvmStatic
    fun getEnvironmentVariable(key: String): String {
        return getEnvironmentVariableOrNull(key)!!
    }

    @JvmStatic
    fun getEnvironmentVariableOrNull(key: String): String? {
        return System.getenv(key)
    }

    @JvmStatic
    fun isOs(name: String): Boolean {
        return osName.startsWith(name)
    }

    private fun computeJavaVersionNumber(): Int {
        val version = javaSpecificationVersion.split(".")
        if (version.isEmpty()) {
            return -1
        }
        val v0 = version[0].toInt()
        if (v0 >= 9 || version.size < 2) {
            return v0
        }
        return version[1].toInt()
    }

    private fun propertiesToMap(properties: Properties): Map<String, String> {
        val map = mutableMapOf<String, String>()
        properties.forEach { k: Any?, v: Any? ->
            if (k !== null) {
                map[k.toString()] = v.toString()
            }
        }
        return map
    }
}