package xyz.srclab.common.io;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.base.ref.BooleanRef;
import xyz.srclab.common.base.ref.LongRef;
import xyz.srclab.common.cache.FsCache;

import java.io.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * File cache service, provides cached IO methods.
 *
 * @author fredsuvn
 * @see Builder
 */
@ThreadSafe
public interface FsFileCache {

    /**
     * Returns a new builder for {@link FsFileCache}.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Opens an input stream for file of given path at specified file position,
     * data of the stream comes from cache or underlying file (if cache not found or expired).
     *
     * @param path   given path
     * @param offset specified file position
     */
    InputStream getInputStream(Path path, long offset);

    /**
     * Opens an output stream for file of given path at specified file position,
     * and cache will be updated after writing.
     *
     * @param path   given path
     * @param offset specified file position
     */
    OutputStream getOutputStream(Path path, long offset);

    /**
     * Sets new file length, truncated or extended.
     *
     * @param newLength new length
     */
    void setFileLength(Path path, long newLength);

    /**
     * Generator for bytes cache.
     */
    interface BytesCacheGenerator {

        /**
         * Generates a new bytes cache with given remove listener.
         *
         * @param removeListener given remove listener
         */
        BytesCache generate(RemoveListener removeListener);

        /**
         * Bytes cache remove listener.
         */
        interface RemoveListener {

            /**
             * On cache remove. This method should be called after the entry was removed.
             *
             * @param key   key of the cache
             * @param cache cache itself
             */
            void onCacheRemove(BytesPos key, BytesCache cache);
        }
    }

    /**
     * Cache interface used for {@link FsFileCache}, must support remove listener.
     */
    @ThreadSafe
    interface BytesCache {

        /**
         * Gets cached value, if the value doesn't exist, create and put a new one with given function.
         * If the function is null, return null else return cached or new value.
         * <p>
         * Note whole operation must be atomic.
         *
         * @param key key of the value
         */
        @Nullable
        byte[] get(BytesPos key, @Nullable Function<BytesPos, byte[]> function);

        /**
         * Puts new value of specified key
         *
         * @param key   specified key
         * @param value new value
         */
        void put(BytesPos key, byte[] value);

        /**
         * Removes the value associated with given key.
         *
         * @param key given key
         */
        void remove(BytesPos key);

        /**
         * Removes values of which key and value (first and second param) pass given predicate.
         *
         * @param predicate given predicate
         */
        void removeIf(BiPredicate<BytesPos, byte[]> predicate);
    }

    /**
     * Key for bytes cache, holds file path and start position of cached bytes data.
     */
    @Data
    @EqualsAndHashCode
    class BytesPos {
        /**
         * File path.
         */
        private final String path;
        /**
         * Start position of cached file data.
         */
        private final long position;
    }

    /**
     * Underlying file reader.
     */
    interface FileReader {

        /**
         * Opens an un-cache underlying input stream for file of given path at specified file position.
         * <p>
         * The returned input stream doesn't need support {@link InputStream#mark(int)}, {@link InputStream#reset()}
         * and even {@link InputStream#available()}.
         *
         * @param path       given path
         * @param offset     specified file position
         * @param bufferSize buffer size of input stream
         */
        InputStream getInputStream(Path path, long offset, int bufferSize) throws IOException;
    }

    /**
     * Underlying file writer.
     */
    interface FileWriter {

        /**
         * Opens an un-cache underlying output stream for file of given path at specified file position,
         *
         * @param path       given path
         * @param offset     specified file position
         * @param bufferSize buffer size of input stream
         */
        OutputStream getOutputStream(Path path, long offset, int bufferSize) throws IOException;
    }

    /**
     * Listener for cache read.
     */
    interface CacheReadListener {

        /**
         * On cache read.
         *
         * @param path   file path
         * @param offset offset position
         * @param length read length
         */
        void onCacheRead(Path path, long offset, long length);
    }

    /**
     * Listener for cache write.
     */
    interface CacheWriteListener {

        /**
         * On cache write.
         *
         * @param file   file path
         * @param offset offset position
         * @param length written length
         */
        void onCacheWrite(Path file, long offset, long length);
    }

    /**
     * Listener for underlying file read.
     */
    interface FileReadListener {

        /**
         * On file read.
         *
         * @param file   file path
         * @param offset offset position
         * @param length read length
         */
        void onFileRead(Path file, long offset, long length);
    }

