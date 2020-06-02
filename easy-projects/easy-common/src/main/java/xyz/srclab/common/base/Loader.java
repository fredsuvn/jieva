package xyz.srclab.common.base;

import org.apache.commons.io.IOUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.exception.ExceptionWrapper;

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
    public static <T> Class<T> loadClass(String className) {
        try {
            return Cast.as(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Class<T> loadClass(String className, ClassLoader classLoader) {
        try {
            return Cast.as(Class.forName(className, true, classLoader));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> Class<T> loadClass(byte[] bytes) {
        return Cast.as(BytesClassLoader.INSTANCE.loadBytes(bytes));
    }

    public static <T> Class<T> loadClass(byte[] bytes, int offset, int length) {
        return Cast.as(BytesClassLoader.INSTANCE.loadBytes(bytes, offset, length));
    }

    public static <T> Class<T> loadClass(InputStream inputStream) {
        return Cast.as(BytesClassLoader.INSTANCE.loadStream(inputStream));
    }

    public static <T> Class<T> loadClass(ByteBuffer byteBuffer) {
        return Cast.as(BytesClassLoader.INSTANCE.loadBuffer(byteBuffer));
    }

    @Nullable
    public static URL loadResource(String resourceName) {
        return loadResource(resourceName, currentClassLoader());
    }

    @Nullable
    public static URL loadResource(String resourceName, ClassLoader classLoader) {
        return classLoader.getResource(resourceName);
    }

    @Immutable
    public static Set<URL> loadResources(String resourceName) {
        return loadResources(resourceName, currentClassLoader());
    }

    @Immutable
    public static Set<URL> loadResources(String resourceName, ClassLoader classLoader) {
        try {
            Enumeration<URL> urlEnumeration = classLoader.getResources(resourceName);
            return SetKit.enumerationToSet(urlEnumeration);
        } catch (Exception e) {
            throw new ExceptionWrapper(e);
        }
    }

    private static final class BytesClassLoader extends ClassLoader {

        private static final BytesClassLoader INSTANCE = new BytesClassLoader();

        private Class<?> loadBytes(byte[] bytes) {
            return loadBytes(bytes, 0, bytes.length);
        }

        private Class<?> loadBytes(byte[] bytes, int offset, int length) {
            return super.defineClass(null, bytes, offset, length);
        }

        private Class<?> loadStream(InputStream inputStream) {
            try {
                return loadBytes(IOUtils.toByteArray(inputStream));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        private Class<?> loadBuffer(ByteBuffer byteBuffer) {
            return super.defineClass(null, byteBuffer, null);
        }
    }
}
