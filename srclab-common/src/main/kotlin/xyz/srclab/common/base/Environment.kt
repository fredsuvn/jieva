package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
object Environment {

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
    fun javaVersion(): String? = getSystemProperty(JAVA_VERSION_KEY)

    @JvmStatic
    fun javaVendor(): String? = getSystemProperty(JAVA_VENDOR_KEY)

    @JvmStatic
    fun javaVendorUrl(): String? = getSystemProperty(JAVA_VENDOR_URL_KEY)

    @JvmStatic
    fun javaHome(): String? = getSystemProperty(JAVA_HOME_KEY)

    @JvmStatic
    fun javaVmSpecificationVersion(): String? = getSystemProperty(JAVA_VM_SPECIFICATION_VERSION_KEY)

    @JvmStatic
    fun javaVmSpecificationVendor(): String? = getSystemProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY)

    @JvmStatic
    fun javaVmSpecificationName(): String? = getSystemProperty(JAVA_VM_SPECIFICATION_NAME_KEY)

    @JvmStatic
    fun javaVmVersion(): String? = getSystemProperty(JAVA_VM_VERSION_KEY)

    @JvmStatic
    fun javaVmVendor(): String? = getSystemProperty(JAVA_VM_VENDOR_KEY)

    @JvmStatic
    fun javaVmName(): String? = getSystemProperty(JAVA_VM_NAME_KEY)

    @JvmStatic
    fun javaSpecificationVersion(): String? = getSystemProperty(JAVA_SPECIFICATION_VERSION_KEY)

    @JvmStatic
    fun javaSpecificationVendor(): String? = getSystemProperty(JAVA_SPECIFICATION_VENDOR_KEY)

    @JvmStatic
    fun javaSpecificationName(): String? = getSystemProperty(JAVA_SPECIFICATION_NAME_KEY)

    @JvmStatic
    fun javaClassVersion(): String? = getSystemProperty(JAVA_CLASS_VERSION_KEY)

    @JvmStatic
    fun javaClassPath(): String? = getSystemProperty(JAVA_CLASS_PATH_KEY)

    @JvmStatic
    fun javaLibraryPath(): String? = getSystemProperty(JAVA_LIBRARY_PATH_KEY)

    @JvmStatic
    fun javaIoTmpdir(): String? = getSystemProperty(JAVA_IO_TMPDIR_KEY)

    @JvmStatic
    fun javaCompiler(): String? = getSystemProperty(JAVA_COMPILER_KEY)

    @JvmStatic
    fun javaExtDirs(): String? = getSystemProperty(JAVA_EXT_DIRS_KEY)

    @JvmStatic
    fun osName(): String? = getSystemProperty(OS_NAME_KEY)

    @JvmStatic
    fun osArch(): String? = getSystemProperty(OS_ARCH_KEY)

    @JvmStatic
    fun osVersion(): String? = getSystemProperty(OS_VERSION_KEY)

    @JvmStatic
    fun fileSeparator(): String? = getSystemProperty(FILE_SEPARATOR_KEY)

    @JvmStatic
    fun pathSeparator(): String? = getSystemProperty(PATH_SEPARATOR_KEY)

    @JvmStatic
    fun lineSeparator(): String? = getSystemProperty(LINE_SEPARATOR_KEY)

    @JvmStatic
    fun userName(): String? = getSystemProperty(USER_NAME_KEY)

    @JvmStatic
    fun userHome(): String? = getSystemProperty(USER_HOME_KEY)

    @JvmStatic
    fun userDir(): String? = getSystemProperty(USER_DIR_KEY)

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
}