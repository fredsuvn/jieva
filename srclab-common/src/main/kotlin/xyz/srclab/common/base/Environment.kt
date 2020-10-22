package xyz.srclab.common.base

import java.util.*

/**
 * @author sunqian
 */
interface Environment {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val properties: Map<String, String>
        @JvmName("properties") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val variables: Map<String, String>
        @JvmName("variables") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVersion: String
        @JvmName("javaVersion") get() {
            return getProperty(KEY_OF_JAVA_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVendor: String
        @JvmName("javaVendor") get() {
            return getProperty(KEY_OF_JAVA_VENDOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVendorUrl: String
        @JvmName("javaVendorUrl") get() {
            return getProperty(KEY_OF_JAVA_VENDOR_URL).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaHome: String
        @JvmName("javaHome") get() {
            return getProperty(KEY_OF_JAVA_HOME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationVersion: String
        @JvmName("javaVmSpecificationVersion") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationVendor: String
        @JvmName("javaVmSpecificationVendor") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationName: String
        @JvmName("javaVmSpecificationName") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_NAME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmVersion: String
        @JvmName("javaVmVersion") get() {
            return getProperty(KEY_OF_JAVA_VM_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmVendor: String
        @JvmName("javaVmVendor") get() {
            return getProperty(KEY_OF_JAVA_VM_VENDOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaVmName: String
        @JvmName("javaVmName") get() {
            return getProperty(KEY_OF_JAVA_VM_NAME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaSpecificationVersion: String
        @JvmName("javaSpecificationVersion") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaSpecificationVendor: String
        @JvmName("javaSpecificationVendor") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_VENDOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaSpecificationName: String
        @JvmName("javaSpecificationName") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_NAME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaClassVersion: String
        @JvmName("javaClassVersion") get() {
            return getProperty(KEY_OF_JAVA_CLASS_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaClassPath: String
        @JvmName("javaClassPath") get() {
            return getProperty(KEY_OF_JAVA_CLASS_PATH).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaLibraryPath: String
        @JvmName("javaLibraryPath") get() {
            return getProperty(KEY_OF_JAVA_LIBRARY_PATH).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaIoTmpdir: String
        @JvmName("javaIoTmpdir") get() {
            return getProperty(KEY_OF_JAVA_IO_TMPDIR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaCompiler: String
        @JvmName("javaCompiler") get() {
            return getProperty(KEY_OF_JAVA_COMPILER).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val javaExtDirs: String
        @JvmName("javaExtDirs") get() {
            return getProperty(KEY_OF_JAVA_EXT_DIRS).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val osName: String
        @JvmName("osName") get() {
            return getProperty(KEY_OF_OS_NAME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val osArch: String
        @JvmName("osArch") get() {
            return getProperty(KEY_OF_OS_ARCH).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val osVersion: String
        @JvmName("osVersion") get() {
            return getProperty(KEY_OF_OS_VERSION).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val fileSeparator: String
        @JvmName("fileSeparator") get() {
            return getProperty(KEY_OF_FILE_SEPARATOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val pathSeparator: String
        @JvmName("pathSeparator") get() {
            return getProperty(KEY_OF_PATH_SEPARATOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return getProperty(KEY_OF_LINE_SEPARATOR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val userName: String
        @JvmName("userName") get() {
            return getProperty(KEY_OF_USER_NAME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val userHome: String
        @JvmName("userHome") get() {
            return getProperty(KEY_OF_USER_HOME).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val userDir: String
        @JvmName("userDir") get() {
            return getProperty(KEY_OF_USER_DIR).asNotNull()
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    val availableProcessors: Int
        @JvmName("availableProcessors") get() {
            return Runtime.getRuntime().availableProcessors()
        }

    fun getProperty(key: String): String?

    fun setProperty(key: String, value: String)

    fun getVariable(key: String): String?

    companion object {

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
        fun defaultEnvironment(): Environment {
            return DefaultEnvironment
        }
    }
}

fun defaultEnvironment(): Environment {
    return Environment.defaultEnvironment()
}

object DefaultEnvironment : Environment {

    @Suppress(INAPPLICABLE_JVM_NAME)
    override val properties: Map<String, String>
        @JvmName("properties") get() {
            val properties = System.getProperties() ?: return mapOf()
            return propertiesToMap(properties)
        }

    @Suppress(INAPPLICABLE_JVM_NAME)
    override val variables: Map<String, String>
        @JvmName("variables") get() {
            return System.getenv()
        }

    override fun getProperty(key: String): String? {
        return System.getProperty(key)
    }

    override fun setProperty(key: String, value: String) {
        System.setProperty(key, value)
    }

    override fun getVariable(key: String): String? {
        return System.getenv(key)
    }

    private fun propertiesToMap(properties: Properties): Map<String, String> {
        val map = mutableMapOf<String, String>()
        properties.forEach { k: Any?, v: Any? -> map[k.toString()] = v.toString() }
        return map
    }

    private fun getPropertyNotNull(key: String): String {
        return getProperty(key).asNotNull()
    }
}