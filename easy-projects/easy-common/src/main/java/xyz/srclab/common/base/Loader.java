package xyz.srclab.common.base;

import org.apache.commons.io.IOUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.SetKit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Set;

public class Loader {

    public static ClassLoader currentClassLoader() {
        return Current.thread().getContextClassLoader();
    }

    @Nullable
    public static <T> Class<T> findClass(String className) {
        try {
            return Cast.as(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Class<T> findClass(String className, ClassLoader classLoader) {
        try {
            return Cast.as(Class.forName(className, true, classLoader));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> Class<T> loadClass(byte[] bytes) {
        return Cast.as(BytesClassLoader.INSTANCE.loadClass(bytes));
    }

    public static <T> Class<T> loadClass(byte[] bytes, int offset, int length) {
        return Cast.as(BytesClassLoader.INSTANCE.loadClass(bytes, offset, length));
    }

    public static <T> Class<T> loadClass(InputStream inputStream) {
        return Cast.as(BytesClassLoader.INSTANCE.loadClass(inputStream));
    }

    public static <T> Class<T> loadClass(ByteBuffer byteBuffer) {
        return Cast.as(BytesClassLoader.INSTANCE.loadClass(byteBuffer));
    }

    @Nullable
    public static URL findResource(String resourceName) {
        return findResource(resourceName, currentClassLoader());
    }

    @Nullable
    public static URL findResource(String resourceName, ClassLoader classLoader) {
        return classLoader.getResource(resourceName);
    }

    @Immutable
    public static Set<URL> findResources(String resourceName) {
        return findResources(resourceName, currentClassLoader());
    }

    @Immutable
    public static Set<URL> findResources(String resourceName, ClassLoader classLoader) {
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(resourceName);
            return SetKit.enumerationToSet(urlEnumeration);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final class BytesClassLoader extends ClassLoader {

        private static final BytesClassLoader INSTANCE = new BytesClassLoader();

        public Class<?> loadClass(byte[] bytes) {
            return loadClass(bytes, 0, bytes.length);
        }

        public Class<?> loadClass(byte[] bytes, int offset, int length) {
            return super.defineClass(null, bytes, offset, length);
        }

        public Class<?> loadClass(InputStream inputStream) {
            try {
                return loadClass(IOUtils.toByteArray(inputStream));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public Class<?> loadClass(ByteBuffer byteBuffer) {
            return super.defineClass(null, byteBuffer, null);
        }
    }
}
