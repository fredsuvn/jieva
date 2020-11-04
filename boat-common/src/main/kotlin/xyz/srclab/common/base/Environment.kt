package xyz.srclab.common.base

import xyz.srclab.kotlin.compile.COMPILE_INAPPLICABLE_JVM_NAME
import java.util.*

/**
 * @author sunqian
 */
interface Environment {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val properties: Map<String, String>
        @JvmName("properties") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val variables: Map<String, String>
        @JvmName("variables") get

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVersion: String
        @JvmName("javaVersion") get() {
            return getProperty(KEY_OF_JAVA_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVendor: String
        @JvmName("javaVendor") get() {
            return getProperty(KEY_OF_JAVA_VENDOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVendorUrl: String
        @JvmName("javaVendorUrl") get() {
            return getProperty(KEY_OF_JAVA_VENDOR_URL).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaHome: String
        @JvmName("javaHome") get() {
            return getProperty(KEY_OF_JAVA_HOME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationVersion: String
        @JvmName("javaVmSpecificationVersion") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationVendor: String
        @JvmName("javaVmSpecificationVendor") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_VENDOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmSpecificationName: String
        @JvmName("javaVmSpecificationName") get() {
            return getProperty(KEY_OF_JAVA_VM_SPECIFICATION_NAME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmVersion: String
        @JvmName("javaVmVersion") get() {
            return getProperty(KEY_OF_JAVA_VM_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmVendor: String
        @JvmName("javaVmVendor") get() {
            return getProperty(KEY_OF_JAVA_VM_VENDOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaVmName: String
        @JvmName("javaVmName") get() {
            return getProperty(KEY_OF_JAVA_VM_NAME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaSpecificationVersion: String
        @JvmName("javaSpecificationVersion") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaSpecificationVendor: String
        @JvmName("javaSpecificationVendor") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_VENDOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaSpecificationName: String
        @JvmName("javaSpecificationName") get() {
            return getProperty(KEY_OF_JAVA_SPECIFICATION_NAME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaClassVersion: String
        @JvmName("javaClassVersion") get() {
            return getProperty(KEY_OF_JAVA_CLASS_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaClassPath: String
        @JvmName("javaClassPath") get() {
            return getProperty(KEY_OF_JAVA_CLASS_PATH).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaLibraryPath: String
        @JvmName("javaLibraryPath") get() {
            return getProperty(KEY_OF_JAVA_LIBRARY_PATH).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaIoTmpdir: String
        @JvmName("javaIoTmpdir") get() {
            return getProperty(KEY_OF_JAVA_IO_TMPDIR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaCompiler: String
        @JvmName("javaCompiler") get() {
            return getProperty(KEY_OF_JAVA_COMPILER).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val javaExtDirs: String
        @JvmName("javaExtDirs") get() {
            return getProperty(KEY_OF_JAVA_EXT_DIRS).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val osName: String
        @JvmName("osName") get() {
            return getProperty(KEY_OF_OS_NAME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val osArch: String
        @JvmName("osArch") get() {
            return getProperty(KEY_OF_OS_ARCH).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val osVersion: String
        @JvmName("osVersion") get() {
            return getProperty(KEY_OF_OS_VERSION).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val fileSeparator: String
        @JvmName("fileSeparator") get() {
            return getProperty(KEY_OF_FILE_SEPARATOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val pathSeparator: String
        @JvmName("pathSeparator") get() {
            return getProperty(KEY_OF_PATH_SEPARATOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val lineSeparator: String
        @JvmName("lineSeparator") get() {
            return getProperty(KEY_OF_LINE_SEPARATOR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val userName: String
        @JvmName("userName") get() {
            return getProperty(KEY_OF_USER_NAME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val userHome: String
        @JvmName("userHome") get() {
            return getProperty(KEY_OF_USER_HOME).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val userDir: String
        @JvmName("userDir") get() {
            return getProperty(KEY_OF_USER_DIR).asNotNull()
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    val availableProcessors: Int
        @JvmName("availableProcessors") get() {
            return Runtime.getRuntime().availableProcessors()
        }

    fun getProperty(key: String): String?

    fun setProperty(key: String, value: String)

    fun getVariable(key: String): String?

    companion object {

        @JvmField
        val KEY_OF_JAVA_VERSION = "java.version"

        @JvmField
        val KEY_OF_JAVA_VENDOR = "java.vendor"

        @JvmField
        val KEY_OF_JAVA_VENDOR_URL = "java.vendor.url"

        @JvmField
        val KEY_OF_JAVA_HOME = "java.home"

        @JvmField
        val KEY_OF_JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version"

        @JvmField
        val KEY_OF_JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor"

        @JvmField
        val KEY_OF_JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name"

        @JvmField
        val KEY_OF_JAVA_VM_VERSION = "java.vm.version"

        @JvmField
        val KEY_OF_JAVA_VM_VENDOR = "java.vm.vendor"

        @JvmField
        val KEY_OF_JAVA_VM_NAME = "java.vm.name"

        @JvmField
        val KEY_OF_JAVA_SPECIFICATION_VERSION = "java.specification.version"

        @JvmField
        val KEY_OF_JAVA_SPECIFICATION_VENDOR = "java.specification.vendor"

        @JvmField
        val KEY_OF_JAVA_SPECIFICATION_NAME = "java.specification.name"

        @JvmField
        val KEY_OF_JAVA_CLASS_VERSION = "java.class.version"

        @JvmField
        val KEY_OF_JAVA_CLASS_PATH = "java.class.path"

        @JvmField
        val KEY_OF_JAVA_LIBRARY_PATH = "java.library.path"

        @JvmField
        val KEY_OF_JAVA_IO_TMPDIR = "java.io.tmpdir"

        @JvmField
        val KEY_OF_JAVA_COMPILER = "java.compiler"

        @JvmField
        val KEY_OF_JAVA_EXT_DIRS = "java.ext.dirs"

        @JvmField
        val KEY_OF_OS_NAME = "os.name"

        @JvmField
        val KEY_OF_OS_ARCH = "os.arch"

        @JvmField
        val KEY_OF_OS_VERSION = "os.version"

        @JvmField
        val KEY_OF_FILE_SEPARATOR = "file.separator"

        @JvmField
        val KEY_OF_PATH_SEPARATOR = "path.separator"

        @JvmField
        val KEY_OF_LINE_SEPARATOR = "line.separator"

        @JvmField
        val KEY_OF_USER_NAME = "user.name"

        @JvmField
        val KEY_OF_USER_HOME = "user.home"

        @JvmField
        val KEY_OF_USER_DIR = "user.dir"

        @JvmField
        val DEFAULT: Environment = DefaultEnvironment
    }
}

object DefaultEnvironment : Environment {

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
    override val properties: Map<String, String>
        @JvmName("properties") get() {
            val properties = System.getProperties() ?: return mapOf()
            return propertiesToMap(properties)
        }

    @Suppress(COMPILE_INAPPLICABLE_JVM_NAME)
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
}