    /**
     * Listener for underlying file write.
     */
    interface FileWriteListener {

        /**
         * On file write.
         *
         * @param file   file path
         * @param offset offset position
         * @param length written length
         */
        void onFileWrite(Path file, long offset, long length);
    }

    /**
     * Builder for {@link FsFileCache}.
     */
    class Builder {

        private static final class BytesCacheImpl implements BytesCache {

            private final FsCache<BytesPos, byte[]> cache;

            private BytesCacheImpl(BytesCacheGenerator.RemoveListener removeListener) {
                this.cache = FsCache.softCache((cache, key) ->
                    removeListener.onCacheRemove(key, BytesCacheImpl.this));
            }

            @Override
            public @Nullable byte[] get(BytesPos key, @Nullable Function<BytesPos, byte[]> function) {
                if (function == null) {
                    return cache.get(key);
                }
                return cache.get(key, function);
            }

            @Override
            public void put(BytesPos key, byte[] value) {
                cache.put(key, value);
            }

            @Override
            public void remove(BytesPos key) {
                cache.remove(key);
            }

            @Override
            public void removeIf(BiPredicate<BytesPos, byte[]> predicate) {
                cache.removeIf(predicate);
            }
        }

        private static final BytesCacheGenerator DEFAULT_CACHE_GENERATOR = BytesCacheImpl::new;

