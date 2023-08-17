package xyz.srclab.common.io;

import lombok.EqualsAndHashCode;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.cache.FsCache;

import java.io.*;
import java.nio.file.Path;

/**
 * Cache for file, provides methods about IO streams to read/write files between cache and underlying.
 *
 * @author fredsuvn
 * @see Builder
 */
public interface FsFileCache {

    /**
     * Returns an input stream for given file seeks from given offset.
     * <p>
     * The stream first attempts to read cached data, and if unavailable, it will read from the underlying file.
     *
     * @param file   given file
     * @param offset seek position of the file
     */
    InputStream getInputStream(File file, long offset);

    /**
     * Returns an output stream for given file seeks from given offset.
     * <p>
     * The stream will update cached data, after writing to the underlying file.
     *
     * @param file   given file
     * @param offset seek position of the file
     */
    OutputStream getOutputStream(File file, long offset);

    /**
     * Cache interface used for {@link FsFileCache}.
     */
    interface Cache<K, V> {

        /**
         * Gets cached value, may be null if expired or not found.
         *
         * @param key key of the value
         */
        @Nullable
        V get(K key);

        /**
         * Puts new value of specified key
         *
         * @param key   specified key
         * @param value new value
         */
        void put(K key, V value);
    }

    /**
     * Underlying file reader.
     */
    interface FileReader {

        /**
         * Returns an un-cached underlying file input stream to read file.
         * The start read point will be set at given offset, and readable bytes is given length.
         * The length may be set to -1 to read to end of the file.
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
     * Listener on reading from cache.
     */
    interface ReadCacheListener {

        /**
         * On read cache.
         *
         * @param path   path of file
         * @param offset offset position
         * @param length read length
         */
        void onReadCache(Path path, long offset, long length);
    }

    /**
     * Listener on reading from underlying file.
     */
    interface ReadFileListener {

        /**
         * On read cache.
         *
         * @param file   path of file
         * @param offset offset position
         * @param length read length
         */
        void onReadFile(Path file, long offset, long length);
    }

    /**
     * Listener on writing to cache.
     */
    interface WriteCacheListener {

        /**
         * On write cache.
         *
         * @param file   path of file
         * @param offset offset position
         * @param length written length
         */
        void onWriteCache(Path file, long offset, long length);
    }

    /**
     * Listener on writing to underlying file.
     */
    interface WriteFileListener {

        /**
         * On write cache.
         *
         * @param file   path of file
         * @param offset offset position
         * @param length written length
         */
        void onWriteFile(Path file, long offset, long length);
    }

    /**
     * Builder for {@link  FsFileCache}.
     */
    class Builder {

        private static final Cache<Object, Object> DEFAULT_CACHE = new Cache<Object, Object>() {

            private final FsCache<Object, Object> cache = FsCache.softCache();

            @Override
            public Object get(Object key) {
                return cache.get(key);
            }

            @Override
            public void put(Object key, Object value) {
                cache.put(key, value);
            }
        };

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

        private int chunkSize = 1024;
        private Cache<Object, Object> cache = DEFAULT_CACHE;
        private FileReader fileReader = DEFAULT_FILE_READER;
        private FileWriter fileWriter = DEFAULT_FILE_WRITER;
        private ReadCacheListener readCacheListener = null;
        private ReadFileListener readFileListener = null;
        private WriteCacheListener writeCacheListener = null;
        private WriteFileListener writeFileListener = null;

        /**
         * Sets file chunk size for caching, must be greater than or equal to 128, default is {@link FsIO#IO_BUFFER_SIZE}.
         *
         * @param chunkSize chunk size
         */
        public Builder chunkSize(int chunkSize) {
            FsCheck.checkArgument(chunkSize >= 128, "Must be greater than or equal to 128");
            this.chunkSize = chunkSize;
            return this;
        }

