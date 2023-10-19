package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.collect.FsCollect;

import java.util.Map;

/**
 * System utilities
 *
 * @author fredsuvn
 */
public class FsSystem {

    /**
     * Key of system property: java.version.
     */
    public static final String JAVA_VERSION_KEY = "java.version";
    /**
     * Key of system property: java.vendor.
     */
    public static final String JAVA_VENDOR_KEY = "java.vendor";
    /**
     * Key of system property: java.vendor.url.
     */
    public static final String JAVA_VENDOR_URL_KEY = "java.vendor.url";
    /**
     * Key of system property: java.home.
     */
    public static final String JAVA_HOME_KEY = "java.home";
    /**
     * Key of system property: java.vm.specification.version.
     */
    public static final String JAVA_VM_SPECIFICATION_VERSION_KEY = "java.vm.specification.version";
    /**
     * Key of system property: java.vm.specification.vendor.
     */
    public static final String JAVA_VM_SPECIFICATION_VENDOR_KEY = "java.vm.specification.vendor";
    /**
     * Key of system property: java.vm.specification.name.
     */
    public static final String JAVA_VM_SPECIFICATION_NAME_KEY = "java.vm.specification.name";
    /**
     * Key of system property: java.vm.version.
     */
    public static final String JAVA_VM_VERSION_KEY = "java.vm.version";
    /**
     * Key of system property: java.vm.vendor.
     */
    public static final String JAVA_VM_VENDOR_KEY = "java.vm.vendor";
    /**
     * Key of system property: java.vm.name.
     */
    public static final String JAVA_VM_NAME_KEY = "java.vm.name";
    /**
     * Key of system property: java.specification.version.
     */
    public static final String JAVA_SPECIFICATION_VERSION_KEY = "java.specification.version";
    /**
     * Key of system property: java.specification.vendor.
     */
    public static final String JAVA_SPECIFICATION_VENDOR_KEY = "java.specification.vendor";
    /**
     * Key of system property: java.specification.name.
     */
    public static final String JAVA_SPECIFICATION_NAME_KEY = "java.specification.name";
    /**
     * Key of system property: java.class.version.
     */
    public static final String JAVA_CLASS_VERSION_KEY = "java.class.version";
    /**
     * Key of system property: java.class.path.
     */
    public static final String JAVA_CLASS_PATH_KEY = "java.class.path";
    /**
     * Key of system property: java.library.path.
     */
    public static final String JAVA_LIBRARY_PATH_KEY = "java.library.path";
    /**
     * Key of system property: java.io.tmpdir.
     */
    public static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    /**
     * Key of system property: java.compiler.
     */
    public static final String JAVA_COMPILER_KEY = "java.compiler";
    /**
     * Key of system property: java.ext.dirs.
     */
    public static final String JAVA_EXT_DIRS_KEY = "java.ext.dirs";
    /**
     * Key of system property: os.name.
     */
    public static final String OS_NAME_KEY = "os.name";
    /**
     * Key of system property: os.arch.
     */
    public static final String OS_ARCH_KEY = "os.arch";
    /**
     * Key of system property: os.version.
     */
    public static final String OS_VERSION_KEY = "os.version";
    /**
     * Key of system property: file.separator.
     */
    public static final String FILE_SEPARATOR_KEY = "file.separator";
    /**
     * Key of system property: path.separator.
     */
    public static final String PATH_SEPARATOR_KEY = "path.separator";
    /**
     * Key of system property: line.separator.
     */
    public static final String LINE_SEPARATOR_KEY = "line.separator";
    /**
     * Key of system property: user.name.
     */
    public static final String USER_NAME_KEY = "user.name";
    /**
     * Key of system property: user.home.
     */
    public static final String USER_HOME_KEY = "user.home";
    /**
     * Key of system property: user.dir.
     */
    public static final String USER_DIR_KEY = "user.dir";
    /**
     * Key of system property: file.encoding.
     */
    public static final String FILE_ENCODING_KEY = "file.encoding";

    //Add on JDK17:
    /**
     * Key of system property: native.encoding.
     */
    public static final String NATIVE_ENCODING_KEY = "native.encoding";