        private static final FileReader DEFAULT_FILE_READER = (path, offset, bufferSize) -> {
            try {
                RandomAccessFile random = new RandomAccessFile(path.toFile(), "r");
                InputStream randomInput = FsIO.toInputStream(random, offset, -1);
                return new BufferedInputStream(randomInput, bufferSize);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        };

        private static final FileWriter DEFAULT_FILE_WRITER = (path, offset, bufferSize) -> {
            try {
                RandomAccessFile random = new RandomAccessFile(path.toFile(), "rw");
                OutputStream randomOutput = FsIO.toOutputStream(random, offset, -1);
                return new BufferedOutputStream(randomOutput, bufferSize);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        };

        private int chunkSize = 1024 * 4;
        private int bufferSize = FsIO.IO_BUFFER_SIZE;
        private BytesCacheGenerator bytesCacheGenerator = DEFAULT_CACHE_GENERATOR;
        private FileReader fileReader = DEFAULT_FILE_READER;
        private FileWriter fileWriter = DEFAULT_FILE_WRITER;
        private CacheReadListener cacheReadListener = null;
        private CacheWriteListener cacheWriteListener = null;
        private FileReadListener fileReadListener = null;
        private FileWriteListener fileWriteListener = null;

        /**
         * Sets file chunk size for caching, default is {@link FsIO#IO_BUFFER_SIZE}.
         *
         * @param chunkSize chunk size
         */
        public Builder chunkSize(int chunkSize) {
            FsCheck.checkArgument(chunkSize > 0, "chunkSize must > 0.");
            this.chunkSize = chunkSize;
            return this;
        }

        /**
         * Sets buffer size for IO operation, default is {@link FsIO#IO_BUFFER_SIZE}.
         *
         * @param bufferSize buffer size
         */
        public Builder bufferSize(int bufferSize) {
            FsCheck.checkArgument(bufferSize > 0, "bufferSize must > 0.");
            this.bufferSize = bufferSize;
            return this;
        }

        /**
         * Sets cache generator, the default generator uses {@link FsCache#softCache(FsCache.RemoveListener)}.
         *
         * @param bytesCacheGenerator cache generator
         */
        public Builder cacheGenerator(BytesCacheGenerator bytesCacheGenerator) {
            this.bytesCacheGenerator = bytesCacheGenerator;
            return this;
        }

        /**
         * Sets underlying file reader, the default file reader uses {@link RandomAccessFile}.
         *
         * @param fileReader underlying file reader
         */
        public Builder fileReader(FileReader fileReader) {
            this.fileReader = fileReader;
            return this;
        }

        /**
         * Sets underlying file writer, the default file writer uses {@link RandomAccessFile}.
         *
         * @param fileWriter underlying file writer
         */
        public Builder fileReader(FileWriter fileWriter) {
            this.fileWriter = fileWriter;
            return this;
        }

        /**
         * Sets cache read listener, default is null.
         *
         * @param cacheReadListener cache read listener
         */
        public Builder cacheReadListener(@Nullable FsFileCache.CacheReadListener cacheReadListener) {
            this.cacheReadListener = cacheReadListener;
            return this;
        }

        /**
         * Sets cache write listener, default is null.
         *
         * @param cacheWriteListener cache write listener
         */
        public Builder cacheWriteListener(@Nullable FsFileCache.CacheWriteListener cacheWriteListener) {
            this.cacheWriteListener = cacheWriteListener;
            return this;
        }

        /**
         * Sets underlying file read listener, default is null.
         *
         * @param fileReadListener file read listener
         */
        public Builder fileReadListener(@Nullable FsFileCache.FileReadListener fileReadListener) {
            this.fileReadListener = fileReadListener;
            return this;
        }

        /**
         * Sets underlying file write listener, default is null.
         *
         * @param fileWriteListener file write listener
         */
        public Builder fileWriteListener(@Nullable FsFileCache.FileWriteListener fileWriteListener) {
            this.fileWriteListener = fileWriteListener;
            return this;
        }

        /**
         * Builds {@link FsFileCache}.
         */
        public FsFileCache build() {
            return new FsFileCacheImpl(
                chunkSize,
                bufferSize,
                bytesCacheGenerator,
                fileReader,
                fileWriter,
                cacheReadListener,
                cacheWriteListener,
                fileReadListener,
                fileWriteListener
            );
        }

        private static final class FsFileCacheImpl implements FsFileCache {

            private static final byte[] EOF = new byte[0];

            private final int chunkSize;
            private final int bufferSize;
            private final FileReader fileReader;
            private final FileWriter fileWriter;
            private final CacheReadListener cacheReadListener;
            private final CacheWriteListener cacheWriteListener;
            private final FileReadListener fileReadListener;
            private final FileWriteListener fileWriteListener;
            private final BytesCache fileCache;

            private FsFileCacheImpl(
                int chunkSize,
                int bufferSize,
                BytesCacheGenerator bytesCacheGenerator,
                FileReader fileReader,
                FileWriter fileWriter,
                CacheReadListener cacheReadListener,
                CacheWriteListener cacheWriteListener,
                FileReadListener fileReadListener,
                FileWriteListener fileWriteListener
            ) {
                this.chunkSize = chunkSize;
                this.bufferSize = bufferSize;
                this.fileReader = fileReader;
                this.fileWriter = fileWriter;
                this.cacheReadListener = cacheReadListener;
                this.cacheWriteListener = cacheWriteListener;
                this.fileReadListener = fileReadListener;
                this.fileWriteListener = fileWriteListener;
                this.fileCache = bytesCacheGenerator.generate(new BytesCacheGenerator.RemoveListener() {
                    @Override
                    public void onCacheRemove(BytesPos key, BytesCache cache) {
                        fileCache.remove(key);
                    }
                });
            }

            @Override
            public InputStream getInputStream(Path path, long offset) {
                return new CacheInputStream(path, offset);
            }

            @Override
            public OutputStream getOutputStream(Path path, long offset) {
                return new CacheOutputStream(path, offset);
            }

            @Override
            public void setFileLength(Path path, long newLength) {
                try {
                    RandomAccessFile random = new RandomAccessFile(path.toFile(), "rws");
                    random.setLength(newLength);
                    random.close();
                } catch (Exception e) {
                    throw new FsIOException(e);
                }
            }

            private final class CacheInputStream extends InputStream {

                private final Path path;
                private long pos;
                private InputStream underlying = null;

                CacheInputStream(Path path, long offset) {
                    try {
                        FsCheck.checkArgument(offset >= 0, "offset must >= 0.");
                        this.path = path;
                        this.pos = offset;
                    } catch (Exception e) {
                        throw new FsIOException(e);
                    }
                }

                @Override
                public synchronized int read(byte[] b, int off, int len) throws IOException {
                    try {
                        FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
                        if (pos == -1) {
                            return -1;
                        }
                        if (len == 0) {
                            return 0;
                        }
                        int readSize = read0(b, off, len);
                        if (readSize == -1) {
                            pos = -1;
                            return -1;
                        } else {
                            pos += readSize;
                        }
                        return readSize;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private int read0(byte[] b, int off, int len) throws IOException {
                    int offset = off;
                    int remaining = len;
                    final LongRef posIndex = new LongRef(pos / chunkSize);
                    long posOffset = pos % chunkSize;
                    String pathString = path.toString();
                    BytesPos bytesPos;
                    final BooleanRef cacheFlag = new BooleanRef(true);
                    while (true) {
                        bytesPos = new BytesPos(pathString, posIndex.get());
                        byte[] bytes = fileCache.get(bytesPos, k -> {
                            cacheFlag.set(false);
                            if (underlying == null) {
                                try {
                                    underlying = fileReader.getInputStream(path, posIndex.get() * chunkSize, bufferSize);
                                } catch (Exception e) {
                                    throw new FsIOException(e);
                                }
                            }
                            byte[] readBytes = FsIO.readBytes(underlying, chunkSize);
                            if (readBytes == null) {
                                return EOF;
                            }
                            return readBytes;
                        });
                        if (bytes.length > 0) {
                            int copySize = Math.min(remaining, bytes.length - (int) posOffset);
                            System.arraycopy(bytes, (int) posOffset, b, offset, copySize);
                            offset += copySize;
                            remaining -= copySize;
                            if (cacheFlag.get()) {
                                if (cacheReadListener != null) {
                                    cacheReadListener.onCacheRead(path, posIndex.get() * chunkSize + posOffset, copySize);
                                }
                            } else {
                                if (fileReadListener != null) {
                                    fileReadListener.onFileRead(path, posIndex.get() * chunkSize + posOffset, copySize);
                                }
                            }
                            cacheFlag.set(true);
                        }
                        if (remaining <= 0 || bytes.length < chunkSize) {
                            break;
                        }
                        posIndex.incrementAndGet();
                        posOffset = 0;
                    }
                    if (remaining == len) {
                        return -1;
                    }
                    return len - remaining;
                }

                @Override
                public synchronized int read() throws IOException {
                    byte[] dest = new byte[1];
                    int read = read(dest, 0, 1);
                    if (read == -1) {
                        return -1;
                    }
                    return dest[0] & 0x000000ff;
                }

                // @Override
                // public synchronized long skip(long n) throws IOException {
                //     try {
                //         if (n <= 0) {
                //             return 0;
                //         }
                //         if (underlying != null) {
                //
                //             long skip = underlying.skip(n);
                //             pos += skip;
                //             return skip;
                //         }
                //         pos += n;
                //         return n;
                //     } catch (Exception e) {
                //         throw new IOException(e);
                //     }
                // }

                @Override
                public synchronized int available() throws IOException {
                    if (underlying != null) {
                        return underlying.available();
                    }
                    long posIndex = pos / chunkSize;
                    long posOffset = pos % chunkSize;
                    byte[] bytes = fileCache.get(new BytesPos(path.toString(), posIndex), null);
                    if (bytes == null || posOffset >= bytes.length) {
                        return 0;
                    }
                    return bytes.length - (int) posIndex;
                }

                @Override
                public synchronized void close() throws IOException {
                    try {
                        if (underlying != null) {
                            underlying.close();
                        }
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }
            }

            private final class CacheOutputStream extends OutputStream {

                private final Path path;
                private long pos;
                private OutputStream underlying = null;

                CacheOutputStream(Path path, long offset) {
                    try {
                        FsCheck.checkArgument(offset >= 0, "offset must >= 0.");
                        this.path = path;
                        this.pos = offset;
                    } catch (Exception e) {
                        throw new FsIOException(e);
                    }
                }

                @Override
                public synchronized void write(byte[] b, int off, int len) throws IOException {
                    try {
                        FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
                        if (len == 0) {
                            return;
                        }
                        writeUnderlying(b, off, len);
                        String pathString = path.toString();
                        fileCache.removeIf((bytesPos, data) -> Objects.equals(bytesPos.getPath(), pathString));
                        pos += len;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private void writeUnderlying(byte[] b, int off, int len) throws IOException {
                    if (underlying == null) {
                        underlying = fileWriter.getOutputStream(path, pos, bufferSize);
                    }
                    underlying.write(b, off, len);
                    if (fileWriteListener != null) {
                        fileWriteListener.onFileWrite(path, pos, len);
                    }
                }

                @Override
                public synchronized void write(int b) throws IOException {
                    write(new byte[]{(byte) b});
                }

                @Override
                public synchronized void flush() throws IOException {
                    if (underlying != null) {
                        underlying.flush();
                    }
                }

                @Override
                public synchronized void close() throws IOException {
                    if (underlying != null) {
                        underlying.close();
                    }
                }
            }
        }
    }
}
