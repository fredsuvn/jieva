package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.collect.FsCollect;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * System utilities
 *
 * @author fredsuvn
 */
public class FsSystem {

    public static final String JAVA_VERSION_KEY = "java.version";
    public static final String JAVA_VENDOR_KEY = "java.vendor";
    public static final String JAVA_VENDOR_URL_KEY = "java.vendor.url";
    public static final String JAVA_HOME_KEY = "java.home";
    public static final String JAVA_VM_SPECIFICATION_VERSION_KEY = "java.vm.specification.version";
    public static final String JAVA_VM_SPECIFICATION_VENDOR_KEY = "java.vm.specification.vendor";
    public static final String JAVA_VM_SPECIFICATION_NAME_KEY = "java.vm.specification.name";
    public static final String JAVA_VM_VERSION_KEY = "java.vm.version";
    public static final String JAVA_VM_VENDOR_KEY = "java.vm.vendor";
    public static final String JAVA_VM_NAME_KEY = "java.vm.name";
    public static final String JAVA_SPECIFICATION_VERSION_KEY = "java.specification.version";
    public static final String JAVA_SPECIFICATION_VENDOR_KEY = "java.specification.vendor";
    public static final String JAVA_SPECIFICATION_NAME_KEY = "java.specification.name";
    public static final String JAVA_CLASS_VERSION_KEY = "java.class.version";
    public static final String JAVA_CLASS_PATH_KEY = "java.class.path";
    public static final String JAVA_LIBRARY_PATH_KEY = "java.library.path";
    public static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    public static final String JAVA_COMPILER_KEY = "java.compiler";
    public static final String JAVA_EXT_DIRS_KEY = "java.ext.dirs";
    public static final String OS_NAME_KEY = "os.name";
    public static final String OS_ARCH_KEY = "os.arch";
    public static final String OS_VERSION_KEY = "os.version";
    public static final String FILE_SEPARATOR_KEY = "file.separator";
    public static final String PATH_SEPARATOR_KEY = "path.separator";
    public static final String LINE_SEPARATOR_KEY = "line.separator";
    public static final String USER_NAME_KEY = "user.name";
    public static final String USER_HOME_KEY = "user.home";
    public static final String USER_DIR_KEY = "user.dir";
    public static final String FILE_ENCODING_KEY = "file.encoding";

    //Add on JDK17:
    public static final String NATIVE_ENCODING_KEY = "native.encoding";

    private static Charset nativeCharset = null;

    @Nullable
    public static String getJavaVersion() {
        return System.getProperty(JAVA_VERSION_KEY);
    }

    @Nullable
    public static String getJavaVendor() {
        return System.getProperty(JAVA_VENDOR_KEY);
    }

    @Nullable
    public static String getJavaVendorUrl() {
        return System.getProperty(JAVA_VENDOR_URL_KEY);
    }

    @Nullable
    public static String getJavaHome() {
        return System.getProperty(JAVA_HOME_KEY);
    }

    @Nullable
    public static String getJavaVmSpecificationVersion() {
        return System.getProperty(JAVA_VM_SPECIFICATION_VERSION_KEY);
    }

    @Nullable
    public static String getJavaVmSpecificationVendor() {
        return System.getProperty(JAVA_VM_SPECIFICATION_VENDOR_KEY);
    }

    @Nullable
    public static String getJavaVmSpecificationName() {
        return System.getProperty(JAVA_VM_SPECIFICATION_NAME_KEY);
    }

    @Nullable
    public static String getJavaVmVersion() {
        return System.getProperty(JAVA_VM_VERSION_KEY);
    }

    @Nullable
    public static String getJavaVmVendor() {
        return System.getProperty(JAVA_VM_VENDOR_KEY);
    }

    @Nullable
    public static String getJavaVmName() {
        return System.getProperty(JAVA_VM_NAME_KEY);
    }

    @Nullable
    public static String getJavaSpecificationVersion() {
        return System.getProperty(JAVA_SPECIFICATION_VERSION_KEY);
    }

    @Nullable
    public static String getJavaSpecificationVendor() {
        return System.getProperty(JAVA_SPECIFICATION_VENDOR_KEY);
    }