    /**
     * Returns system property of {@link #JAVA_VERSION_KEY}.
     *
     * @return system property of {@link #JAVA_VERSION_KEY}
     */
    @Nullable
    public static String getJavaVersion() {
        return System.getProperty(JAVA_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VENDOR_KEY}.
     *
     * @return system property of {@link #JAVA_VENDOR_KEY}
     */
    @Nullable
    public static String getJavaVendor() {
        return System.getProperty(JAVA_VENDOR_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VENDOR_URL_KEY}.
     *
     * @return system property of {@link #JAVA_VENDOR_URL_KEY}
     */
    @Nullable
    public static String getJavaVendorUrl() {
        return System.getProperty(JAVA_VENDOR_URL_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_HOME_KEY}.
     *
     * @return system property of {@link #JAVA_HOME_KEY}
     */
    @Nullable
    public static String getJavaHome() {
        return System.getProperty(JAVA_HOME_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_SPECIFICATION_VERSION_KEY}.
     *
     * @return system property of {@link #JAVA_VM_SPECIFICATION_VERSION_KEY}
     */
    @Nullable
    public static String getJavaVmSpecificationVersion() {
        return System.getProperty(JAVA_VM_SPECIFICATION_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_SPECIFICATION_VENDOR_KEY}.
     *
     * @return system property of {@link #JAVA_VM_SPECIFICATION_VENDOR_KEY}
     */
    @Nullable
    public static String getJavaVmSpecificationVendor() {
        return System.getProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_SPECIFICATION_NAME_KEY}.
     *
     * @return system property of {@link #JAVA_VM_SPECIFICATION_NAME_KEY}
     */
    @Nullable
    public static String getJavaVmSpecificationName() {
        return System.getProperty(JAVA_VM_SPECIFICATION_NAME_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_VERSION_KEY}.
     *
     * @return system property of {@link #JAVA_VM_VERSION_KEY}
     */
    @Nullable
    public static String getJavaVmVersion() {
        return System.getProperty(JAVA_VM_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_VENDOR_KEY}.
     *
     * @return system property of {@link #JAVA_VM_VENDOR_KEY}
     */
    @Nullable
    public static String getJavaVmVendor() {
        return System.getProperty(JAVA_VM_VENDOR_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_VM_NAME_KEY}.
     *
     * @return system property of {@link #JAVA_VM_NAME_KEY}
     */
    @Nullable
    public static String getJavaVmName() {
        return System.getProperty(JAVA_VM_NAME_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_SPECIFICATION_VERSION_KEY}.
     *
     * @return system property of {@link #JAVA_SPECIFICATION_VERSION_KEY}
     */
    @Nullable
    public static String getJavaSpecificationVersion() {
        return System.getProperty(JAVA_SPECIFICATION_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_SPECIFICATION_VENDOR_KEY}.
     *
     * @return system property of {@link #JAVA_SPECIFICATION_VENDOR_KEY}
     */
    @Nullable
    public static String getJavaSpecificationVendor() {
        return System.getProperty(JAVA_SPECIFICATION_VENDOR_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_SPECIFICATION_NAME_KEY}.
     *
     * @return system property of {@link #JAVA_SPECIFICATION_NAME_KEY}
     */
    @Nullable
    public static String getJavaSpecificationName() {
        return System.getProperty(JAVA_SPECIFICATION_NAME_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_CLASS_VERSION_KEY}.
     *
     * @return system property of {@link #JAVA_CLASS_VERSION_KEY}
     */
    @Nullable
    public static String getJavaClassVersion() {
        return System.getProperty(JAVA_CLASS_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_CLASS_PATH_KEY}.
     *
     * @return system property of {@link #JAVA_CLASS_PATH_KEY}
     */
    @Nullable
    public static String getJavaClassPath() {
        return System.getProperty(JAVA_CLASS_PATH_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_LIBRARY_PATH_KEY}.
     *
     * @return system property of {@link #JAVA_LIBRARY_PATH_KEY}
     */
    @Nullable
    public static String getJavaLibraryPath() {
        return System.getProperty(JAVA_LIBRARY_PATH_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_IO_TMPDIR_KEY}.
     *
     * @return system property of {@link #JAVA_IO_TMPDIR_KEY}
     */
    @Nullable
    public static String getJavaIoTmpdir() {
        return System.getProperty(JAVA_IO_TMPDIR_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_COMPILER_KEY}.
     *
     * @return system property of {@link #JAVA_COMPILER_KEY}
     */
    @Nullable
    public static String getJavaCompiler() {
        return System.getProperty(JAVA_COMPILER_KEY);
    }

    /**
     * Returns system property of {@link #JAVA_EXT_DIRS_KEY}.
     *
     * @return system property of {@link #JAVA_EXT_DIRS_KEY}
     */
    @Nullable
    public static String getJavaExtDirs() {
        return System.getProperty(JAVA_EXT_DIRS_KEY);
    }

    /**
     * Returns system property of {@link #OS_NAME_KEY}.
     *
     * @return system property of {@link #OS_NAME_KEY}
     */
    @Nullable
    public static String getOsName() {
        return System.getProperty(OS_NAME_KEY);
    }

    /**
     * Returns system property of {@link #OS_ARCH_KEY}.
     *
     * @return system property of {@link #OS_ARCH_KEY}
     */
    @Nullable
    public static String getOsArch() {
        return System.getProperty(OS_ARCH_KEY);
    }

    /**
     * Returns system property of {@link #OS_VERSION_KEY}.
     *
     * @return system property of {@link #OS_VERSION_KEY}
     */
    @Nullable
    public static String getOsVersion() {
        return System.getProperty(OS_VERSION_KEY);
    }

    /**
     * Returns system property of {@link #FILE_SEPARATOR_KEY}.
     *
     * @return system property of {@link #FILE_SEPARATOR_KEY}
     */
    @Nullable
    public static String getFileSeparator() {
        return System.getProperty(FILE_SEPARATOR_KEY);
    }

    /**
     * Returns system property of {@link #PATH_SEPARATOR_KEY}.
     *
     * @return system property of {@link #PATH_SEPARATOR_KEY}
     */
    @Nullable
    public static String getPathSeparator() {
        return System.getProperty(PATH_SEPARATOR_KEY);
    }

    /**
     * Returns system property of {@link #LINE_SEPARATOR_KEY}.
     *
     * @return system property of {@link #LINE_SEPARATOR_KEY}
     */
    @Nullable
    public static String getLineSeparator() {
        return System.getProperty(LINE_SEPARATOR_KEY);
    }

    /**
     * Returns system property of {@link #USER_NAME_KEY}.
     *
     * @return system property of {@link #USER_NAME_KEY}
     */
    @Nullable
    public static String getUserName() {
        return System.getProperty(USER_NAME_KEY);
    }

    /**
     * Returns system property of {@link #USER_HOME_KEY}.
     *
     * @return system property of {@link #USER_HOME_KEY}
     */
    @Nullable
    public static String getUserHome() {
        return System.getProperty(USER_HOME_KEY);
    }

    /**
     * Returns system property of {@link #USER_DIR_KEY}.
     *
     * @return system property of {@link #USER_DIR_KEY}
     */
    @Nullable
    public static String getUserDir() {
        return System.getProperty(USER_DIR_KEY);
    }

    /**
     * Returns system property of {@link #FILE_ENCODING_KEY}.
     *
     * @return system property of {@link #FILE_ENCODING_KEY}
     */
    @Nullable
    public static String getFileEncoding() {
        return System.getProperty(FILE_ENCODING_KEY);
    }

    /**
     * Returns system property of {@link #NATIVE_ENCODING_KEY}.
     *
     * @return system property of {@link #NATIVE_ENCODING_KEY}
     */
    @Nullable
    public static String getNativeEncoding() {
        return System.getProperty(NATIVE_ENCODING_KEY);
    }

    /**
     * Reads all system properties into a new map and returns.
     *
     * @return all system properties
     */
    public static Map<String, String> getProperties() {
        return FsCollect.toMap(System.getProperties());
    }

    /**
     * Returns whether current OS is Windows.
     *
     * @return whether current OS is Windows
     */
    public static boolean isWindows() {
        String osName = getOsName();
        return osName != null && osName.startsWith("Windows");
    }

    /**
     * Returns whether current OS is Linux.
     *
     * @return whether current OS is Linux
     */
    public static boolean isLinux() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Linux") || osName.startsWith("LINUX"));
    }

    /**
     * Returns whether current OS is Mac.
     *
     * @return whether current OS is Mac
     */
    public static boolean isMac() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Mac"));
    }

    /**
     * Returns whether current OS is BSD (FreeBSD, OpenBSD or NetBSD).
     *
     * @return whether current OS is BSD (FreeBSD, OpenBSD or NetBSD)
     */
    public static boolean isBsd() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("FreeBSD") || osName.startsWith("OpenBSD") || osName.startsWith("NetBSD"));
    }

    /**
     * Gets java major version.
     * <p>
     * If java version &lt;= 1.8, return second version number such as
     * {@code 6} for {@code 1.6.x}, {@code 8} for {@code 1.8.x}.
     * Else return first number such as {@code 9} for {@code 9.0}.
     * <p>
     * Return -1 if obtain failed.
     *
     * @return java major version
     */
    public static int javaMajorVersion() {
        String javaVersion = getJavaVersion();
        if (javaVersion == null) {
            return -1;
        }
        int dotIndex = FsString.indexOf(javaVersion, ".");
        if (dotIndex <= 0) {
            return Integer.parseInt(javaVersion);
        }
        int versionFirst = Integer.parseInt(javaVersion.substring(0, dotIndex));
        if (versionFirst >= 9) {
            return versionFirst;
        }
        if (versionFirst == 1) {
            int nextDotIndex = FsString.indexOf(javaVersion, ".", dotIndex + 1);
            if (nextDotIndex < 0) {
                return -1;
            }
            return Integer.parseInt(javaVersion.substring(dotIndex + 1, nextDotIndex));
        }
        return -1;
    }

    /**
     * Returns whether current JDK version &gt;= 9.
     *
     * @return whether current JDK version &gt;= 9
     */
    public static boolean isJdk9OrHigher() {
        return javaMajorVersion() >= 9;
    }
}
