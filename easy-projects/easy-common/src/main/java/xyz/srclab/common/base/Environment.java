package xyz.srclab.common.base;

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

    private Environment() {
        this(System.getProperties());
    }

    private Environment(Properties properties) {
        this.java = new Java(properties);
        this.os = new Os(properties);
        this.user = new User(properties);
        this.app = new App(properties);
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

    public static final class Java {

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

        private Java(Properties properties) {
            this.version = properties.getProperty("java.version");
            this.vendor = properties.getProperty("java.vendor");
            this.vendorUrl = properties.getProperty("java.vendor.url");
            this.home = properties.getProperty("java.home");
            this.vmSpecificationVersion = properties.getProperty("java.vm.specification.version");
            this.vmSpecificationVendor = properties.getProperty("java.vm.specification.vendor");
            this.vmSpecificationName = properties.getProperty("java.vm.specification.name");
            this.vmVersion = properties.getProperty("java.vm.version");
            this.vmVendor = properties.getProperty("java.vm.vendor");
            this.vmName = properties.getProperty("java.vm.name");
            this.specificationVersion = properties.getProperty("java.specification.version");
            this.specificationVendor = properties.getProperty("java.specification.vendor");
            this.specificationName = properties.getProperty("java.specification.name");
            this.classVersion = properties.getProperty("java.class.version");
            this.classPath = properties.getProperty("java.class.path");
            this.libraryPath = properties.getProperty("java.library.path");
            this.ioTmpdir = properties.getProperty("java.io.tmpdir");
            this.compiler = properties.getProperty("java.compiler");
            this.extDirs = properties.getProperty("java.ext.dirs");
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
    }

    public static final class Os {

        private final String name;
        private final String arch;
        private final String version;
        private final String fileSeparator;
        private final String pathSeparator;
        private final String lineSeparator;

        private Os(Properties properties) {
            this.name = properties.getProperty("os.name");
            this.arch = properties.getProperty("os.arch");
            this.version = properties.getProperty("os.version");
            this.fileSeparator = properties.getProperty("file.separator");
            this.pathSeparator = properties.getProperty("path.separator");
            this.lineSeparator = properties.getProperty("line.separator");
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
    }

    public static final class User {

        private final String name;
        private final String home;
        private final String dir;

        private User(Properties properties) {
            this.name = properties.getProperty("user.name");
            this.home = properties.getProperty("user.home");
            this.dir = properties.getProperty("user.dir");
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
    }

    public static final class App {

        private App(Properties properties) {
        }
    }

    private static final class EnvironmentHolder {

        public static Environment INSTANCE = new Environment();
    }
}