    @Nullable
    public static String getJavaSpecificationName() {
        return System.getProperty(JAVA_SPECIFICATION_NAME_KEY);
    }

    @Nullable
    public static String getJavaClassVersion() {
        return System.getProperty(JAVA_CLASS_VERSION_KEY);
    }

    @Nullable
    public static String getJavaClassPath() {
        return System.getProperty(JAVA_CLASS_PATH_KEY);
    }

    @Nullable
    public static String getJavaLibraryPath() {
        return System.getProperty(JAVA_LIBRARY_PATH_KEY);
    }

    @Nullable
    public static String getJavaIoTmpdir() {
        return System.getProperty(JAVA_IO_TMPDIR_KEY);
    }

    @Nullable
    public static String getJavaCompiler() {
        return System.getProperty(JAVA_COMPILER_KEY);
    }

    @Nullable
    public static String getJavaExtDirs() {
        return System.getProperty(JAVA_EXT_DIRS_KEY);
    }

    @Nullable
    public static String getOsName() {
        return System.getProperty(OS_NAME_KEY);
    }

    @Nullable
    public static String getOsArch() {
        return System.getProperty(OS_ARCH_KEY);
    }

    @Nullable
    public static String getOsVersion() {
        return System.getProperty(OS_VERSION_KEY);
    }

    @Nullable
    public static String getFileSeparator() {
        return System.getProperty(FILE_SEPARATOR_KEY);
    }

    @Nullable
    public static String getPathSeparator() {
        return System.getProperty(PATH_SEPARATOR_KEY);
    }

    @Nullable
    public static String getLineSeparator() {
        return System.getProperty(LINE_SEPARATOR_KEY);
    }

    @Nullable
    public static String getUserName() {
        return System.getProperty(USER_NAME_KEY);
    }

    @Nullable
    public static String getUserHome() {
        return System.getProperty(USER_HOME_KEY);
    }

    @Nullable
    public static String getUserDir() {
        return System.getProperty(USER_DIR_KEY);
    }

    @Nullable
    public static String getFileEncoding() {
        return System.getProperty(FILE_ENCODING_KEY);
    }

    @Nullable
    public static String getNativeEncoding() {
        return System.getProperty(NATIVE_ENCODING_KEY);
    }

    /**
     * Reads all system properties into a new map and returns.
     */
    public static Map<String, String> getProperties() {
        return FsCollect.toMap(System.getProperties());
    }

    /**
     * Returns the default charset of JVM: {@link Charset#defaultCharset()}
     */
    public static Charset jvmCharset() {
        return Charset.defaultCharset();
    }

    /**
     * Returns native charset.
     * This will search following system properties in order:
     * <ul>
     * <li>native.encoding</li>
     * <li>sun.jnu.encoding</li>
     * <li>file.encoding</li>
     * </ul>
     * <p>
     * If still not found, return null.
     */
    @Nullable
    public static Charset nativeCharset() {
        Charset result = null;
        if (nativeCharset == null) {
            String encoding = getNativeEncoding();
            if (encoding != null) {
                result = Charset.forName(encoding);
                nativeCharset = result;
                return result;
            }
            encoding = System.getProperty("sun.jnu.encoding");
            if (encoding != null) {
                result = Charset.forName(encoding);
                nativeCharset = result;
                return result;
            }
            encoding = getFileEncoding();
            if (encoding != null) {
                result = Charset.forName(encoding);
                nativeCharset = result;
                return result;
            }
            return null;
        }
        return nativeCharset;
    }

    /**
     * Returns whether current OS is Windows.
     */
    public static boolean isWindows() {
        String osName = getOsName();
        return osName != null && osName.startsWith("Windows");
    }

    /**
     * Returns whether current OS is Linux.
     */
    public static boolean isLinux() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Linux") || osName.startsWith("LINUX"));
    }

    /**
     * Returns whether current OS is Mac.
     */
    public static boolean isMac() {
        String osName = getOsName();
        return osName != null && (osName.startsWith("Mac"));
    }

    /**
     * Returns whether current OS is FreeBSD, OpenBSD or NetBSD.
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
     * Returns whether current JDK version >= 9.
     */
    public static boolean isJdk9OrHigher() {
        return javaMajorVersion() >= 9;
    }
}
