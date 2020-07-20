package xyz.srclab.common.base;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.collection.MapKit;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author sunqian
 */
public class Environment {

    public static String JAVA_VERSION = "java.version";
    public static String JAVA_VENDOR = "java.vendor";
    public static String JAVA_VENDOR_URL = "java.vendor.url";
    public static String JAVA_HOME = "java.home";
    public static String JAVA_VM_SPECIFICATION_VERSION = "java.vm.specification.version";
    public static String JAVA_VM_SPECIFICATION_VENDOR = "java.vm.specification.vendor";
    public static String JAVA_VM_SPECIFICATION_NAME = "java.vm.specification.name";
    public static String JAVA_VM_VERSION = "java.vm.version";
    public static String JAVA_VM_VENDOR = "java.vm.vendor";
    public static String JAVA_VM_NAME = "java.vm.name";
    public static String JAVA_SPECIFICATION_VERSION = "java.specification.version";
    public static String JAVA_SPECIFICATION_VENDOR = "java.specification.vendor";
    public static String JAVA_SPECIFICATION_NAME = "java.specification.name";
    public static String JAVA_CLASS_VERSION = "java.class.version";
    public static String JAVA_CLASS_PATH = "java.class.path";
    public static String JAVA_LIBRARY_PATH = "java.library.path";
    public static String JAVA_IO_TMPDIR = "java.io.tmpdir";
    public static String JAVA_COMPILER = "java.compiler";
    public static String JAVA_EXT_DIRS = "java.ext.dirs";
    public static String OS_NAME = "os.name";
    public static String OS_ARCH = "os.arch";
    public static String OS_VERSION = "os.version";
    public static String FILE_SEPARATOR = "file.separator";
    public static String PATH_SEPARATOR = "path.separator";
    public static String LINE_SEPARATOR = "line.separator";
    public static String USER_NAME = "user.name";
    public static String USER_HOME = "user.home";
    public static String USER_DIR = "user.dir";

    public static Environment currentEnvironment() {
        return EnvironmentHolder.INSTANCE;
    }

    private final Java java;
    private final Os os;
    private final User user;
    private final App app;
    private final Ext ext;

    private final @Immutable Map<String, String> properties;

    private Environment() {
        this(System.getProperties());
    }

    private Environment(Properties properties) {
        this.java = new Java(properties);
        this.os = new Os(properties);
        this.user = new User(properties);
        this.app = new App(properties);
        Map<String, String> already = MapKit.merge(
                java.properties(), os.properties(), user.properties(), app.properties());
        this.ext = new Ext(properties, already);
        this.properties = MapKit.map(properties, String::valueOf, String::valueOf);
    }

    public Java javaEnvironment() {
        return java;
    }

    public Os osEnvironment() {
        return os;
    }

    public User userEnvironment() {
        return user;
    }

    public App appEnvironment() {
        return app;
    }

    public Ext extEnvironment() {
        return ext;
    }

    public Map<String, String> asMap() {
        return properties;
    }

    public String javaVersion() {
        return java.version();
    }

    public String javaVendor() {
        return java.vendor();
    }

    public String javaVendorUrl() {
        return java.vendorUrl();
    }

    public String javaHome() {
        return java.home();
    }

    public String javaVmSpecificationVersion() {
        return java.vmSpecificationVersion();
    }

    public String javaVmSpecificationVendor() {
        return java.vmSpecificationVendor();
    }

    public String javaVmSpecificationName() {
        return java.vmSpecificationName();
    }

    public String javaVmVersion() {
        return java.vmVersion();
    }

    public String javaVmVendor() {
        return java.vmVendor();
    }

    public String javaVmName() {
        return java.vmName();
    }

    public String javaSpecificationVersion() {
        return java.specificationVersion();
    }

    public String javaSpecificationVendor() {
        return java.specificationVendor();
    }

    public String javaSpecificationName() {
        return java.specificationName();
    }

    public String javaClassVersion() {
        return java.classVersion();
    }

    public String javaClassPath() {
        return java.classPath();
    }

    public String javaLibraryPath() {
        return java.libraryPath();
    }

    public String javaIoTmpdir() {
        return java.ioTmpdir();
    }

    public String javaCompiler() {
        return java.compiler();
    }

    public String javaExtDirs() {
        return java.extDirs();
    }

    public String osName() {
        return os.name();
    }

    public String osArch() {
        return os.arch();
    }

    public String osVersion() {
        return os.version();
    }

    public String osFileSeparator() {
        return os.fileSeparator();
    }

    public String osPathSeparator() {
        return os.pathSeparator();
    }

    public String osLineSeparator() {
        return os.lineSeparator();
    }

    public String userName() {
        return user.name();
    }

    public String userHome() {
        return user.home();
    }

    public String userDir() {
        return user.dir();
    }

    private static abstract class Env {

        protected String getProperty(String key, Properties source, @Out Map<String, String> dest) {
            String value = source.getProperty(key);
            dest.put(key, value);
            return value;
        }

        @Immutable
        public abstract Map<String, String> properties();
    }

    public static final class Java extends Env {

        private final String version;
        private final String vendor;
        private final String vendorUrl;
        private final String home;
        private final String vmSpecificationVersion;
        private final String vmSpecificationVendor;
        private final String vmSpecificationName;
        private final String vmVersion;
        private final String vmVendor;
        private final String vmName;
        private final String specificationVersion;
        private final String specificationVendor;
        private final String specificationName;
        private final String classVersion;
        private final String classPath;
        private final String libraryPath;
        private final String ioTmpdir;
        private final String compiler;
        private final String extDirs;

