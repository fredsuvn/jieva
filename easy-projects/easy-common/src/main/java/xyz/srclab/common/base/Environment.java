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

    public static Environment currentEnvironment() {
        return EnvironmentHolder.INSTANCE;
    }

    private final Java java;
    private final Os os;
    private final User user;
    private final App app;
    private final Ext ext;

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
    }

    public Java getJavaEnvironment() {
        return java;
    }

    public Os getOsEnvironment() {
        return os;
    }

    public User getUserEnvironment() {
        return user;
    }

    public App getAppEnvironment() {
        return app;
    }

    public Ext getExtEnvironment() {
        return ext;
    }

    public String getJavaVersion() {
        return java.getVersion();
    }

    public String getJavaVendor() {
        return java.getVendor();
    }

    public String getJavaVendorUrl() {
        return java.getVendorUrl();
    }

    public String getJavaHome() {
        return java.getHome();
    }

    public String getJavaVmSpecificationVersion() {
        return java.getVmSpecificationVersion();
    }

    public String getJavaVmSpecificationVendor() {
        return java.getVmSpecificationVendor();
    }

    public String getJavaVmSpecificationName() {
        return java.getVmSpecificationName();
    }

    public String getJavaVmVersion() {
        return java.getVmVersion();
    }

    public String getJavaVmVendor() {
        return java.getVmVendor();
    }

    public String getJavaVmName() {
        return java.getVmName();
    }

    public String getJavaSpecificationVersion() {
        return java.getSpecificationVersion();
    }

    public String getJavaSpecificationVendor() {
        return java.getSpecificationVendor();
    }

    public String getJavaSpecificationName() {
        return java.getSpecificationName();
    }

    public String getJavaClassVersion() {
        return java.getClassVersion();
    }

    public String getJavaClassPath() {
        return java.getClassPath();
    }

    public String getJavaLibraryPath() {
        return java.getLibraryPath();
    }

    public String getJavaIoTmpdir() {
        return java.getIoTmpdir();
    }

    public String getJavaCompiler() {
        return java.getCompiler();
    }

    public String getJavaExtDirs() {
        return java.getExtDirs();
    }

    public String getOsName() {
        return os.getName();
    }

    public String getOsArch() {
        return os.getArch();
    }

    public String getOsVersion() {
        return os.getVersion();
    }

    public String getOsFileSeparator() {
        return os.getFileSeparator();
    }

    public String getOsPathSeparator() {
        return os.getPathSeparator();
    }

    public String getOsLineSeparator() {
        return os.getLineSeparator();
    }

    public String getUserName() {
        return user.getName();
    }

    public String getUserHome() {
        return user.getHome();
    }

    public String getUserDir() {
        return user.getDir();
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

        public String getVersion() {
            return version;
        }

        public String getVendor() {
            return vendor;
        }

        public String getVendorUrl() {
            return vendorUrl;
        }

        public String getHome() {
            return home;
        }

        public String getVmSpecificationVersion() {
            return vmSpecificationVersion;
        }

        public String getVmSpecificationVendor() {
            return vmSpecificationVendor;
        }

        public String getVmSpecificationName() {
            return vmSpecificationName;
        }

        public String getVmVersion() {
            return vmVersion;
        }

        public String getVmVendor() {
            return vmVendor;
        }

        public String getVmName() {
            return vmName;
        }

        public String getSpecificationVersion() {
            return specificationVersion;
        }

        public String getSpecificationVendor() {
            return specificationVendor;
        }

        public String getSpecificationName() {
            return specificationName;
        }

        public String getClassVersion() {
            return classVersion;
        }

        public String getClassPath() {
            return classPath;
        }

        public String getLibraryPath() {
            return libraryPath;
        }

        public String getIoTmpdir() {
            return ioTmpdir;
        }

        public String getCompiler() {
            return compiler;
        }

        public String getExtDirs() {
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

        public String getName() {
            return name;
        }

        public String getArch() {
            return arch;
        }

        public String getVersion() {
            return version;
        }

        public String getFileSeparator() {
            return fileSeparator;
        }

        public String getPathSeparator() {
            return pathSeparator;
        }

        public String getLineSeparator() {
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

        public String getName() {
            return name;
        }

        public String getHome() {
            return home;
        }

        public String getDir() {
            return dir;
        }

        @Override
        public @Immutable Map<String, String> properties() {
            return properties;
        }
    }

    public static final class App extends Env {

        private final @Immutable Map<String, String> properties;

        private App(Properties properties) {
            Map<String, String> appProperties = new HashMap<>();
            this.properties = MapKit.unmodifiable(appProperties);
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
