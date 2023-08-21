package xyz.srclab.common.io;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.annotations.concurrent.ThreadSafe;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.base.ref.LongRef;
import xyz.srclab.common.cache.FsCache;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Cache for file, provides methods about IO streams to read/write files between cache and underlying.
 *
 * @author fredsuvn
 * @see Builder
 */
@ThreadSafe
public interface FsFileCache {

    /**
     * Returns an input stream of file on given path seeks from given offset.
     * <p>
     * The stream first attempts to read cached data, and if unavailable, it will read from the underlying file.
     *
     * @param path   given path
     * @param offset seek position of the file
     */
    InputStream getInputStream(Path path, long offset);

    /**
     * Returns an output stream of file on given path seeks from given offset.
     * <p>
     * The stream will update cached data, after writing to the underlying file.
     *
     * @param path   given path
     * @param offset seek position of the file
     */
    OutputStream getOutputStream(Path path, long offset);

    /**
     * Generator for file cache.
     * <p>
     * Note the generated cache <b>must</b> support remove listener.
     */
    interface CacheGenerator {

        /**
         * Generates a new file cache with given remove listener.
         *
         * @param removeListener given remove listener
         */
        FileCache generate(Path path, RemoveListener removeListener);

        /**
         * File cache remove listener.
         */
        interface RemoveListener {

            /**
             * On cache remove. This method should be called after removing the key and its value.
             *
             * @param path  file path
             * @param key   key of the cache
             * @param cache cache itself
             */
            void onCacheRemove(Path path, Long key, FileCache cache);
        }
    }

    /**
     * Cache interface used for {@link FsFileCache}, must support remove listener.
     */
    @ThreadSafe
    interface FileCache {

        /**
         * Gets cached value, if the value doesn't exist, create and put a new one with given function.
         * If the function is null, return null else return cached or new value.
         * <p>
         * Note whole operation must be atomic.
         *
         * @param key key of the value
         */
        @Nullable
        byte[] get(Long key, @Nullable Function<Long, byte[]> function);

        /**
         * Puts new value of specified key
         *
         * @param key   specified key
         * @param value new value
         */
        void put(Long key, byte[] value);

        /**
         * Current size of cache.
         */
        int size();

        /**
         * Removes all data in current cache.
         */
        void clear();
    }

    /**
     * Underlying file reader.
     */
    interface FileReader {

        /**
         * Returns an un-cached underlying file input stream to read file.
         * The start read point will be set at given offset, and readable bytes is given length.
         * The length may be set to -1 to read to end of the file.
         * <p>
         * The returned input stream doesn't need support {@link InputStream#mark(int)}, {@link InputStream#reset()}
         * and even {@link InputStream#available()}.
         *
         * @param path   path of file
         * @param offset the offset position, measured in bytes from the beginning of the file
         * @param length readable bytes, may be -1 to read to end of the file
         */
        InputStream getInputStream(Path path, long offset, long length);
    }

    /**
     * Underlying file writer.
     */
    interface FileWriter {

        /**
         * Returns an un-cached underlying file out stream to write file.
         * The start write point will be set at given offset, and writeable bytes is given length.
         * The length may be set to -1 to write unlimitedly.
         *
         * @param path   path of file
         * @param offset the offset position, measured in bytes from the beginning of the file
         * @param length writeable bytes, may be -1 to write unlimitedly
         */
        OutputStream getOutputStream(Path path, long offset, long length);
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
     * Builder for {@link  FsFileCache}.
     */
    class Builder {

        private static final class FileCacheImpl implements FileCache {

            private final FsCache<Long, byte[]> cache;

            private FileCacheImpl(Path path, CacheGenerator.RemoveListener removeListener) {
                this.cache = FsCache.softCache((cache, key) ->
                    removeListener.onCacheRemove(path, key, FileCacheImpl.this));
            }

            @Override
            public @Nullable byte[] get(Long key, @Nullable Function<Long, byte[]> function) {
                if (function == null) {
                    return cache.get(key);
                }
                return cache.get(key, function);
            }

            @Override
            public void put(Long key, byte[] value) {
                cache.put(key, value);
            }

            @Override
            public int size() {
                return cache.size();
            }

            @Override
            public void clear() {
                cache.clear();
            }
        }

        private static final CacheGenerator DEFAULT_CACHE_GENERATOR = FileCacheImpl::new;

        private static final FileReader DEFAULT_FILE_READER = (path, offset, length) -> {
            try {
                return FsIO.toInputStream(new RandomAccessFile(path.toFile(), "r"), offset, length);
            } catch (FileNotFoundException e) {
                throw new FsIOException(e);
            }
        };

        private static final FileWriter DEFAULT_FILE_WRITER = (path, offset, length) -> {
            try {
                return FsIO.toOutputStream(new RandomAccessFile(path.toFile(), "rws"), offset, length);
            } catch (FileNotFoundException e) {
                throw new FsIOException(e);
            }
        };