        private final @Immutable Map<String, String> properties;

        private Java(Properties properties) {
            Map<String, String> javaProperties = new HashMap<>();
            this.version = getProperty("java.version", properties, javaProperties);
            this.vendor = getProperty("java.vendor", properties, javaProperties);
            this.vendorUrl = getProperty("java.vendor.url", properties, javaProperties);
            this.home = getProperty("java.home", properties, javaProperties);
            this.vmSpecificationVersion = getProperty("java.vm.specification.version", properties, javaProperties);
            this.vmSpecificationVendor = getProperty("java.vm.specification.vendor", properties, javaProperties);
            this.vmSpecificationName = getProperty("java.vm.specification.name", properties, javaProperties);
            this.vmVersion = getProperty("java.vm.version", properties, javaProperties);
            this.vmVendor = getProperty("java.vm.vendor", properties, javaProperties);
            this.vmName = getProperty("java.vm.name", properties, javaProperties);
            this.specificationVersion = getProperty("java.specification.version", properties, javaProperties);
            this.specificationVendor = getProperty("java.specification.vendor", properties, javaProperties);
            this.specificationName = getProperty("java.specification.name", properties, javaProperties);
            this.classVersion = getProperty("java.class.version", properties, javaProperties);
            this.classPath = getProperty("java.class.path", properties, javaProperties);
            this.libraryPath = getProperty("java.library.path", properties, javaProperties);
            this.ioTmpdir = getProperty("java.io.tmpdir", properties, javaProperties);
            this.compiler = getProperty("java.compiler", properties, javaProperties);
            this.extDirs = getProperty("java.ext.dirs", properties, javaProperties);
            this.properties = MapKit.unmodifiable(javaProperties);
        }

        public String version() {
            return version;
        }

        public String vendor() {
            return vendor;
        }

        public String vendorUrl() {
            return vendorUrl;
        }

        public String home() {
            return home;
        }

        public String vmSpecificationVersion() {
            return vmSpecificationVersion;
        }

        public String vmSpecificationVendor() {
            return vmSpecificationVendor;
        }

        public String vmSpecificationName() {
            return vmSpecificationName;
        }

        public String vmVersion() {
            return vmVersion;
        }

        public String vmVendor() {
            return vmVendor;
        }

        public String vmName() {
            return vmName;
        }

        public String specificationVersion() {
            return specificationVersion;
        }

        public String specificationVendor() {
            return specificationVendor;
        }

        public String specificationName() {
            return specificationName;
        }

        public String classVersion() {
            return classVersion;
        }

        public String classPath() {
            return classPath;
        }

        public String libraryPath() {
            return libraryPath;
        }

        public String ioTmpdir() {
            return ioTmpdir;
        }

        public String compiler() {
            return compiler;
        }

        public String extDirs() {
            return extDirs;
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    public static final class Os extends Env {

        private final String name;
        private final String arch;
        private final String version;
        private final String fileSeparator;
        private final String pathSeparator;
        private final String lineSeparator;

        private final @Immutable Map<String, String> properties;

        private Os(Properties properties) {
            Map<String, String> osProperties = new HashMap<>();
            this.name = getProperty("os.name", properties, osProperties);
            this.arch = getProperty("os.arch", properties, osProperties);
            this.version = getProperty("os.version", properties, osProperties);
            this.fileSeparator = getProperty("file.separator", properties, osProperties);
            this.pathSeparator = getProperty("path.separator", properties, osProperties);
            this.lineSeparator = getProperty("line.separator", properties, osProperties);
            this.properties = MapKit.unmodifiable(osProperties);
        }

        public String name() {
            return name;
        }

        public String arch() {
            return arch;
        }

        public String version() {
            return version;
        }

        public String fileSeparator() {
            return fileSeparator;
        }

        public String pathSeparator() {
            return pathSeparator;
        }

        public String lineSeparator() {
            return lineSeparator;
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    public static final class User extends Env {

        private final String name;
        private final String home;
        private final String dir;

        private final @Immutable Map<String, String> properties;

        private User(Properties properties) {
            Map<String, String> userProperties = new HashMap<>();
            this.name = getProperty("user.name", properties, userProperties);
            this.home = getProperty("user.home", properties, userProperties);
            this.dir = getProperty("user.dir", properties, userProperties);
            this.properties = MapKit.unmodifiable(userProperties);
        }

        public String name() {
            return name;
        }

        public String home() {
            return home;
        }

        public String dir() {
            return dir;
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    public static final class App extends Env {

        private final String name;
        private final String version;
        private final String description;

        private final @Immutable Map<String, String> properties;

        private App(Properties properties) {
            Map<String, String> appProperties = new HashMap<>();
            this.name = getProperty("app.name", properties, appProperties);
            this.version = getProperty("app.version", properties, appProperties);
            this.description = getProperty("app.description", properties, appProperties);
            this.properties = MapKit.unmodifiable(appProperties);
        }

        public String name() {
            return name;
        }

        public String version() {
            return version;
        }

        public String description() {
            return description;
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    public static final class Ext extends Env {

        private final @Immutable Map<String, String> properties;

        private Ext(Properties properties, Map<String, String> already) {
            Map<String, String> extProperties = new HashMap<>();
            properties.forEach((k, v) -> {
                String key = k.toString();
                if (already.containsKey(key)) {
                    return;
                }
                extProperties.put(key, String.valueOf(v));
            });
            this.properties = MapKit.unmodifiable(extProperties);
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    private static final class EnvironmentHolder {

        public static Environment INSTANCE = new Environment();
    }
}