        /**
         * Choose whether to use cache with soft reference or weak reference, default is true (soft reference).
         * <p>
         * Note this builder will use {@link FsCache#softCache()} or {@link FsCache#weakCache()} to cache file chunks.
         *
         * @param softCache true for soft reference, false to weak reference
         */
        public Builder cache(Cache<Object, Object> cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Sets underlying file reader, default file reader uses {@link FsIO#toInputStream(RandomAccessFile, long)}.
         *
         * @param fileReader underlying file reader
         */
        public Builder fileReader(FileReader fileReader) {
            this.fileReader = fileReader;
            return this;
        }

        /**
         * Sets underlying file writer, default file writer uses {@link FsIO#toOutputStream(RandomAccessFile, long)}.
         *
         * @param fileWriter underlying file writer
         */
        public Builder fileReader(FileWriter fileWriter) {
            this.fileWriter = fileWriter;
            return this;
        }

        /**
         * Sets read cache listener, default is null.
         *
         * @param readCacheListener read cache listener
         */
        public Builder readCacheListener(@Nullable ReadCacheListener readCacheListener) {
            this.readCacheListener = readCacheListener;
            return this;
        }

        /**
         * Sets read cache listener, default is null.
         *
         * @param readFileListener read file listener
         */
        public Builder readFileListener(@Nullable ReadFileListener readFileListener) {
            this.readFileListener = readFileListener;
            return this;
        }

        /**
         * Sets read cache listener, default is null.
         *
         * @param writeCacheListener write cache listener
         */
        public Builder writeCacheListener(@Nullable WriteCacheListener writeCacheListener) {
            this.writeCacheListener = writeCacheListener;
            return this;
        }

        /**
         * Sets read cache listener, default is null.
         *
         * @param writeFileListener write file listener
         */
        public Builder writeFileListener(@Nullable WriteFileListener writeFileListener) {
            this.writeFileListener = writeFileListener;
            return this;
        }

        /**
         * Builds {@link FsFileCache}.
         */
        public FsFileCache build() {
            return new FsFileCacheImpl(
                chunkSize,
                softCache,
                fileReader,
                fileWriter,
                readCacheListener,
                readFileListener,
                writeCacheListener,
                writeFileListener
            );
        }

        private static final class FsFileCacheImpl implements FsFileCache {

            private final int chunkSize;
            private final boolean softCache;
            private final FileReader fileReader;
            private final FileWriter fileWriter;
            private final ReadCacheListener readCacheListener;
            private final ReadFileListener readFileListener;
            private final WriteCacheListener writeCacheListener;
            private final WriteFileListener writeFileListener;
            private final FsCache<FileChunkKey, byte[]> cache;

            private FsFileCacheImpl(
                int chunkSize,
                boolean softCache,
                FileReader fileReader,
                FileWriter fileWriter,
                ReadCacheListener readCacheListener,
                ReadFileListener readFileListener,
                WriteCacheListener writeCacheListener,
                WriteFileListener writeFileListener
            ) {
                this.chunkSize = chunkSize;
                this.softCache = softCache;
                this.fileReader = fileReader;
                this.fileWriter = fileWriter;
                this.readCacheListener = readCacheListener;
                this.readFileListener = readFileListener;
                this.writeCacheListener = writeCacheListener;
                this.writeFileListener = writeFileListener;
                cache = softCache ? FsCache.softCache() : FsCache.weakCache();
            }

            @Override
            public InputStream getInputStream(File file, long offset) {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return 0;
                    }
                };
            }

            @Override
            public OutputStream getOutputStream(File file, long offset) {
                return null;
            }

            private FileChunkPointer findChunkStartPointer(File file, long offset) {
                String path = file.getAbsolutePath();
                long count = offset + 1;
                long chunkIndex;
                if (count % chunkSize == 0) {
                    chunkIndex = count / chunkSize - 1;
                } else {
                    chunkIndex = count / chunkSize;
                }
                int chunkOffset = (int) (offset - chunkIndex * chunkSize);
                return new FileChunkPointer(new FileChunkKey(path, chunkIndex), chunkOffset);
            }

            private final class CachedInputStream extends InputStream {

                // private final File file;
                // private final long offset;
                private final FileChunkPointer chunkPointer;

                private InputStream underlying;

                CachedInputStream(File file, long offset) {
                    // this.file = file;
                    // this.offset = offset;
                    this.chunkPointer = findChunkStartPointer(file, offset);
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
                    if (len == 0) {
                        return 0;
                    }
                    int offset = off;
                    int rest = len;

                    // Read from cache:
                    if (underlying == null) {
                        while (true) {
                            byte[] bytes = cache.get(chunkPointer.chunkKey);
                            if (bytes == null) {
                                break;
                            }
                            int copySize = Math.min(bytes.length, rest);
                            System.arraycopy(bytes, chunkPointer.chunkOffset, b, offset, copySize);
                            rest -= copySize;
                            if (rest == 0) {
                                return len;
                            }
                            offset += copySize;
                            if (bytes.length == chunkSize) {
                                chunkPointer.next();
                            } else {
                                break;
                            }
                        }
                    }

                    if (underlying == null) {
                        underlying = fileReader.getInputStream();
                    }

                    while (true) {

                    }

                    return underlying.read(b, offset, rest);
                }

                @Override
                public int read(byte[] b) throws IOException {
                    return read(b, 0, b.length);
                }

                @Override
                public int read() throws IOException {
                    if (underlying != null) {
                        return underlying.read();
                    }
                    return random.read();
                }

                @Override
                public void close() throws IOException {
                    if (underlying != null) {
                        underlying.close();
                    }
                }
            }

            private static final class FileChunkPointer {

                private FileChunkKey chunkKey;
                private int chunkOffset;

                private FileChunkPointer(FileChunkKey chunkKey, int chunkOffset) {
                    this.chunkKey = chunkKey;
                    this.chunkOffset = chunkOffset;
                }

                public void next() {
                    chunkKey = new FileChunkKey(chunkKey.path, chunkKey.chunkIndex + 1);
                    chunkOffset = 0;
                }
            }

            @EqualsAndHashCode
            private static final class FileChunkKey {

                private final String path;
                private final long chunkIndex;

                public FileChunkKey(String path, long chunkIndex) {
                    this.path = path;
                    this.chunkIndex = chunkIndex;
                }
            }
        }
    }
}