        private int chunkSize = 1024 * 4;
        private int bufferSize = FsIO.IO_BUFFER_SIZE;
        private CacheGenerator cacheGenerator = DEFAULT_CACHE_GENERATOR;
        private FileReader fileReader = DEFAULT_FILE_READER;
        private FileWriter fileWriter = DEFAULT_FILE_WRITER;
        private CacheReadListener cacheReadListener = null;
        private CacheWriteListener cacheWriteListener = null;
        private FileReadListener fileReadListener = null;
        private FileWriteListener fileWriteListener = null;

        /**
         * Sets file chunk size for caching, min is 128, default is {@link FsIO#IO_BUFFER_SIZE}.
         *
         * @param chunkSize chunk size
         */
        public Builder chunkSize(int chunkSize) {
            this.chunkSize = Math.max(chunkSize, 128);
            return this;
        }

        /**
         * Sets buffer size for IO operation, min is 128, default is {@link FsIO#IO_BUFFER_SIZE}.
         *
         * @param bufferSize buffer size
         */
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = Math.max(bufferSize, 128);
            return this;
        }

        /**
         * Sets cache generator, the default generator uses {@link FsCache#softCache(FsCache.RemoveListener)}.
         *
         * @param cacheGenerator cache generator
         */
        public Builder cacheGenerator(CacheGenerator cacheGenerator) {
            this.cacheGenerator = cacheGenerator;
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
                cacheGenerator,
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
            private final CacheGenerator cacheGenerator;
            private final FileReader fileReader;
            private final FileWriter fileWriter;
            private final CacheReadListener cacheReadListener;
            private final CacheWriteListener cacheWriteListener;
            private final FileReadListener fileReadListener;
            private final FileWriteListener fileWriteListener;
            private final Map<Path, FileCache> fileCacheMap = new ConcurrentHashMap<>();

            private FsFileCacheImpl(
                int chunkSize,
                int bufferSize,
                CacheGenerator cacheGenerator,
                FileReader fileReader,
                FileWriter fileWriter,
                CacheReadListener cacheReadListener,
                CacheWriteListener cacheWriteListener,
                FileReadListener fileReadListener,
                FileWriteListener fileWriteListener
            ) {
                this.chunkSize = chunkSize;
                this.bufferSize = bufferSize;
                this.cacheGenerator = cacheGenerator;
                this.fileReader = fileReader;
                this.fileWriter = fileWriter;
                this.cacheReadListener = cacheReadListener;
                this.cacheWriteListener = cacheWriteListener;
                this.fileReadListener = fileReadListener;
                this.fileWriteListener = fileWriteListener;
            }

            @Override
            public InputStream getInputStream(Path path, long offset) {
                return new CacheInputStream(path, offset, -1);
            }

            @Override
            public OutputStream getOutputStream(Path path, long offset) {
                return null;
            }

            private final class CacheInputStream extends InputStream {

                private final Path path;
                private final long limit;
                private long pos;
                private InputStream underlying = null;

                CacheInputStream(Path path, long offset, long length) {
                    try {
                        if (length != -1) {
                            FsCheck.checkArgument(offset >= 0 && length >= 0, "offset and length must >= 0.");
                            this.limit = offset + length;
                        } else {
                            this.limit = length;
                        }
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
                        if (len == 0) {
                            return 0;
                        }
                        int result;
                        if (limit == -1) {
                            result = read0(b, off, len);
                        } else {
                            if (pos >= limit) {
                                return -1;
                            }
                            long remaining = limit - pos;
                            result = read0(b, off, (int) Math.min(len, remaining));
                        }
                        if (result == -1) {
                            if (limit != -1) {
                                pos = limit;
                            } else {
                                return -1;
                            }
                        } else {
                            pos += result;
                        }
                        return result;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private int read0(byte[] b, int off, int len) throws IOException {
                    FileCache fileCache = fileCacheMap.computeIfAbsent(path, k ->
                        cacheGenerator.generate(path, (path, key, cache) -> {
                            if (cache.size() <= 0) {
                                fileCacheMap.remove(path);
                            }
                        }));
                    int offset = off;
                    int remaining = len;
                    final LongRef posIndex = new LongRef(pos / chunkSize);
                    long posOffset = pos % chunkSize;
                    while (true) {
                        byte[] bytes = fileCache.get(posIndex.get(), k -> {
                            if (underlying == null) {
                                try {
                                    RandomAccessFile random = new RandomAccessFile(path.toFile(), "r");
                                    InputStream randomInput = FsIO.toInputStream(random, posIndex.get() * chunkSize, -1);
                                    underlying = new BufferedInputStream(randomInput, bufferSize);
                                } catch (Exception e) {
                                    throw new FsIOException(e);
                                }
                            }
                            byte[] readBytes = FsIO.readBytes(underlying, chunkSize);
                            return readBytes == null ? EOF : readBytes;
                        });
                        if (bytes.length > 0) {
                            int copySize = Math.min(remaining, bytes.length - (int) posOffset);
                            System.arraycopy(bytes, (int) posOffset, b, offset, copySize);
                            offset += copySize;
                            remaining -= copySize;
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
                    try {
                        int result;
                        if (limit != -1) {
                            if (pos >= limit) {
                                return -1;
                            }
                        }
                        result = read0();
                        if (result == -1) {
                            if (limit != -1) {
                                pos = limit;
                            } else {
                                return -1;
                            }
                        } else {
                            pos++;
                        }
                        return result;
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private int read0() throws IOException {
                    FileCache fileCache = fileCacheMap.computeIfAbsent(path, k ->
                        cacheGenerator.generate(path, (path, key, cache) -> {
                            if (cache.size() <= 0) {
                                fileCacheMap.remove(path);
                            }
                        }));
                    long posIndex = pos / chunkSize;
                    long posOffset = pos % chunkSize;
                    byte[] bytes = fileCache.get(posIndex, k -> {
                        if (underlying == null) {
                            try {
                                RandomAccessFile random = new RandomAccessFile(path.toFile(), "r");
                                InputStream randomInput = FsIO.toInputStream(random, posIndex * chunkSize, -1);
                                underlying = new BufferedInputStream(randomInput, bufferSize);
                            } catch (Exception e) {
                                throw new FsIOException(e);
                            }
                        }
                        byte[] readBytes = FsIO.readBytes(underlying, chunkSize);
                        return readBytes == null ? EOF : readBytes;
                    });
                    if (bytes.length > 0) {
                        return bytes[(int) posOffset] & 0x000000ff;
                    }
                    return -1;
                }

                @Override
                public synchronized long skip(long n) throws IOException {
                    try {
                        if (n <= 0) {
                            return 0;
                        }
                        if (limit == -1) {
                            pos += n;
                            return n;
                        }
                        long result = Math.min(n, limit - pos);
                        pos += result;
                        return result;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public synchronized int available() throws IOException {
                    if (underlying != null) {
                        return underlying.available();
                    }
                    FileCache fileCache = fileCacheMap.get(path);
                    if (fileCache == null) {
                        return 0;
                    }
                    long posIndex = pos / chunkSize;
                    long posOffset = pos % chunkSize;
                    byte[] bytes = fileCache.get(posIndex, null);
                    if (bytes == null || posOffset >= bytes.length) {
                        return 0;
                    }
                    return bytes.length - (int) posIndex;
                }

                @Override
                public void close() throws IOException {
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
                private final long limit;
                private final boolean resize;
                private long pos;
                private RandomAccessFile underlying = null;

                CacheOutputStream(Path path, long offset, long length, boolean resize) {
                    try {
                        if (length != -1) {
                            FsCheck.checkArgument(offset >= 0 && length >= 0, "offset and length must >= 0.");
                            this.limit = offset + length;
                        } else {
                            FsCheck.checkArgument(!resize, "resize must = false if limit = -1.");
                            this.limit = length;
                        }
                        this.path = path;
                        this.resize = resize;
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
                        if (limit != -1) {
                            FsCheck.checkInBounds(pos + len - 1, pos, limit);
                        }
                        write0(b, off, len);
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                private void write0(byte[] b, int off, int len) throws IOException {
                    if (underlying == null) {
                        underlying = getUnderlying();
                    }
                    FileChannel fileChannel = underlying.getChannel();
                    FileLock fileLock = fileChannel.lock(pos, len, true);
                    try {
                        if (fileLock == null || !fileLock.isValid()) {
                            throw new IOException("Lock file failed, pos: " + pos + ", len:- " + len + ".");
                        }
                        underlying.seek(pos);
                        underlying.write(b, off, len);
                        pos += len;
                        if (resize && pos >= limit) {
                            underlying.setLength(limit);
                        }
                        FileCache fileCache = fileCacheMap.get(path);
                        if (fileCache != null) {
                            fileCache.clear();
                        }
                        fileCacheMap.remove(path);
                    } finally {
                        if (fileLock != null) {
                            fileLock.release();
                        }
                    }
                }

                private RandomAccessFile getUnderlying() throws IOException {
                    return new RandomAccessFile(path.toFile(), "rws");
                }

                @Override
                public synchronized void write(int b) throws IOException {
                    write(new byte[]{(byte) b});
                }

                @Override
                public void flush() throws IOException {
                    if (underlying != null) {
                        underlying.getChannel().force(true);
                    }
                }

                @Override
                public void close() throws IOException {
                    if (underlying != null) {
                        underlying.close();
                    }
                }
            }
        }
    }
}
